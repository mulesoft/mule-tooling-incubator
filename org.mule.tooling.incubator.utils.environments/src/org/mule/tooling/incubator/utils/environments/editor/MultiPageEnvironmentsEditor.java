package org.mule.tooling.incubator.utils.environments.editor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Properties;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.mule.tooling.incubator.utils.environments.model.EnvironmentsConfiguration;


public class MultiPageEnvironmentsEditor extends FormEditor {
	
	
	private EnvironmentsConfiguration envConfig;
	
	MuleEnvironmentsEditor editor;
	
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

		//this is the root node
		envConfig = new EnvironmentsConfiguration();
		
		for(File props : files) {
			if (props.getName().startsWith(fileName) && props.getName().endsWith(".properties")) {
				Properties contents = new Properties();
				
				FileInputStream fis = new FileInputStream(props);
				contents.load(fis);
				envConfig.addEnvironment(props.getName(), contents);
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
			doSaveModel(monitor);
			editor.setDirty(false);
			editor.refreshValues();
		} catch (Exception ex) {
			//TODO - display dialog
		}
		
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

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

}
