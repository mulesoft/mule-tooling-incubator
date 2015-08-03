/**
 * $Id: LicenseManager.java 10480 2007-12-19 00:47:04Z moosa $
 * --------------------------------------------------------------------------------------
 * (c) 2003-2008 MuleSource, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSource's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSource. If such an agreement is not in place, you may not use the software.
 */

package org.mule.tooling.properties.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.ide.IDEActionFactory;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.mule.tooling.properties.actions.AddPropertyAction;
import org.mule.tooling.properties.actions.DeletePropertyAction;
import org.mule.tooling.properties.utils.UIUtils;

/**
 * Manages the installation/deinstallation of global actions for multi-page
 * editors. Responsible for the redirection of global actions to the active
 * editor. Multi-page contributor replaces the contributors for the individual
 * editors in the multi-page editor.
 */
public class MultiPagePropertiesEditorContributor extends
		MultiPageEditorActionBarContributor {
	private IEditorPart activeEditorPart;
	private AddPropertyAction addPropertyAction;
	private DeletePropertyAction deletePropertyAction;
	
	private List<Action> contributedActions = new ArrayList<Action>();
	

	/**
	 * Creates a multi-page contributor.
	 */
	public MultiPagePropertiesEditorContributor() {
		super();
		createActions();
		createActionContributions();
	}

	/**
	 * Returns the action registed with the given text editor.
	 * 
	 * @return IAction or null if editor is null.
	 */
	protected IAction getAction(ITextEditor editor, String actionID) {
		return (editor == null ? null : editor.getAction(actionID));
	}

	/*
	 * (non-JavaDoc) Method declared in
	 * AbstractMultiPageEditorActionBarContributor.
	 */

	public void setActivePage(IEditorPart part) {
		if (activeEditorPart == part)
			return;

		activeEditorPart = part;

		IActionBars actionBars = getActionBars();
		if (actionBars != null) {

			ITextEditor editor = (part instanceof ITextEditor) ? (ITextEditor) part
					: null;

			actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(),
					getAction(editor, ITextEditorActionConstants.DELETE));
			actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(),
					getAction(editor, ITextEditorActionConstants.UNDO));
			actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(),
					getAction(editor, ITextEditorActionConstants.REDO));
			actionBars.setGlobalActionHandler(ActionFactory.CUT.getId(),
					getAction(editor, ITextEditorActionConstants.CUT));
			actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(),
					getAction(editor, ITextEditorActionConstants.COPY));
			actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(),
					getAction(editor, ITextEditorActionConstants.PASTE));
			actionBars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(),
					getAction(editor, ITextEditorActionConstants.SELECT_ALL));
			actionBars.setGlobalActionHandler(ActionFactory.FIND.getId(),
					getAction(editor, ITextEditorActionConstants.FIND));
			actionBars.setGlobalActionHandler(
					IDEActionFactory.BOOKMARK.getId(),
					getAction(editor, IDEActionFactory.BOOKMARK.getId()));
			actionBars.updateActionBars();
		}

		//addEncryptAction(actionBars);

		if (activeEditorPart instanceof GraphicalMulePropertiesEditor) {
			addPropertyAction.setEnabled(true);
			deletePropertyAction.setEnabled(true);
//			if (encryptAction != null)
//				encryptAction.setEnabled(true);
		} else {
			addPropertyAction.setEnabled(false);
			deletePropertyAction.setEnabled(false);
//			if (encryptAction != null)
//				encryptAction.setEnabled(false);
		}
	}

//	protected void addEncryptAction(IActionBars actionBars) {
//	
//			IToolBarManager toolBarManager = actionBars.getToolBarManager();
//			IContributionItem find = toolBarManager
//					.find(EncryptPropertyAction.ID);
//			if (find == null) {
//				toolBarManager.add(encryptAction);
//			}
//	
//	}

	private void createActions() {
		final IPropertiesEditorAccessor provider = new IPropertiesEditorAccessor() {

			@Override
			public IPropertiesEditor getPropertiesEditor() {
				return getCurrentEditor();
			}

		};
		addPropertyAction = new AddPropertyAction(provider);
		deletePropertyAction = new DeletePropertyAction(provider);
		
	}
	
	/**
	 * Create the actions contributed by others.
	 */
	public void createActionContributions() {
		contributedActions.addAll(UIUtils.getContributedToolbarButtons(this));
	}



	public void contributeToMenu(IMenuManager manager) {
		/**
		 * IMenuManager menu = new MenuManager("Editor &Menu");
		 * manager.prependToGroup(IWorkbenchActionConstants.MB_ADDITIONS, menu);
		 * menu.add(sampleAction);
		 **/
	}

	public void contributeToToolBar(IToolBarManager manager) {
		manager.add(new Separator());
		manager.add(addPropertyAction);
		manager.add(deletePropertyAction);
		// manager.add(editPropertyAction);
		
		
		for(Action action : contributedActions) {
			manager.add(action);
		}
		
	}

	@Override
	public void setActiveEditor(IEditorPart part) {
		super.setActiveEditor(part);		
	}
	
	public IPropertiesEditor getCurrentEditor() {
		return (IPropertiesEditor) (activeEditorPart instanceof IPropertiesEditor ? activeEditorPart
				: null);
	}

	public IEditorPart getActiveEditor() {
		return activeEditorPart;
	}
	
}
