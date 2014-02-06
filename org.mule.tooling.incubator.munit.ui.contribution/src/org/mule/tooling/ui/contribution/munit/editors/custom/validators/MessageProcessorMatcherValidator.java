package org.mule.tooling.ui.contribution.munit.editors.custom.validators;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Text;
import org.mule.tooling.ui.modules.core.widgets.AttributesPropertyPage;


public class MessageProcessorMatcherValidator {

    private Text messageProcessorMatcher;
    
    public MessageProcessorMatcherValidator(Text messageProcessorMatcher) {
        this.messageProcessorMatcher = messageProcessorMatcher;
    }

    public void validate(AttributesPropertyPage propertyPage){
        propertyPage.validate();
        if ( messageProcessorMatcher.getText() == null || messageProcessorMatcher.getText().length() == 0){
            propertyPage.getHost().setErrorMessage("Message processor matcher cannot be empty", IStatus.ERROR);
        }
    }
    

}
