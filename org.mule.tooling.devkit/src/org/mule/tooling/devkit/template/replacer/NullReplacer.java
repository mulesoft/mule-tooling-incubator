package org.mule.tooling.devkit.template.replacer;

import java.io.Reader;
import java.io.Writer;

public class NullReplacer implements Replacer {

    @Override
    public void replace(Reader reader, Writer writer) throws Exception {
        while (reader.ready()) {
            writer.write(reader.read());
        }

    }

}
