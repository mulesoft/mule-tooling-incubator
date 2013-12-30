
package org.mule.tooling.incubator.ws;

import org.mule.tooling.model.messageflow.MessageFlowNode;
import org.mule.tooling.model.messageflow.decorator.PropertyCollectionMap;
import org.mule.tooling.ui.modules.core.widgets.AttributesPropertyPage;
import org.mule.tooling.ui.modules.core.widgets.editors.CustomEditor;
import org.mule.tooling.ui.modules.core.widgets.editors.StringFieldEditor;
import org.mule.tooling.ui.modules.core.widgets.meta.AttributeHelper;

import javax.wsdl.Port;
import javax.wsdl.Service;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class WsdlPortCustomEditor extends CustomEditor
{

    protected AttributesPropertyPage propertyPage;
    protected ComboViewer combo;
    protected String xmlValue;

    public ComboViewer getComboViewer()
    {
        return combo;
    }

    public WsdlPortCustomEditor(AttributesPropertyPage parentPage, AttributeHelper helper)
    {
        super(parentPage, helper);
    }

    @Override
    protected Control createControl(AttributesPropertyPage propertyPage)
    {
        this.propertyPage = propertyPage;
        final Composite newComposite = new Composite(getGroup(propertyPage), SWT.NONE);
        GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false).margins(0, 0).applyTo(newComposite);
        GridDataFactory.fillDefaults()
            .align(SWT.FILL, SWT.FILL)
            .grab(true, true)
            .span(3, 1)
            .applyTo(newComposite);

        Label label = new Label(newComposite, SWT.NONE);
        label.setText("WSDL Port: ");
        label.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, false, false));

        final ComboViewer viewer = new ComboViewer(newComposite, SWT.READ_ONLY);
        viewer.setContentProvider(new ArrayContentProvider());
        viewer.getCombo().setEnabled(false);
        viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        combo = viewer;

        final WsdlServiceCustomEditor serviceCustomEditor = (WsdlServiceCustomEditor) propertyPage.getEditors()
            .get("wsdlService");
        populatePort(viewer, serviceCustomEditor.getService());
        serviceCustomEditor.getComboViewer().addSelectionChangedListener(new ISelectionChangedListener()
        {
            @Override
            public void selectionChanged(SelectionChangedEvent event)
            {
                populatePort(viewer, serviceCustomEditor.getService());
            }

        });
        combo.addSelectionChangedListener(new ISelectionChangedListener()
        {

            @Override
            public void selectionChanged(SelectionChangedEvent event)
            {
                populateDefaultServiceAddress(getPort(), true);
            }
        });
        return newComposite;
    }

    private Group getGroup(final AttributesPropertyPage parentPage)
    {
        Group parent = null;
        for (Control control : parentPage.getChildren())
        {
            if (control instanceof Group)
            {
                Group group = (Group) control;
                if (group.getText().equalsIgnoreCase("Web Service Operation"))
                {
                    parent = group;
                    break;
                }
            }
        }
        return parent;
    }

    @Override
    public void refreshOptions()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void loadFrom(MessageFlowNode arg0, PropertyCollectionMap arg1)
    {
        if (arg0.getProperties() != null && arg0.getProperties().getProperty("wsdlPort") != null)
        {
            xmlValue = arg0.getProperties().getProperty("wsdlPort").getValue();
            if (xmlValue != null && !xmlValue.isEmpty())
            {
                combo.setSelection(new StructuredSelection(xmlValue));
            }
        }
    }

    @Override
    public void saveTo(MessageFlowNode arg0, PropertyCollectionMap arg1)
    {
        if (!combo.getCombo().getText().isEmpty())
        {
            arg1.addProperty("wsdlPort", combo.getCombo().getText());
        }
        else
        {
            arg1.addProperty("wsdlPort", xmlValue);
        }
    }

    public Port getPort()
    {
        Service service = ((WsdlServiceCustomEditor) propertyPage.getEditors().get("wsdlService")).getService();
        if (service != null)
        {
            return service.getPort(combo.getCombo().getText());

        }
        else
        {
            return null;
        }
    }

    private void populatePort(final ComboViewer viewer, final Service service)
    {
        if (service != null)
        {
            viewer.setInput(WSDLUtils.getPortNames(service));
            if (xmlValue != null)
            {
                viewer.setSelection(new StructuredSelection(xmlValue), true);
            }
            viewer.getCombo().setEnabled(true);
        }
        else
        {
            viewer.setInput(null);
            viewer.setSelection(null);
            viewer.getCombo().setEnabled(false);
        }
    }

    private void populateDefaultServiceAddress(Port port, boolean override)
    {
        final StringFieldEditor serviceAddressEditor = (StringFieldEditor) propertyPage.getEditors().get(
            "serviceAddress");
        if ((override || serviceAddressEditor.getValue() == null || serviceAddressEditor.getValue().isEmpty())
            && port != null)
        {
            serviceAddressEditor.setValue(WSDLUtils.getPortSOAPAddress(port));
        }
    }

}
