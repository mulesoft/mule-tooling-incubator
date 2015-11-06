package org.mule.tooling.editor.persistance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.mule.tooling.editor.model.Namespace;

public class Utils implements INamespaceDeserializer<IEditorInput>, INamespaceSerializer<IEditorInput> {

    @Override
    public void serialize(Namespace namespace, IEditorInput ouput) {
        IFile iFile = (IFile) ouput.getAdapter(IFile.class);
        File file = null;
        if (iFile == null) {
            throw new IllegalArgumentException("Could not adapt input to IFile");
        }
        file = iFile.getLocation().toFile();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Namespace.class);
            Marshaller m = jaxbContext.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.marshal(namespace, new FileOutputStream(file));
        } catch (JAXBException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Namespace deserialize(IEditorInput input) {
        IFile fileInput = ((IFile) input.getAdapter(IFile.class));
        if (fileInput == null) {
            throw new IllegalArgumentException("Could not adapt input to IFile");
        }

        File file = fileInput.getLocation().toFile();
        JAXBContext jaxbContext;
        Namespace namspace = null;
        try {
            jaxbContext = JAXBContext.newInstance(Namespace.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            namspace = (Namespace) jaxbUnmarshaller.unmarshal(file);
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return namspace;
    }

}
