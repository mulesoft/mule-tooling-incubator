package org.mule.tooling.editor.internal.model;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class GenerateSchema extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        File baseDir = null;
        try {
            baseDir = getFileFromUser();
            schemaGenerator.saveSchemaToFile(baseDir);
        } catch (JAXBException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private File getFileFromUser() {
        // TODO Auto-generated method stub
        return null;
    }

}
