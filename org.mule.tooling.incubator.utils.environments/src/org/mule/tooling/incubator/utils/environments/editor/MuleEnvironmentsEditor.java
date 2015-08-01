package org.mule.tooling.incubator.utils.environments.editor;



import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.mule.tooling.incubator.utils.environments.model.EnvironmentsConfiguration;
import org.mule.tooling.incubator.utils.environments.model.PropertyKeyTreeNode;
import org.mule.tooling.ui.MuleImages;

public class MuleEnvironmentsEditor extends FormPage implements ISelectionChangedListener {
	
	public static final String FORM_PAGE_ID = "org.mule.tooling.environments.environmentsPage";
	
	private final PropertyKeyTreeNode rootNode;
	private final EnvironmentsConfiguration configuration;
	
	private MuleEnvironmentConfigsPart configsPart;
	private MuleEnvironmentKeysTreePart treePart;
	
	private boolean dirty = false;
	
	public MuleEnvironmentsEditor(FormEditor editor, String title, EnvironmentsConfiguration configuration) {
		super(editor, FORM_PAGE_ID, title);
		this.configuration = configuration; 
		this.rootNode = configuration.buildCombinedKeySet();
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		FormToolkit toolkit = managedForm.getToolkit();
		ScrolledForm form = managedForm.getForm();
		form.setText(getTitle());
		form.setImage(MuleImages.GLOBALS_TITLE_IMAGE);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.makeColumnsEqualWidth = true;
		
		GridData configData = new GridData(GridData.FILL_BOTH);
		configData.horizontalSpan = 2;
		
		//to be fixed
		form.getBody().setLayout(layout);
		
		//add a tree view.
		treePart = new MuleEnvironmentKeysTreePart(form.getBody(), toolkit, rootNode);
		configsPart = new MuleEnvironmentConfigsPart(form.getBody(), toolkit, configuration.elementsForKey(""));
		
		treePart.getSection().setLayoutData(new GridData(GridData.FILL_BOTH));
		configsPart.getSection().setLayoutData(configData);
		
		addListener(treePart);
		addListener(configsPart);
	}

	private void addListener(MuleEnvironmentConfigsPart configsPart) {
		configsPart.setEditor(this);
	}

	private void addListener(MuleEnvironmentKeysTreePart treePart) {
		treePart.addSelectionChangedListener(this);
	}
	
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		try {
			TreeSelection selection = (TreeSelection) event.getSelection();
			if (selection.getFirstElement() != null) {
				PropertyKeyTreeNode node = (PropertyKeyTreeNode) selection.getFirstElement();
				String completeKey = node.buildCompleteKey();
				configsPart.setCurrentConfiguration(configuration.elementsForKey(completeKey));
			} else {
				configsPart.setCurrentConfiguration(configuration.elementsForKey(""));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
		if (dirty) {
			updatePropertiesModel();
		}
		firePropertyChange(PROP_DIRTY);
		getManagedForm().dirtyStateChanged();
	}
		
	@Override
	public boolean isDirty() {
		return dirty;
	}
	
	private void updatePropertiesModel() {
		configuration.updateConfigParts(configsPart.getCurrentConfiguration());
	}
	
	public void refreshValues() {
		treePart.setKeyModel(configuration.buildCombinedKeySet());
	}

	public void addEnvironment(String resultingKey) {
		System.out.println("Called actually the method to add an environment");
	}
	
}
