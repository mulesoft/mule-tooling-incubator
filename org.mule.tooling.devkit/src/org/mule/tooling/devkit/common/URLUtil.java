package org.mule.tooling.devkit.common;

import java.io.File;
import java.net.URI;
import java.net.URL;

/**
 * A utility class for manipulating URLs. This class works around some of the broken behavior of the java.net.URL class.
 */
public class URLUtil {

    /**
     * Returns the URL as a local file, or <code>null</code> if the given URL does not represent a local file.
     * 
     * @param url
     *            The url to return the file for
     * @return The local file corresponding to the given url, or <code>null</code>
     */
    public static File toFile(URL url) {

        if (!"file".equalsIgnoreCase(url.getProtocol())) //$NON-NLS-1$
            return null;
        // assume all illegal characters have been properly encoded, so use URI class to unencode

        String externalForm = url.toExternalForm();
        String pathString = externalForm.substring(5);

        try {
            if (pathString.indexOf('/') == 0) {
                if (pathString.indexOf("//") == 0) //$NON-NLS-1$
                    externalForm = "file:" + ensureUNCPath(pathString); //$NON-NLS-1$
                return new File(new URI(externalForm));
            }
            if (pathString.indexOf(':') == 1)
                return new File(new URI("file:/" + pathString)); //$NON-NLS-1$

            return new File(new URI(pathString).getSchemeSpecificPart());
        } catch (Exception e) {
            // URL contains unencoded characters
            return new File(pathString);
        }
    }

    /**
     * Ensures the given path string starts with exactly four leading slashes.
     */
    private static String ensureUNCPath(String path) {
        int len = path.length();
        StringBuffer result = new StringBuffer(len);
        for (int i = 0; i < 4; i++) {
            // if we have hit the first non-slash character, add another leading slash
            if (i >= len || result.length() > 0 || path.charAt(i) != '/')
                result.append('/');
        }
        result.append(path);
        return result.toString();
    }
}