
package org.mule.tooling.incubator.ws;

import org.mule.tooling.model.messageflow.MessageFlowNode;
import org.mule.tooling.model.messageflow.decorator.PropertyCollectionMap;
import org.mule.tooling.ui.modules.core.widgets.AttributesPropertyPage;
import org.mule.tooling.ui.modules.core.widgets.editors.CustomEditor;
import org.mule.tooling.ui.modules.core.widgets.editors.StringFieldEditor;
import org.mule.tooling.ui.modules.core.widgets.meta.AttributeHelper;

import javax.wsdl.Definition;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class WsdlServiceCustomEditor extends CustomEditor
{

    protected AttributesPropertyPage propertyPage;
    protected ComboViewer comboViewer;
    protected String xmlValue;

    public WsdlServiceCustomEditor(AttributesPropertyPage parentPage, AttributeHelper helper)
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
        label.setText("WSDL Service: ");
        label.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, false, false));

        final ComboViewer viewer = new ComboViewer(newComposite, SWT.READ_ONLY);
        comboViewer = viewer;
        viewer.setContentProvider(new ArrayContentProvider());
        viewer.getCombo().setEnabled(false);
        viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

        final StyledText text = (StyledText) ((StringFieldEditor) propertyPage.getEditors().get(
            "wsdlLocation")).getText();

        selectService(viewer, text.getText());

        text.addModifyListener(new ModifyListener()
        {
            @Override
            public void modifyText(ModifyEvent e)
            {
                selectService(viewer, text.getText());
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
        if (arg0.getProperties() != null && arg0.getProperties().getProperty("wsdlService") != null)
        {
            xmlValue = arg0.getProperties().getProperty("wsdlService").getValue();
            selectService(
                comboViewer,
                ((StyledText) ((StringFieldEditor) propertyPage.getEditors().get("wsdlLocation")).getText()).getText());
        }
    }

    public ComboViewer getComboViewer()
    {
        return comboViewer;
    }

    public Service getService()
    {
        String wsdlString = ((StyledText) ((StringFieldEditor) propertyPage.getEditors().get("wsdlLocation")).getText()).getText();
        if (!wsdlString.isEmpty())
        {
            Definition def;
            try
            {
                def = WSDLUtils.loadWSDL(wsdlString);
                return def.getService(new QName(def.getTargetNamespace(), comboViewer.getCombo().getText()));
            }
            catch (WSDLException e)
            {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    private void selectService(final ComboViewer viewer, final String text)
    {
        try
        {
            if (!text.isEmpty())
            {
                Definition def = WSDLUtils.loadWSDL(text);
                viewer.setInput(WSDLUtils.getServiceNames(def));
                if (def.getServices().size() == 1)
                {
                    viewer.setSelection(new StructuredSelection(WSDLUtils.getServiceNames(def)[0]));
                }
                viewer.getCombo().setEnabled(true);
            }
            else
            {
                viewer.setSelection(null);
                viewer.getCombo().setEnabled(false);
            }
        }
        catch (WSDLException e1)
        {
            viewer.setInput(null);
            viewer.setSelection(null);
            viewer.getCombo().setEnabled(false);
        }
    }

    @Override
    public void saveTo(MessageFlowNode arg0, PropertyCollectionMap arg1)
    {
        if (!comboViewer.getCombo().getText().isEmpty())
        {
            arg1.addProperty("wsdlService", comboViewer.getCombo().getText());
        }
        else
        {
            arg1.addProperty("wsdlService", xmlValue);
        }
    }

}
