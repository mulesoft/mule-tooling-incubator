package org.mule.tooling.studio.ui.editor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.actions.OpenNewClassWizardAction;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.mule.tooling.editor.annotation.ClassPicker;
import org.mule.tooling.studio.ui.widget.ComboPart;

import com.google.common.base.CaseFormat;

public abstract class AbstractBaseEditorDetailsPage implements IDetailsPage {

    protected IManagedForm mform;

    @Override
    public void initialize(IManagedForm form) {
        this.setMform(form);
    }

    protected Text createTextField(FormToolkit toolkit, Composite client, String messageId, ModifyListener listener) {
        toolkit.createLabel(client, Messages.getString(messageId));
        Text text = toolkit.createText(client, "", SWT.SINGLE);
        if ("requiredType".equals(messageId)) {
            Set<String> references = new HashSet<String>();
            // TODO Generate auto completion
            // ModuleContributionManager instance = ModuleContributionManager.instance();

            // for (MuleModule module : instance.getAllModules()) {
            // for (GlobalDefinition global : module.getGlobals()) {
            // references.add(global.getId());
            // }
            // }

            new AutoCompleteField(text, new TextContentAdapter(), references.toArray(new String[references.size()]));
        }
        GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
        gd.widthHint = 10;
        text.setLayoutData(gd);
        text.addModifyListener(listener);
        return text;
    }

    protected Text createClassPickerTextField(FormToolkit toolkit, Composite client, String messageId, ModifyListener listener, final ClassPicker classpicker) {

        toolkit.createLabel(client, Messages.getString(messageId));
        Composite container = toolkit.createComposite(client);
        GridLayout glayout = new GridLayout();
        glayout.marginWidth = glayout.marginHeight = 0;
        glayout.numColumns = 2;
        glayout.makeColumnsEqualWidth = false;
        container.setLayout(glayout);
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.widthHint = 10;
        container.setLayoutData(layoutData);
        final Text text = toolkit.createText(container, "", SWT.SINGLE); //$NON-NLS-1$
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.widthHint = 10;
        text.setLayoutData(gd);
        text.addModifyListener(listener);
        Button button = toolkit.createButton(container, "Browse", SWT.NONE);
        SelectionListener selectionListener = new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                OpenNewClassWizardAction newClassWizard = new OpenNewClassWizardAction();
                NewClassWizardPage newClassWizardPage = new NewClassWizardPage();
                newClassWizard.setConfiguredWizardPage(newClassWizardPage);

                newClassWizardPage.init(StructuredSelection.EMPTY);
                if (classpicker.mustExtend().length > 0) {
                    newClassWizardPage.setSuperClass(classpicker.mustExtend()[0].getName(), true);
                }
                List<String> interfacesNames = new ArrayList<String>();
                if (classpicker.mustImplement().length > 0) {
                    for (Class<?> clazz : classpicker.mustImplement())
                        interfacesNames.add(clazz.getName());
                }
                newClassWizardPage.setSuperInterfaces(interfacesNames, true);
                newClassWizard.setOpenEditorOnFinish(true);

                newClassWizard.run();
                if (newClassWizard.getCreatedElement() != null) {
                    try {
                        IPackageDeclaration[] packages = ((ICompilationUnit) newClassWizard.getCreatedElement().getParent()).getPackageDeclarations();
                        String packagesValue = "";
                        for (IPackageDeclaration value : packages) {
                            packagesValue = value.getElementName() + ".";
                        }
                        text.setText(packagesValue + newClassWizard.getCreatedElement().getElementName());
                    } catch (JavaModelException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }
        };
        button.addSelectionListener(selectionListener);
        gd = new GridData();
        button.setLayoutData(gd);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.widthHint = 10;
        return text;
    }

    protected Button createCheckBoxField(FormToolkit toolkit, Composite client, String messageId, SelectionListener listener) {
        Button button = toolkit.createButton(client, Messages.getString(messageId), SWT.CHECK);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
        gd.widthHint = 10;
        gd.horizontalSpan = 2;
        button.setLayoutData(gd);
        button.addSelectionListener(listener);
        return button;
    }

    protected ComboPart createEnumTextField(FormToolkit toolkit, Composite client, String messageId, Field field, SelectionListener listener) {
        toolkit.createLabel(client, Messages.getString(messageId));
        ComboPart combo = new ComboPart(client, toolkit, toolkit.getBorderStyle());

        combo.addSelectionListener(listener);
        List<String> values = new ArrayList<String>();
        for (Object value : field.getType().getEnumConstants()) {
            String stringValue = StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(

            CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, value.toString())

            ), ' ');
            values.add(stringValue);
        }
        combo.setItems(values.toArray(new String[values.size()]));
        GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
        gd.widthHint = 10;
        combo.getControl().setLayoutData(gd);
        return combo;
    }

    protected Spinner createSpinnerField(FormToolkit toolkit, Composite client, String messageId, ModifyListener listener) {
        toolkit.createLabel(client, Messages.getString(messageId));
        Spinner spinner = new Spinner(client, SWT.BORDER);
        spinner.setMinimum(0);
        spinner.setMaximum(100);
        spinner.setSelection(50);
        spinner.setIncrement(1);
        spinner.setPageIncrement(100);
        spinner.addModifyListener(listener);
        spinner.pack();
        GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
        gd.widthHint = 10;
        spinner.setLayoutData(gd);
        return spinner;
    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public void commit(boolean onSave) {
    }

    @Override
    public boolean setFormInput(Object input) {
        return false;
    }

    @Override
    public void setFocus() {
    }

    @Override
    public boolean isStale() {
        return false;
    }

    @Override
    public void refresh() {
        update();
    }

    protected abstract void update();

    @Override
    public void selectionChanged(IFormPart part, ISelection selection) {
        refresh();
    }

    @Override
    public void createContents(Composite parent) {
    }

    public IManagedForm getMform() {
        return mform;
    }

    public void setMform(IManagedForm mform) {
        this.mform = mform;
    }

    protected String getValue(String field) {
        return field == null ? "" : field;
    }

}
