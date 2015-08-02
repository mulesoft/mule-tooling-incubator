package org.mule.tooling.incubator.utils.environments.editor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.mule.tooling.incubator.utils.environments.model.EnvironmentsConfiguration;
import org.mule.tooling.incubator.utils.environments.util.FileListResourceDeltaVisitor;


public class MultiPageEnvironmentsEditor extends FormEditor implements IResourceChangeListener {
	
	
	private EnvironmentsConfiguration envConfig;
	
	private MuleEnvironmentsEditor editor;
	
	private Set<File> watchedFiles;
	
	private boolean performingSave;
	
	public MultiPageEnvironmentsEditor() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}
	
	@Override
	protected void addPages() {
		try {
			builEnvironmentsModel();
			configureEditorTitle();
			createEnvironmentsPage();
			addAdditionalPages();
		} catch (Exception ex) {
			//generic catch-all
			ex.printStackTrace();
		}
	}

	private void builEnvironmentsModel() throws Exception {
		IFileEditorInput input = (IFileEditorInput) getEditorInput();
		
		String fileName = input.getFile().getName();
		//guess the prefix 
		fileName = guessPrefix(fileName);
		IPath dir = input.getFile().getLocation().removeLastSegments(1);
		
		File[] files = dir.toFile().listFiles();
		
		watchedFiles = new HashSet<File>();
		
		//this is the root node
		envConfig = new EnvironmentsConfiguration(fileName, fileName.endsWith(".properties"));
		
		for(File props : files) {
			if (props.getName().startsWith(fileName) && props.getName().endsWith(".properties")) {
				Properties contents = new Properties();
				FileInputStream fis = new FileInputStream(props);
				contents.load(fis);
				envConfig.addEnvironment(props.getName(), contents);
				watchedFiles.add(props);
			}
		}
		
	}

	private String guessPrefix(String fileName) {
		
		int index = fileName.lastIndexOf('-');
		if (index < 0) {
			return fileName;
		}
		return fileName.substring(0, index);
	}

	private void configureEditorTitle() {
		
		IFileEditorInput input = (IFileEditorInput) getEditorInput();
		
		setPartName("Environment Config: " + guessPrefix(input.getFile().getName()));
		
	}

	private void addAdditionalPages() {
		
	}

	private void createEnvironmentsPage() {
		try {
			editor = new MuleEnvironmentsEditor(this, "Environment Config", envConfig);
			int index = addPage(editor);
			setPageText(index, "Environments Config");
		} catch (PartInitException ex) {
			//TODO - LOG
			ex.printStackTrace();
		}		
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		try {
			performingSave = true;
			createNewFilesIfNeeded(monitor);
			doSaveModel(monitor);
			editor.setDirty(false);
			editor.refreshValues();
		} catch (Exception ex) {
			MessageDialog md = new MessageDialog(
			          Display.getDefault().getActiveShell(),
					  "Error while saving files", null, 
				      "Could not perform save, reason: " + ex.getMessage(),
				      MessageDialog.ERROR,
				      new String[] {"Ok"}, 0);
			md.open();
			
			//TODO - LOG
			ex.printStackTrace();
		} finally {
			performingSave = false;
		}
		
	}

	private void createNewFilesIfNeeded(IProgressMonitor monitor) throws IOException {
		
		IFileEditorInput input = (IFileEditorInput) getEditorInput();
		IPath dir = input.getFile().getLocation().removeLastSegments(1);
		
		for(String fn : envConfig.getNewEnvironments()) {
			IPath ne = dir.append(fn);
			File f = ne.toFile();
			if (!f.createNewFile()) {
				throw new IllegalStateException("Could not create additional environment file!");
			}
		}
		//we have taken care of creating them
		envConfig.clearNewEnvironments();
	}

	private void doSaveModel(IProgressMonitor monitor) throws Exception {
		
		IFileEditorInput input = (IFileEditorInput) getEditorInput();
		
		String fileName = input.getFile().getName();
		//guess the prefix 
		fileName = guessPrefix(fileName);
		IPath dir = input.getFile().getLocation().removeLastSegments(1);
		
		File[] files = dir.toFile().listFiles();
		
		for(File props : files) {
			
			Properties pfile = envConfig.getEnvironmentsConfiguration().get(props.getName());
			
			if (pfile == null) {
				System.out.println("TODO - need to define what to do in this situation");
				continue;
			}
			
			pfile.store(new FileOutputStream(props), "Updated by Mule Environments Editor " + new Date());
		}
		
		input.getFile().getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
	}
	
	private void refreshEditor() {
		editor.refreshConfiguration(envConfig);
	}
	
	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}
	
	@Override
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {

		FileListResourceDeltaVisitor visitor = new FileListResourceDeltaVisitor(watchedFiles);
		
		try {
			event.getDelta().accept(visitor);
			if (visitor.isFound() && !performingSave) {
				System.out.println("Reload configuration");
				builEnvironmentsModel();
				refreshEditor();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	
}
