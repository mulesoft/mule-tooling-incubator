package org.mule.tooling.ui.contribution.munit.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.mule.tooling.ui.contribution.munit.actions.RunTestAction;

public class RunMunitTestHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        String mode = RunTestAction.RUN_MODE;
        String perspective = null;
        RunTestAction.runOrdebugMunitTest(perspective, mode);

        return null;
    }

}
