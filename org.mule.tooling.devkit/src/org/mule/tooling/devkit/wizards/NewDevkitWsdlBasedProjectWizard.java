package org.mule.tooling.devkit.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap12.SOAP12Address;
import javax.wsdl.factory.WSDLFactory;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.mule.tooling.devkit.DevkitImages;
import org.mule.tooling.devkit.common.ConnectorMavenModel;
import org.mule.tooling.devkit.template.TemplateStringWriter;
import org.mule.tooling.devkit.template.replacer.ComponentReplacer;

public class NewDevkitWsdlBasedProjectWizard extends Wizard implements INewWizard {

    NewDevkitWsdlBasedProjectWizardPage configPage;

    public NewDevkitWsdlBasedProjectWizard() {
        super();
        this.setWindowTitle("New Anypoint Wsdl Based Connector Project");
        setNeedsProgressMonitor(true);
        this.setDefaultPageImageDescriptor(DevkitImages.getManaged("", "mulesoft-logo.png"));
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {

    }

    @Override
    public void addPages() {
        configPage = new NewDevkitWsdlBasedProjectWizardPage(new ConnectorMavenModel());
        addPage(configPage);
    }

    @Override
    public IWizardPage getNextPage(IWizardPage currentPage) {
        return null;
    }

    @Override
    public boolean performFinish() {
        final List<String> wsdlFiles = configPage.getWsdlPath();
        final ConnectorMavenModel mavenModel = getPopulatedModel();
        final IRunnableWithProgress op = new IRunnableWithProgress() {

            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                monitor.beginTask("Parsing WSDL", wsdlFiles.size() + 1);
                createWsdlConfig(wsdlFiles, monitor);
            }

        };
        if (!runInContainer(op)) {
            return false;
        }

        return true;
    }

    private ConnectorMavenModel getPopulatedModel() {
        // TODO Auto-generated method stub
        return null;
    }

    public class ServiceDefinition {

        public String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDisplay() {
            return display;
        }

        public void setDisplay(String display) {
            this.display = display;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getServicePort() {
            return servicePort;
        }

        public void setServicePort(String servicePort) {
            this.servicePort = servicePort;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String display;
        public String location;
        public String serviceName;
        public String servicePort;
        public String address;
    }

    private void createWsdlConfig(final List<String> wsdlFiles, IProgressMonitor monitor) {
        try {

            List<ServiceDefinition> deff = getServiceDefinitions(wsdlFiles, monitor);
            TemplateStringWriter template = new TemplateStringWriter(new NullProgressMonitor());
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("package", "org.mule.modules");
            map.put("strategyClassName", "WsdlConfig");
            map.put("serviceDefinitions", deff);
            monitor.worked(1);
            String value = template.apply("/templates/connector_wsdl.tmpl", new ComponentReplacer(map));
            System.out.println(value);
        } catch (WSDLException e) {
            e.printStackTrace();
        } catch (CoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private List<ServiceDefinition> getServiceDefinitions(final List<String> wsdlFiles, IProgressMonitor monitor) throws WSDLException {
        WSDLFactory factory;
        List<ServiceDefinition> deff = new ArrayList<ServiceDefinition>();
        factory = WSDLFactory.newInstance();

        ExtensionRegistry registry = factory.newPopulatedExtensionRegistry();
        javax.wsdl.xml.WSDLReader wsdlReader = factory.newWSDLReader();
        wsdlReader.setFeature("javax.wsdl.verbose", false);
        wsdlReader.setFeature("javax.wsdl.importDocuments", true);
        wsdlReader.setExtensionRegistry(registry);
        for (String wsdlFile : wsdlFiles) {
            monitor.setTaskName("Parsing: " + wsdlFile);
            javax.wsdl.Definition definition = wsdlReader.readWSDL(wsdlFile);
            Map services = definition.getAllServices();
            for (Object serviceDef : services.values()) {
                javax.wsdl.Service serviceItem = (javax.wsdl.Service) serviceDef;
                String name = serviceItem.getQName().getLocalPart();
                Map portsMap = serviceItem.getPorts();
                for (Object portDef : portsMap.values()) {
                    javax.wsdl.Port portItem = (javax.wsdl.Port) portDef;
                    String portName = portItem.getName();
                    String addressValue = getPortAddress(portItem);
                    System.out.println(name + "-" + portName + "-" + addressValue);
                    ServiceDefinition xxx = new ServiceDefinition();
                    xxx.setId(portName + "_ID");
                    xxx.setAddress(addressValue);
                    xxx.setServiceName(name);
                    xxx.setServicePort(portName);
                    xxx.setDisplay(portName);
                    xxx.setLocation(wsdlFile);
                    deff.add(xxx);
                }
            }
            monitor.worked(1);
        }
        return deff;
    }

    private String getPortAddress(javax.wsdl.Port portItem) {
        String addressValue = "";
        List extElements = portItem.getExtensibilityElements();
        for (Object element : extElements) {
            if (element instanceof SOAPAddress) {
                SOAPAddress address = (SOAPAddress) element;
                addressValue = address.getLocationURI();

            } else if (element instanceof SOAP12Address) {
                SOAP12Address address = (SOAP12Address) element;
                addressValue = address.getLocationURI();
            } else {
                throw new RuntimeException("Typo raro:" + element.getClass());
            }
        }
        return addressValue;
    }

    private boolean runInContainer(final IRunnableWithProgress work) {
        try {
            getContainer().run(true, true, work);
        } catch (InterruptedException e) {
            return false;
        } catch (InvocationTargetException e) {
            Throwable realException = e.getTargetException();
            MessageDialog.openError(getShell(), "Error", realException.getMessage());
            return false;
        }

        return true;
    }
}