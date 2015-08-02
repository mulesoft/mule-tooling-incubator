package org.mule.tooling.incubator.utils.placeholder;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.mule.tooling.incubator.utils.environments.api.EnvironmentsEditorToolbarExtension;
import org.mule.tooling.incubator.utils.environments.api.IEnvironmentsEditorContext;
import org.mule.tooling.properties.extension.PropertyKeySuggestion;

public class EnvironmentEditorToolbarCustomizer extends EnvironmentsEditorToolbarExtension {

	@Override
	protected void customizeToolbar(ToolBar toolbar,
			final IEnvironmentsEditorContext context) throws Exception {
		
		ToolItem toolItem = new ToolItem(toolbar, SWT.PUSH);
		toolItem.setText("Detect Keys");
		toolItem.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(IDE.SharedImages.IMG_OBJS_TASK_TSK));
		
		toolItem.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				PropertyKeysContributor contributor = new PropertyKeysContributor();
				List<PropertyKeySuggestion> keys = contributor.buildSuggestions(context.getCurrentConfiguration().getProject());
				for(PropertyKeySuggestion key : keys) {
					context.getCurrentConfiguration().createNewKey(key.getSuggestion(), key.getDefaultValue());
				}
				context.refreshUi();
				context.setDirty(true);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
	}

}
