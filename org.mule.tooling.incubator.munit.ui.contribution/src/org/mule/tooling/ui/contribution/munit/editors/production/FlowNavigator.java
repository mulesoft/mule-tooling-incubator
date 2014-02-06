package org.mule.tooling.ui.contribution.munit.editors.production;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.mule.tooling.model.messageflow.Flow;
import org.mule.tooling.ui.contribution.munit.common.Filter;
import org.mule.tooling.ui.contribution.munit.common.FlowViewer;

/**
 * <p>
 * Toolbar of flows navigation for the Munit production code viewer. The toolbar redraws the selected flows path every time
 * a flow is selected {@see FlowNavigator#select(String)}. It also uses the {@link FlowViewer} to redraw the flow
 * </p> 
 */
public class FlowNavigator {
	private ToolBarManager manager;
	private FlowViewer flowViewer;

	public FlowNavigator(Composite parent, FlowViewer flowViewer)
	{
		this.flowViewer = flowViewer;
		ToolBar toolbar = new ToolBar(parent, SWT.NONE );
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(false, false).applyTo(toolbar);
		parent.layout();

		FontData fontData = new FontData();
		fontData.setStyle(SWT.BOLD);
		toolbar.setFont(new Font(toolbar.getDisplay(), fontData));
		manager = new ToolBarManager(toolbar);
	}

	/**
	 * <p>
	 * Adds a new action into the flow navigator and notifies the {@link FlowViewer} to redraw the selected flow.
	 * </p>
	 * @param flowName
	 * 		<p>The name of the flow to be navigated. If null then no action is performed.</p>
	 */
	@SuppressWarnings("unchecked")
	public void select(final String flowName)
	{
		if ( flowName == null)
			return;

		IContributionItem selectedAction = manager.find(flowName);
		if ( selectedAction != null )
		{
			removeActionsAfter(selectedAction);
		}
		else{
		    
			Action newAction = new Action(flowName, Action.AS_PUSH_BUTTON) {
				@SuppressWarnings("unchecked")
				@Override
				public void run() {
					removeActionsAfter(manager.find(this.getText()));
					manager.update(true);
					flowViewer.show(new FlowNameFilter(this.getText()));
				}
			};	

			newAction.setId(flowName);
			newAction.setText(flowName);
			manager.add(newAction);
		}
		manager.update(true);
		flowViewer.show(new FlowNameFilter(flowName));
	}

	/**
	 * <p>
	 * Disposes all the flow navigation tool, no buttons will be seen in the UI after this.
	 * </p>
	 * <p>
	 * This method also calls the {@link FlowViewer#showAll()} method so it is also refreshed.
	 * </p>
	 */
	public void refresh()
	{
		manager.removeAll();
		manager.update(true);
		flowViewer.showAll();
	}

	private void removeActionsAfter(IContributionItem selectedAction) {
		boolean startDispose = false;
		for ( IContributionItem item : manager.getItems())
		{
			if ( startDispose )
			{
				manager.remove(item);
			}

			if ( selectedAction.getId().equals(item.getId()) )
			{
				startDispose = true;
			}
		}
	}
	
	
	private class FlowNameFilter implements Filter<Flow>{

		private String name;
		
		public FlowNameFilter(String name) {
			this.name = name;
		}

		@Override
		public boolean accept(Flow flow) {
			return name.equals(flow.getName());
		}
	}
}
