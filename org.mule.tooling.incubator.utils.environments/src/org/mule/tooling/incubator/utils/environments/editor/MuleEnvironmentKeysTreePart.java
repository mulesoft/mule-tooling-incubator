package org.mule.tooling.incubator.utils.environments.editor;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.mule.tooling.incubator.utils.environments.model.PropertyKeyTreeNode;

public class MuleEnvironmentKeysTreePart extends SectionPart {
	
	private final PropertyKeyTreeNode keyModel;
	
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
		
		keysViewer = new TreeViewer(panel);
		MuleEnvironmentsTreeProvider environmentsProvider = new MuleEnvironmentsTreeProvider();
		keysViewer.setContentProvider(environmentsProvider);
		keysViewer.setLabelProvider(environmentsProvider);
		keysViewer.setInput(keyModel);
		
		toolkit.paintBordersFor(panel);
		section.setClient(panel);
	}

	
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		keysViewer.addSelectionChangedListener(listener);
	}
	
}
