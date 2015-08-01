package org.mule.tooling.incubator.utils.environments.editor;


import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.mule.tooling.incubator.utils.environments.dialogs.AddKeyDialog;
import org.mule.tooling.incubator.utils.environments.model.PropertyKeyTreeNode;

public class MuleEnvironmentKeysTreePart extends SectionPart {
	
	private PropertyKeyTreeNode keyModel;
	
	private TreeViewer keysViewer;
	
	public MuleEnvironmentKeysTreePart(Composite parent, FormToolkit toolkit, PropertyKeyTreeNode keyModel) {
		super(parent, toolkit, Section.TITLE_BAR);
		this.keyModel = keyModel;
		configurePanel(getSection(), toolkit);
	}

	private void configurePanel(Section section, FormToolkit toolkit) {
		section.setText("Environment Keys");
		
		Composite panel = toolkit.createComposite(section);
		
		panel.setLayout(new FillLayout());
		
		createTree(panel);
		
		Menu popupMenu = new Menu(keysViewer.getControl());
		
		MenuItem createKeyCommand = new MenuItem(popupMenu, SWT.NONE);
		createKeyCommand.setText("Add Key");
		createKeyCommand.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_OBJ_ADD));
		
		createKeyCommand.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeSelection selection = (TreeSelection) keysViewer.getSelection();
				
				String key = null;
				
				AddKeyDialog dialog = new AddKeyDialog(Display.getDefault().getActiveShell());
				int status = dialog.open();
				if (status == AddKeyDialog.CANCEL) {
					return;
				}
				
				String resultingKey = dialog.getResultingKey();
				
				if (StringUtils.isEmpty(resultingKey)) {
					//this should not happen
					return;
				}
				
				if (selection.getFirstElement() == null) {
					System.out.println("Completely new key");
					key = resultingKey;
				} else {
					System.out.println("Partially specified key");
					PropertyKeyTreeNode node = (PropertyKeyTreeNode) selection.getFirstElement();
					System.out.println(node.buildCompleteKey());
					key = node.buildCompleteKey() + "." + resultingKey;
				}
				keyModel.storeKey(key);
				keysViewer.refresh();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		keysViewer.getControl().setMenu(popupMenu);
		
		toolkit.paintBordersFor(panel);
		section.setClient(panel);
	}

	private void createTree(Composite panel) {
		keysViewer = new TreeViewer(panel);
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
	
	
}
