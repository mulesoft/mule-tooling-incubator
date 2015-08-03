/**
 * $Id: LicenseManager.java 10480 2007-12-19 00:47:04Z moosa $
 * --------------------------------------------------------------------------------------
 * (c) 2003-2008 MuleSource, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSource's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSource. If such an agreement is not in place, you may not use the software.
 */

package org.mule.tooling.properties.editors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;

public class MultiPagePropertiesEditor extends MultiPageEditorPart implements
		IResourceChangeListener {

	private MulePropertiesEditor textEditor;
	private GraphicalMulePropertiesEditor graphicalEditor;
	private int graphicalEditorIndex;
	
	public MultiPagePropertiesEditor() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	/**
	 * Creates page 0 of the multi-page editor, which contains a text editor.
	 */
	void createTextEditor() {
		try {
			textEditor = new MulePropertiesEditor();

			int index = addPage(textEditor, getEditorInput());
			setPageText(index, textEditor.getTitle());
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(),
					"Error creating nested text editor", null, e.getStatus());
		}
	}

	/**
	 * Creates page 1 of the multi-page editor, which allows you to change the
	 * font used in page 2.
	 */
	void createGraphicalEditor() {
		graphicalEditor = new GraphicalMulePropertiesEditor();
		addPageChangedListener(new IPageChangedListener() {
			
			@Override
			public void pageChanged(PageChangedEvent event) {
				if (event.getSelectedPage().equals(textEditor) && graphicalEditor.isDirty()){
					textEditor.getPropertiesDocument().set(graphicalEditor.getContent());
				} else if(textEditor.isDirty()) {
					graphicalEditor.setContent(textEditor.getPropertiesDocument().get());
				}
			}
		});
		
		try {
			graphicalEditorIndex = addPage(graphicalEditor, getEditorInput());
			setPageText(graphicalEditorIndex, graphicalEditor.getTitle());
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		
		
	}
	
	

	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void createPages() {
		createGraphicalEditor();
		createTextEditor();
		
		
		textEditor.updatePartControl(getEditorInput());
		graphicalEditor.updatePartControl(getEditorInput());
		
	}

	/**
	 * The <code>MultiPageEditorPart</code> implementation of this
	 * <code>IWorkbenchPart</code> method disposes all nested editors.
	 * Subclasses may extend.
	 */
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}

	/**
	 * Saves the multi-page editor's document.
	 */
	public void doSave(IProgressMonitor monitor) {
		if(getActivePage() == graphicalEditorIndex){
			textEditor.getPropertiesDocument().set(graphicalEditor.getContent());
		}else{
			graphicalEditor.setContent(textEditor.getPropertiesDocument().get());
		}
		textEditor.doSave(monitor);
		graphicalEditor.doSave(monitor);
	}

	/**
	 * Saves the multi-page editor's document as another file. Also updates the
	 * text for page 0's tab, and updates this multi-page editor's input to
	 * correspond to the nested editor's.
	 */
	public void doSaveAs() {
		if(getActivePage() == graphicalEditorIndex){
			textEditor.getPropertiesDocument().set(graphicalEditor.getContent());
		}else{
			graphicalEditor.setContent(textEditor.getPropertiesDocument().get());
		}
		textEditor.doSaveAs();
		graphicalEditor.doSaveAs();
		setPageText(0, textEditor.getTitle());
		setInput(textEditor.getEditorInput());
	}

	/*
	 * (non-Javadoc) Method declared on IEditorPart
	 */
	public void gotoMarker(IMarker marker) {
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}

	/**
	 * The <code>MultiPageEditorExample</code> implementation of this method
	 * checks that the input is an instance of <code>IFileEditorInput</code>.
	 */
	public void init(IEditorSite site, IEditorInput editorInput)
			throws PartInitException {
		if (!(editorInput instanceof IFileEditorInput))
			throw new PartInitException(
					"Invalid Input: Must be IFileEditorInput");
		super.init(site, editorInput);
		IFile file = ((IFileEditorInput)editorInput).getFile();
		setPartName(file.getName());
	}

	/*
	 * (non-Javadoc) Method declared on IEditorPart.
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}

	/**
	 * Closes all project files on project close.
	 */
	public void resourceChanged(final IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.PRE_CLOSE) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow()
							.getPages();
					for (int i = 0; i < pages.length; i++) {
						if (((FileEditorInput) textEditor.getEditorInput())
								.getFile().getProject()
								.equals(event.getResource())) {
							IEditorPart editorPart = pages[i]
									.findEditor(textEditor.getEditorInput());
							pages[i].closeEditor(editorPart, true);
						}
					}
				}
			});
		}
	}

	public GraphicalMulePropertiesEditor getGraphicalEditor() {
		return graphicalEditor;
	}

	
	
	
	

}
