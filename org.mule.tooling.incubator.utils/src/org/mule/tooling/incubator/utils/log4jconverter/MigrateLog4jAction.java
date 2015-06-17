package org.mule.tooling.incubator.utils.log4jconverter;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class MigrateLog4jAction implements IObjectActionDelegate {
	
	private IFile selectedFile;
	
	@Override
	public void run(IAction action) {
		if (selectedFile == null) {
			return;
		}
		try {
			InputStream is = selectedFile.getContents();
			Properties originalProps = new Properties();
			originalProps.load(is);
			File target = selectedFile.getParent().getLocation().append("log4j2.xml").toFile();
			FileWriter out = new FileWriter(target);
			Log4jMigrator migrator = new Log4jMigrator(originalProps);
			migrator.convert(out);
			
			
			//if everything is successful, rename the original file.
			selectedFile.move(new Path("log4j.properties.old"), true, new NullProgressMonitor());
			
			//refresh the project.
			selectedFile.getProject().refreshLocal(IProject.DEPTH_INFINITE, new NullProgressMonitor());
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		IStructuredSelection ssel = (IStructuredSelection) selection;
		selectedFile = (IFile) ssel.getFirstElement();
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub
		
	}



}
