/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.tooling.ui.contribution.munit.runner;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;
import org.mule.tooling.ui.contribution.munit.MunitPlugin;
import org.mule.tooling.ui.contribution.munit.editors.MunitMessageFlowEditor;
import org.mule.tooling.ui.contribution.munit.editors.MunitMultiPageEditor;

public class MunitTestRunnerViewPart extends ViewPart
{

    static final int REFRESH_INTERVAL = 200;
    public static final String NAME = "org.eclipse.jdt.munit.ResultView";
    static final int LAYOUT_FLAT = 0;
    static final int LAYOUT_HIERARCHICAL = 1;
	private IFile file;
    UpdateUIJob updateUIJob;
    SuiteStatus suiteStatus;
    MunitCounterPanel counterPanel;
    private Composite fCounterComposite;
    private Composite fViewerComposite;
    private int processedTests;
    private Text errorViewer;
    MunitMessageFlowEditor munitEditor;


    protected MunitProgressBar fProgressBar;
    private TreeViewer fTreeViewer;
    private boolean fIsDisposed;
    private Action reRunAction;
	private Action reDebugAction;

    public static IFile getConfigFileFromFlowFile(final IProject muleProject, final IFile flowFile) {
        String filename = flowFile.getName();
        final int index = filename.lastIndexOf('.');
        filename = filename.substring(0, index);
        filename += ".xml";

        IFile xmlFile = muleProject.getFolder(MunitPlugin.MUNIT_FOLDER_PATH).getFile(filename);

        return xmlFile;
    }
    private class UpdateUIJob extends UIJob
    {

        private boolean fRunning = true;

        public UpdateUIJob(String name)
        {
            super(name);
            setSystem(true);
        }

        public IStatus runInUIThread(IProgressMonitor monitor)
        {
            if (!fIsDisposed && MunitEclipseUpdater.getInstance().isRunning())
            {
                if (suiteStatus != null && suiteStatus.getSuitePath() != null )
                {
                    
                    fProgressBar.setMaximum(suiteStatus.getNumberOfTests());

                    if (processedTests < suiteStatus.getProcessedTests())
                    {
                        int steps = (suiteStatus.getProcessedTests() - processedTests);
                        for (int i = 0; i < steps; i++)
                        {
                            fProgressBar.step(suiteStatus.getErrors()
                                              + suiteStatus.getFailures());
                        }
                        processedTests += steps;
                    }

                    counterPanel.setTotal(suiteStatus.getNumberOfTests());
                    counterPanel
                            .setRunValue(suiteStatus.getProcessedTests(), 0);
                    counterPanel.setErrorValue(suiteStatus.getErrors());
                    counterPanel.setFailureValue(suiteStatus.getFailures());
                    
                    file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(suiteStatus.getSuitePath()));
                    fTreeViewer.setInput(suiteStatus);
                    reRunAction.setEnabled(true);
                }

            }

            schedule(REFRESH_INTERVAL);

            return Status.OK_STATUS;
        }

        public boolean shouldSchedule()
        {
            return fRunning;
        }
    }

    public void setSuiteStatus(SuiteStatus suiteStatus)
    {
        this.suiteStatus = suiteStatus;

    }

    
    public void clear()
    {
        fIsDisposed = false;
        processedTests = 0;
        counterPanel.reset();
        fProgressBar.reset();
        errorViewer.setText("");
        fTreeViewer.setInput(new SuiteStatus());
    }

    
    
    @Override
	public int getOrientation() {
		return SWT.LEFT_TO_RIGHT;
	}


	protected Composite createProgressCountPanel(Composite parent)
    {

        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        composite.setLayout(layout);
        
        counterPanel = new MunitCounterPanel(composite);
        counterPanel.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
                                                | GridData.HORIZONTAL_ALIGN_FILL));
        reRunAction = new Action("re run", Action.AS_PUSH_BUTTON ) {


			@Override
			public void run() {
				if  ( file != null ){
				    this.setEnabled(false);
				    MunitLaunchConfigurationConstants.runTest(file, "run");    
				}
				else{
				    MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Not Suite selected", "There is no Suite selected to be ran, please select a suite by right clicking on the Suite Canvas and run it"); 
				}
			}
		};
		reRunAction.setImageDescriptor(MunitPlugin.RUN_ICON_DESCRIPTOR);
		
		reDebugAction = new Action("debug", Action.AS_PUSH_BUTTON ) {
			
			
			@Override
			public void run() {
				if  ( file != null ){
					this.setEnabled(false);
					MunitLaunchConfigurationConstants.runTest(file, "debug");    
				}
				else{
					MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Not Suite selected", "There is no Suite selected to be ran, please select a suite by right clicking on the Suite Canvas and run it"); 
				}
			}
		};
		reDebugAction.setImageDescriptor(MunitPlugin.DEBUG_ICON_DESCRIPTOR);
		getViewSite().getActionBars().getToolBarManager().add(reRunAction);	
		getViewSite().getActionBars().getToolBarManager().add(reDebugAction);	
		getViewSite().getActionBars().getToolBarManager().update(true);
        fProgressBar = new MunitProgressBar(composite);
        fProgressBar.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
                                                | GridData.HORIZONTAL_ALIGN_FILL));
        return composite;
    }

   
    
    @Override
    public void createPartControl(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(composite);


        fCounterComposite = createProgressCountPanel(composite);
        fCounterComposite.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
                                                     | GridData.HORIZONTAL_ALIGN_FILL));

        fViewerComposite = createViewerComposite(composite);
        fViewerComposite.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
                                                    | GridData.HORIZONTAL_ALIGN_FILL | GridData.FILL_VERTICAL
                                                    | GridData.GRAB_VERTICAL));

        composite.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
                                          | GridData.HORIZONTAL_ALIGN_FILL));

        updateUIJob = new UpdateUIJob("Munit Job");
        updateUIJob.schedule(REFRESH_INTERVAL);
    }

    private Composite createViewerComposite(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.NONE);

        FillLayout layout = new FillLayout();
        layout.type = SWT.VERTICAL;
        layout.spacing = 20;
        
        
        composite.setLayout(layout);
        
        fTreeViewer = buildTreeViewer(composite);
        
        errorViewer = new Text(composite, SWT.MULTI | SWT.V_SCROLL
                | SWT.H_SCROLL | SWT.READ_ONLY);


        return composite;
    }

    private TreeViewer buildTreeViewer(Composite composite)
    {
        TreeViewer tree = new TreeViewer(composite, SWT.V_SCROLL | SWT.SINGLE);
        
        tree.addDoubleClickListener(new IDoubleClickListener()
        {

            @Override
            public void doubleClick(DoubleClickEvent event)
            {
                
                IFile input = getConfigFileFromFlowFile(file.getProject(), file);
                try
                {
                    IWorkbenchPage activePage = PlatformUI
                            .getWorkbench()
                            .getActiveWorkbenchWindow()
                            .getActivePage();

                    IDE.openEditor(activePage,  input , "org.mule.tooling.ui.contribution.munit.editors.MunitMultiPageEditor", true);
    				
    				if  ( activePage.getActiveEditor() instanceof MunitMultiPageEditor ){
    					MunitMultiPageEditor munitPage = (MunitMultiPageEditor) activePage.getActiveEditor();
    					MunitMessageFlowEditor flowEditor = (MunitMessageFlowEditor) munitPage.getFlowEditor();
    					flowEditor.selectFlowByName(getSelectedStatus(event).getTestName());
    				}

                }
                catch (PartInitException e)
                {

                    MessageDialog
                            .openError(PlatformUI.getWorkbench()
                                               .getActiveWorkbenchWindow().getShell(), "",
                                       "Error");
                }
             

            }
        });
        tree.setLabelProvider(new ILabelProvider()
        {

            @Override
            public void addListener(ILabelProviderListener listener)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void dispose()
            {
                // TODO Auto-generated method stub

            }

            @Override
            public boolean isLabelProperty(Object element, String property)
            {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void removeListener(ILabelProviderListener listener)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public Image getImage(Object element)
            {

                if (element instanceof TestStatus)
                {
                    TestStatus testStatus = (TestStatus) element;
                    if (testStatus.isFinished())
                    {
                        if (testStatus.hasError())
                        {
                            return MunitPlugin.getImageDescriptor(
                                    "/studio16x16-error.png").createImage();
                        }
                        else if (testStatus.hasFailed())
                        {
                            return MunitPlugin.getImageDescriptor(
                                    "/studio16x16-failed.png").createImage();
                        }

                        return MunitPlugin.getImageDescriptor(
                                "/studio16x16-ok.png").createImage();
                    }

                }

                return MunitPlugin.getImageDescriptor("/studio16x16.png")
                        .createImage();
            }

            @Override
            public String getText(Object element)
            {
                if (element instanceof TestStatus)
                {
                    return ((TestStatus) element).getTestName();
                }
                else if (element instanceof SuiteStatus)
                {
                    return ((SuiteStatus) element).getName();
                }
                return null;
            }

        });
        tree.setContentProvider(new ITreeContentProvider()
        {

            @Override
            public void inputChanged(Viewer viewer, Object oldInput,
                                     Object newInput)
            {
                SuiteStatus status = (SuiteStatus) newInput;
                if ( status.getNumberOfTests() > 0){
                    reRunAction.setEnabled(true);
                    reDebugAction.setEnabled(true);
                }
                else{
                    reRunAction.setEnabled(false);
                    reDebugAction.setEnabled(false);
                }
            }

            @Override
            public void dispose()
            {
                // TODO Auto-generated method stub

            }

            @Override
            public Object[] getElements(Object inputElement)
            {
                if (inputElement instanceof SuiteStatus)
                {
                    return ((SuiteStatus) inputElement).getTests().toArray();
                }
                return null;
            }

            @Override
            public Object[] getChildren(Object parentElement)
            {
                return getElements(parentElement);
            }

            @Override
            public Object getParent(Object element)
            {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public boolean hasChildren(Object element)
            {
                // TODO Auto-generated method stub
                return false;
            }
        });

        tree.addSelectionChangedListener(new ISelectionChangedListener()
        {
            public void selectionChanged(SelectionChangedEvent event)
            {
                // if the selection is empty clear the label
                if (event.getSelection().isEmpty())
                {
                    return;
                }
                if (event.getSelection() instanceof TreeSelection)
                {
                    Object firstElement = ((TreeSelection) event.getSelection())
                            .getFirstElement();
                    if (firstElement instanceof TestStatus)
                    {
                        TestStatus testStatus = (TestStatus) firstElement;
                        String cause = testStatus.getCause();
                        if (cause != null)
                        {
                            errorViewer.setText(cause);
                        }
                        else
                        {
                            errorViewer.setText("");
                        }

                    }

                }
            }

        });
        return tree;
    }

    public TestStatus getSelectedStatus(DoubleClickEvent event)
    {
        Object firstElement = ((TreeSelection) event.getSelection())
                .getFirstElement();
        if (firstElement instanceof TestStatus)
        {
            return (TestStatus) firstElement;
        }

        return null;
    }

    @Override
    public void setFocus()
    {
        counterPanel.setFocus();
    }

    public synchronized void dispose()
    {
        fIsDisposed = true;


    }

}
