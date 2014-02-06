package org.mule.tooling.ui.contribution.munit.extensions;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.mule.tooling.messageflow.editor.IMessageFlowNodeContextMenuProvider;
import org.mule.tooling.model.messageflow.MessageFlowNode;
import org.mule.tooling.ui.contribution.debugger.utils.IDebuggerConstants;
import org.mule.tooling.ui.contribution.munit.MunitPlugin;
import org.mule.tooling.ui.contribution.munit.actions.RunTestAction;
import org.mule.tooling.ui.contribution.munit.editors.MunitMultiPageEditor;


public class RunTestExtension implements IMessageFlowNodeContextMenuProvider {

    @Override
    public void addActionsForNode(IMenuManager menu, MessageFlowNode selected) {
    	if ( comesFromRightEditor(menu) )
		{
    		menu.add( new Separator("Test"));
			MenuManager wrapInMenu = new MenuManager("Munit", "Munit");
	        menu.appendToGroup("Test", wrapInMenu);
	        wrapInMenu.add(new RunTestAction(selected, "Run Suite", "run", null, MunitPlugin.RUN_ICON_DESCRIPTOR));
	        wrapInMenu.add(new RunTestAction(selected, "Debug Suite", "debug", IDebuggerConstants.DEBUG_PERSPECTIVE_ID, MunitPlugin.DEBUG_ICON_DESCRIPTOR));
		}
    }

    protected boolean comesFromRightEditor(IMenuManager menu) {
        IWorkbenchPage activePage = PlatformUI
                .getWorkbench()
                .getActiveWorkbenchWindow()
                .getActivePage();
        if (activePage != null ){
            return (activePage.getActiveEditor() instanceof MunitMultiPageEditor);
        }
        
        return false;
    }

 
}