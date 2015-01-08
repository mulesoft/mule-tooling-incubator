package org.mule.tooling.ui.contribution.munit.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.mule.tooling.ui.contribution.debugger.utils.IDebuggerConstants;
import org.mule.tooling.ui.contribution.munit.actions.RunTestAction;

public class DebugMunitTestHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        String mode = RunTestAction.DEBUG_MODE;
        String perspective = IDebuggerConstants.DEBUG_PERSPECTIVE_ID;
        RunTestAction.runOrdebugMunitTest(perspective, mode);

        return null;
    }

}
