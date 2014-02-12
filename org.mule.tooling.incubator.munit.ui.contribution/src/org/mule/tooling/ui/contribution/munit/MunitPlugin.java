package org.mule.tooling.ui.contribution.munit;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mule.tooling.core.event.EventBus;
import org.mule.tooling.ui.contribution.debugger.service.MuleDebuggerService;
import org.mule.tooling.ui.contribution.munit.runner.MunitEclipseUpdater;
import org.mule.tooling.ui.contribution.munit.runner.MunitModel;
import org.mule.tooling.ui.contribution.munit.runner.MunitTestRunnerViewPart;
import org.mule.tooling.ui.contribution.munit.runner.SuiteStatus;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class MunitPlugin extends AbstractUIPlugin {


	public static final ImageDescriptor TEST_ICON_DESCRIPTOR = AbstractUIPlugin.imageDescriptorFromPlugin(MunitPlugin.PLUGIN_ID, "/icons/Suite.png");
	public static final ImageDescriptor CHECK_ICON_DESCRIPTOR = AbstractUIPlugin.imageDescriptorFromPlugin(MunitPlugin.PLUGIN_ID, "/icons/check.png");
	public static final ImageDescriptor PROD_ICON_DESCRIPTOR = AbstractUIPlugin.imageDescriptorFromPlugin(MunitPlugin.PLUGIN_ID, "/icons/Prod.png");
	public static final ImageDescriptor ZOOM_ICON_DESCRIPTOR = AbstractUIPlugin.imageDescriptorFromPlugin(MunitPlugin.PLUGIN_ID, "/icons/Zoom_in.png");
	public static final ImageDescriptor RUN_ICON_DESCRIPTOR = AbstractUIPlugin.imageDescriptorFromPlugin(DebugUIPlugin.getUniqueIdentifier(), "$nl$/icons/full/etool16/run_exc.gif");
	public static final ImageDescriptor DEBUG_ICON_DESCRIPTOR = AbstractUIPlugin.imageDescriptorFromPlugin(DebugUIPlugin.getUniqueIdentifier(), "$nl$/icons/full/eview16/debug_persp.gif");
	public static final ImageDescriptor STEP_ICON_DESCRIPTOR = AbstractUIPlugin.imageDescriptorFromPlugin(MunitPlugin.PLUGIN_ID, "/icons/Step.png");

	public static final String FLOW_REF_TYPE = "http://www.mulesoft.org/schema/mule/core/flow-ref";
	public static final String SUB_FLOW_TYPE = "http://www.mulesoft.org/schema/mule/core/subflow";
	public static final String FLOW_TYPE = "http://www.mulesoft.org/schema/mule/core/flow";
	public static final String TEST_TYPE = "http://www.mulesoft.org/schema/mule/munit/test";
	
	   public static String MUNIT_FOLDER_PATH = "src/test/munit";

	
	private static EventBus eventBus = new EventBus();
	
	public static EventBus getEventBus(){
		return eventBus;
	}
	

	private static MunitPlugin fgPlugin = null;
	private final MunitModel munitModel = new MunitModel();

	public static final String PLUGIN_ID = "org.mule.tooling.ui.contribution.munit"; 
	private static final IPath ICONS_PATH = new Path("$nl$/icons"); 

	public MunitPlugin()
	{
		fgPlugin = this;
		MuleDebuggerService.getDefault().start();
	}

	public static MunitPlugin getDefault()
	{
		return fgPlugin;
	}

	public static Shell getActiveWorkbenchShell()
	{
		IWorkbenchWindow workBenchWindow = getActiveWorkbenchWindow();
		if (workBenchWindow == null)
		{
			return null;
		}
		return workBenchWindow.getShell();
	}

	public static IWorkbenchWindow getActiveWorkbenchWindow()
	{
		if (fgPlugin == null)
		{
			return null;
		}
		IWorkbench workBench = fgPlugin.getWorkbench();
		if (workBench == null)
		{
			return null;
		}
		return workBench.getActiveWorkbenchWindow();
	}

	public static IWorkbenchPage getActivePage()
	{
		IWorkbenchWindow activeWorkbenchWindow = getActiveWorkbenchWindow();
		if (activeWorkbenchWindow == null)
		{
			return null;
		}
		return activeWorkbenchWindow.getActivePage();
	}

	public static String getPluginId()
	{
		return PLUGIN_ID;
	}

	public static void log(Throwable e)
	{
		log(new Status(IStatus.ERROR, getPluginId(), IStatus.ERROR, "Error", e)); 
	}

	public static void log(IStatus status)
	{
		getDefault().getLog().log(status);
	}

	public static ImageDescriptor getImageDescriptor(String relativePath)
	{
		IPath path = ICONS_PATH.append(relativePath);
		return createImageDescriptor(getDefault().getBundle(), path, true);
	}


	private static ImageDescriptor createImageDescriptor(Bundle bundle, IPath path, boolean useMissingImageDescriptor)
	{
		URL url = FileLocator.find(bundle, path, null);
		if (url != null)
		{
			return ImageDescriptor.createFromURL(url);
		}
		if (useMissingImageDescriptor)
		{
			return ImageDescriptor.getMissingImageDescriptor();
		}
		return null;
	}

	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		munitModel.start();
	}

	public static void asyncShowTestRunnerViewPart()
	{
		final SuiteStatus suiteStatus = new SuiteStatus();
		getDisplay().syncExec(new Runnable()
		{
			public void run()
			{
				showTestRunnerViewPartInActivePage(suiteStatus);
			}
		});
	}

	public static ViewPart showTestRunnerViewPartInActivePage(SuiteStatus suiteStatus)
	{
		try
		{
			IWorkbenchPage page = MunitPlugin.getActivePage();
			if (page == null)
			{
				return null;
			}
			MunitTestRunnerViewPart view = (MunitTestRunnerViewPart) page.findView(MunitTestRunnerViewPart.NAME);
			if (view == null)
			{
				// create and show the result view if it isn't created yet.
				MunitTestRunnerViewPart showView = (MunitTestRunnerViewPart) page.showView(MunitTestRunnerViewPart.NAME, null, IWorkbenchPage.VIEW_VISIBLE);
				showView.setSuiteStatus(suiteStatus);
				showView.clear();
				MunitEclipseUpdater.getInstance().setSuiteStatus(suiteStatus);
				return (ViewPart) showView;
			}
			else
			{
				view.clear();
				view.setSuiteStatus(suiteStatus);
				MunitEclipseUpdater.getInstance().setSuiteStatus(suiteStatus);
				return view;
			}
		}
		catch (PartInitException pie)
		{
			MunitPlugin.log(pie);
			return null;
		}
	}

	private static Display getDisplay()
	{

		Display display = Display.getCurrent();
		if (display == null)
		{
			display = Display.getDefault();
		}
		return display;
	}
}
