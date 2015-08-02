package org.mule.tooling.incubator.utils.environments.editor;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.mule.tooling.incubator.utils.environments.dialogs.AddKeyDialog;
import org.mule.tooling.incubator.utils.environments.model.EnvironmentsConfiguration;
import org.mule.tooling.incubator.utils.environments.model.PropertyKeyTreeNode;

public class MuleEnvironmentKeysTreePart extends SectionPart {
	
	private PropertyKeyTreeNode keyModel;
	
	private TreeViewer keysViewer;
	private ToolItem deleteKeyItem;
	private EnvironmentsConfiguration configuration;
	private MuleEnvironmentsEditor editor;
	
	public MuleEnvironmentKeysTreePart(Composite parent, FormToolkit toolkit, EnvironmentsConfiguration configuration, MuleEnvironmentsEditor editor) {
		super(parent, toolkit, Section.TITLE_BAR);
		this.keyModel = configuration.buildCombinedKeySet();
		this.configuration = configuration;
		this.editor = editor;
		configurePanel(getSection(), toolkit);
	}

	private void configurePanel(Section section, FormToolkit toolkit) {
		section.setText("Environment Keys");
		
		Composite panel = toolkit.createComposite(section);
		
		Composite toolBarPanel = toolkit.createComposite(panel);
		Composite treePanel = toolkit.createComposite(panel);
		
		GridLayoutFactory.swtDefaults().equalWidth(true).applyTo(panel);
		panel.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		toolBarPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		treePanel.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		toolBarPanel.setLayout(new RowLayout());
		treePanel.setLayout(new FillLayout());
		
		createToolbar(toolBarPanel, toolkit);
		createTree(treePanel);
		
		createPopupMenu();
		
		toolkit.paintBordersFor(panel);
		section.setClient(panel);
	}

	private void createToolbar(Composite panel, FormToolkit toolkit) {
		ToolBar toolbar = new ToolBar(panel, SWT.HORIZONTAL | SWT.RIGHT);
		
		
		ToolItem addKeyItem = new ToolItem(toolbar, SWT.PUSH);
		addKeyItem.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_OBJ_ADD));
		addKeyItem.setText("New Key");
		addKeyItem.setToolTipText("Add new top-level key");
		addKeyItem.addSelectionListener(new AddNodeSelectionListener());
		
		deleteKeyItem = new ToolItem(toolbar, SWT.PUSH);
		deleteKeyItem.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_ETOOL_DELETE));
		deleteKeyItem.setText("Delete Key");
		deleteKeyItem.setToolTipText("Delete Selected Key");
		deleteKeyItem.addSelectionListener(new DeleteNodeSelectionListener());
		
	}

	private void createPopupMenu() {
		Menu popupMenu = new Menu(keysViewer.getControl());
		
		MenuItem createKeyCommand = new MenuItem(popupMenu, SWT.NONE);
		createKeyCommand.setText("Add Key");
		createKeyCommand.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_OBJ_ADD));
		
		createKeyCommand.addSelectionListener(new AddNodeSelectionListener());
		
		MenuItem deleteKeyCommand = new MenuItem(popupMenu, SWT.NONE);
		deleteKeyCommand.setText("Remove Key");
		deleteKeyCommand.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_ETOOL_DELETE));
		deleteKeyCommand.addSelectionListener(new DeleteNodeSelectionListener());
		
		keysViewer.getControl().setMenu(popupMenu);
	}

	private void createTree(Composite panel) {
		
		FilteredTree ft = new FilteredTree(panel, SWT.BORDER, new PropertyKeyFilter(), true);		
		keysViewer = ft.getViewer();
		MuleEnvironmentsTreeProvider environmentsProvider = new MuleEnvironmentsTreeProvider();
		keysViewer.setContentProvider(environmentsProvider);
		keysViewer.setLabelProvider(environmentsProvider);
		keysViewer.setInput(keyModel);
	}

	
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		keysViewer.addSelectionChangedListener(listener);
	}

	public void setKeyModel(PropertyKeyTreeNode keyModel) {
		this.keyModel = keyModel;
		keysViewer.setInput(keyModel);
	}

	public String getSelectedKey() {
		TreeSelection selection = (TreeSelection) keysViewer.getSelection();
		
		if (selection.getFirstElement() == null) {
			return "";
		}
		
		PropertyKeyTreeNode node = (PropertyKeyTreeNode) selection.getFirstElement();		
		return node.buildCompleteKey();
	}
	
	public void selectKey(String key) {
		PropertyKeyTreeNode node = keyModel.findItem(key);
		if (node == null) {
			return;
		}
		
		keysViewer.setSelection(new StructuredSelection(node));
	}
	
	/**
	 * Listener for deleting a particular node or set of nodes.
	 * @author juancavallotti
	 *
	 */
	private class DeleteNodeSelectionListener implements SelectionListener {

		@Override
		public void widgetSelected(SelectionEvent e) {
			TreeSelection selection = (TreeSelection) keysViewer.getSelection();
			if (selection.getFirstElement() == null) {
				return; //no node selected
			}
			
			PropertyKeyTreeNode node = (PropertyKeyTreeNode) selection.getFirstElement();
			configuration.deleteKeys(node);
			setKeyModel(configuration.buildCombinedKeySet());
			keysViewer.refresh();
			editor.setDirty(true);
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			
		}
		
	}
	
	private class AddNodeSelectionListener implements SelectionListener {

		@Override
		public void widgetSelected(SelectionEvent e) {
			TreeSelection selection = (TreeSelection) keysViewer.getSelection();
			
			String prefix = null;
			
			if (selection.getFirstElement() != null) {
				PropertyKeyTreeNode node = (PropertyKeyTreeNode) selection.getFirstElement();
				prefix = node.buildCompleteKey();
			}
			
			AddKeyDialog dialog = new AddKeyDialog(Display.getDefault().getActiveShell(), prefix);
			int status = dialog.open();
			if (status == AddKeyDialog.CANCEL) {
				return;
			}
			
			String resultingKey = dialog.getResultingKey();
			
			if (StringUtils.isEmpty(resultingKey)) {
				//this should not happen
				return;
			}
			
			//create the key
			configuration.createNewKey(resultingKey);
			
			//update the system
			setKeyModel(configuration.buildCombinedKeySet());
			selectKey(resultingKey);
			
			editor.setDirty(true);
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			
		}
		
	}
	
	private static class PropertyKeyFilter extends PatternFilter {
		@Override
		protected boolean isLeafMatch(Viewer viewer, Object element) {
			PropertyKeyTreeNode node = (PropertyKeyTreeNode) element;
			return wordMatches(node.buildCompleteKey());
		}
	}
	
}
