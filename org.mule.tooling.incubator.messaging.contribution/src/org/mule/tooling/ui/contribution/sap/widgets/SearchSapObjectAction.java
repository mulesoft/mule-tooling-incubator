package org.mule.tooling.ui.contribution.sap.widgets;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.mule.tooling.ui.contribution.sap.widgets.meta.SearchSapObjectDialog;
import org.mule.tooling.ui.modules.core.widgets.AttributesPropertyPage;
import org.mule.tooling.ui.modules.core.widgets.IFieldEditor;
import org.mule.tooling.ui.modules.core.widgets.editors.AbstractFieldEditor;

public class SearchSapObjectAction extends BaseSapAction {

	@Override
	public void click(AttributesPropertyPage page) {
	    SearchSapObjectDialog dialog = new SearchSapObjectDialog(page.getParent().getShell(), page);
	    dialog.setBlockOnOpen(true);
	    boolean isCancelled = dialog.open() == IDialogConstants.CANCEL_ID;
	    
	    if (!isCancelled) {
	        IFieldEditor sapObjectEditor = page.getEditorsForPage().get("functionName");
	        String selectedSapObjectName = dialog.getSelectedSapObjectName();
	        if (StringUtils.isNotBlank(selectedSapObjectName)) {
	            ((AbstractFieldEditor) sapObjectEditor).setValue(selectedSapObjectName);
	        }
	    }
	}
	
}
