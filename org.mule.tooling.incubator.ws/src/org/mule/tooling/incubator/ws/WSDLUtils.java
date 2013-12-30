
package org.mule.tooling.incubator.ws;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.http.HTTPAddress;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.extensions.schema.SchemaReference;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap12.SOAP12Address;
import javax.xml.namespace.QName;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class WSDLUtils
{

    public static Definition loadWSDL(String wsdlLocation) throws WSDLException
    {
        javax.wsdl.xml.WSDLReader wsdlReader11;
        wsdlReader11 = javax.wsdl.factory.WSDLFactory.newInstance().newWSDLReader();
        return wsdlReader11.readWSDL(wsdlLocation);
    }

    public static List<String> getSchemas(Definition wsdlDefinition)
        throws TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException
    {

        Map<String, String> wsdlNamespaces = wsdlDefinition.getNamespaces();

        List<String> schemas = new ArrayList<String>();
        for (Object o : wsdlDefinition.getTypes().getExtensibilityElements())
        {
            if (o instanceof javax.wsdl.extensions.schema.Schema)
            {
                Schema schema = (Schema) o;
                for (Map.Entry<String, String> entry : wsdlNamespaces.entrySet())
                {
                    if (!schema.getElement().hasAttribute("xmlns:" + entry.getKey()))
                    {
                        schema.getElement().setAttribute("xmlns:" + entry.getKey(), entry.getValue());
                    }
                }
                schemas.add(schemaToString(schema));

                for (Object location : schema.getIncludes())
                {
                    schemas.add(schemaToString(((SchemaReference) location).getReferencedSchema()));
                }
            }
        }
        return schemas;
    }

    private static String schemaToString(Schema schema)
        throws TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException
    {
        StringWriter writer = new StringWriter();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(new DOMSource(schema.getElement()), new StreamResult(writer));
        return writer.toString();
    }

    public static String[] getServiceNames(Definition wsdlDefinition)
    {
        Map<QName, Service> services = wsdlDefinition.getServices();
        List<String> serviceNames = new ArrayList<String>();
        for (Object name : services.keySet().toArray())
        {
            serviceNames.add(((QName) name).getLocalPart());
        }
        return serviceNames.toArray(new String[serviceNames.size()]);
    }

    public static String[] getPortNames(Service service)
    {
        Set portKeys = service.getPorts().keySet();
        List<String> ports = new ArrayList<String>();
        Iterator<String> portsIterator = portKeys.iterator();
        while (portsIterator.hasNext())
        {
            ports.add(portsIterator.next());
        }
        return (String[]) ports.toArray(new String[ports.size()]);
    }

    public static String getPortSOAPAddress(Port port)
    {
        ExtensibilityElement address = (ExtensibilityElement) port.getExtensibilityElements().get(0);
        if (address instanceof SOAPAddress)
        {
            return ((SOAPAddress) address).getLocationURI();
        }
        else if (address instanceof SOAP12Address)
        {
            return ((SOAP12Address) address).getLocationURI();
        }
        else if (address instanceof HTTPAddress)
        {
            return ((HTTPAddress) address).getLocationURI();
        }
        else
        {
            return null;
        }
    }

    public static String[] getOperationNames(Port port)
    {
        List<String> operationNames = new ArrayList<String>();
        for (BindingOperation operation : (List<BindingOperation>) port.getBinding().getBindingOperations())
        {
            operationNames.add(operation.getName());
        }
        return (String[]) operationNames.toArray(new String[operationNames.size()]);
    }

}
