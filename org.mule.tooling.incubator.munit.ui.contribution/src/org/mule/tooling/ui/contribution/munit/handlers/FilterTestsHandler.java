package org.mule.tooling.ui.contribution.munit.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ToolItem;
import org.mule.tooling.core.MuleCorePlugin;
import org.mule.tooling.messageflow.events.RefreshRequestedEvent;
import org.mule.tooling.ui.contribution.munit.editors.MunitMessageFlowEditor;


public class FilterTestsHandler extends AbstractHandler{

    @Override
    public Object execute(ExecutionEvent arg0) throws ExecutionException {
        MunitMessageFlowEditor.showTestsOnly(((ToolItem) ((Event) arg0.getTrigger()).widget).getSelection());
        MuleCorePlugin.getEventBus().fireEvent(new RefreshRequestedEvent());
        
        return null;
    }
}