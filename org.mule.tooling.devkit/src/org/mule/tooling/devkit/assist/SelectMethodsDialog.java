package org.mule.tooling.devkit.assist;

import java.util.ArrayList;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.mule.tooling.devkit.ASTUtils;

public class SelectMethodsDialog extends TitleAreaDialog {

	private Text txtFirstName;
	private List multi;
	private java.util.List<Integer> indexes;
	private java.util.List<MethodDeclaration> methods;
	private String firstName;
	private ICompilationUnit compilationUnit;

	public SelectMethodsDialog(Shell parentShell,
			ICompilationUnit compilationUnit) {
		super(parentShell);
		this.compilationUnit = compilationUnit;
		indexes = new ArrayList<Integer>();
	}

	@Override
	public void create() {
		super.create();
		setTitle("Select new name");
		setMessage("New name", IMessageProvider.INFORMATION);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(2, false);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayout(layout);

		createFirstName(container);
		createLastName(container);

		return area;
	}

	private void createFirstName(Composite container) {
		Label lbtFirstName = new Label(container, SWT.NONE);
		lbtFirstName.setText("Connector Name:");

		GridData dataFirstName = new GridData();
		dataFirstName.grabExcessHorizontalSpace = true;
		dataFirstName.horizontalAlignment = GridData.FILL;

		txtFirstName = new Text(container, SWT.BORDER);
		txtFirstName.setLayoutData(dataFirstName);
		txtFirstName.setText(compilationUnit.getElementName().toLowerCase());
	}

	private void createLastName(Composite container) {
		Label lbtLastName = new Label(container, SWT.NONE);
		lbtLastName.setText("Methods:");

		GridData dataLastName = new GridData();
		dataLastName.grabExcessHorizontalSpace = true;
		dataLastName.horizontalAlignment = GridData.FILL;
		dataLastName.verticalAlignment = GridData.FILL;
		multi = new List(container, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		CompilationUnit unit = ASTUtils.parse(compilationUnit);
		MethodVisitor visitor = new MethodVisitor();
		unit.accept(visitor);
		methods = visitor.getMethods();
		for (MethodDeclaration method : methods) {
			multi.add(method.toString());
		}
		multi.setLayoutData(dataLastName);
		multi.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				indexes.clear();
				int[] selectedItems = multi.getSelectionIndices();
				for (int loopIndex = 0; loopIndex < selectedItems.length; loopIndex++) {
					indexes.add(selectedItems[loopIndex]);
				}
			}

			public void widgetDefaultSelected(SelectionEvent event) {
				indexes.clear();
				int[] selectedItems = multi.getSelectionIndices();
				for (int loopIndex = 0; loopIndex < selectedItems.length; loopIndex++) {
					indexes.add(selectedItems[loopIndex]);
				}
			}
		});
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	// save content of the Text fields because they get disposed
	// as soon as the Dialog closes
	private void saveInput() {
		firstName = txtFirstName.getText();

	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	public String getFirstName() {
		return firstName;
	}

	public java.util.List<MethodDeclaration> getSelectedMethods() {

		java.util.List<MethodDeclaration> selectedMethods = new ArrayList<MethodDeclaration>();
		for (int index : indexes) {
			selectedMethods.add(methods.get(index));
		}
		return selectedMethods;
	}

}