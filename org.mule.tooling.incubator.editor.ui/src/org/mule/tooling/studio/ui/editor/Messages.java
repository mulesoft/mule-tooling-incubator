package org.mule.tooling.studio.ui.editor;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.CaseFormat;

public class Messages {

    private static final String BUNDLE_NAME = "org.mule.tooling.studio.ui.editor.messages"; //$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private Messages() {
    }

    public static String getString(String key) {
        // TODO Auto-generated method stub
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(

            CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, key)

            ), ' ');

        }
    }

}
