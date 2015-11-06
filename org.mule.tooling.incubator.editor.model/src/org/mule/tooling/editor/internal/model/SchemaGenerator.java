package org.mule.tooling.editor.internal.model;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.mule.tooling.editor.model.Namespace;


public class SchemaGenerator {

    public void saveSchemaToFile(final File baseDir) throws JAXBException, IOException {
        class MySchemaOutputResolver extends SchemaOutputResolver {
            public Result createOutput( String namespaceUri, String suggestedFileName ) throws IOException {
                return new StreamResult(new File(baseDir,suggestedFileName));
            }
        }

        JAXBContext context = JAXBContext.newInstance(Namespace.class);
        context.generateSchema(new MySchemaOutputResolver());
    }
}
