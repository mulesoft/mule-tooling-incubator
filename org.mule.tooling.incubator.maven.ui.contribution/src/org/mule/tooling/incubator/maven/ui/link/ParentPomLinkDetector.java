package org.mule.tooling.incubator.maven.ui.link;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;

public class ParentPomLinkDetector extends org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector {

    Pattern pattern = Pattern.compile(".*<(groupId|version|artifactId)>.*</(groupId|version|artifactId)>.*");

    @Override
    public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {

        IDocument document = textViewer.getDocument();
        try {
            IRegion reg = document.getLineInformationOfOffset(region.getOffset());
            String value = document.get(reg.getOffset(), reg.getLength());

            Matcher matcher = pattern.matcher(value);
            Parent parent = getParent(document);
            if (matcher.find() && parent != null) {
                int offset = document.get().indexOf("<parent>");
                IRegion start = document.getLineInformationOfOffset(offset);
                int lastChar = document.get().indexOf("</parent>");
                IRegion end = document.getLineInformationOfOffset(lastChar);
                if ((offset != -1) && (lastChar != -1)) {
                    Region parentReg = new Region(start.getOffset(), end.getOffset() + end.getLength() - start.getOffset());
                    if (isRegionContained(region, parentReg)) {
                        IHyperlink[] links = new IHyperlink[1];
                        links[0] = new ParentPomHyperlink(parentReg, parent);
                        
                        return links;
                    }
                }
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected boolean isRegionContained(IRegion region, Region parentReg) {
        return parentReg.getOffset() < region.getOffset() && ((parentReg.getOffset() + parentReg.getLength()) > (region.getOffset() + region.getLength()));
    }

    private Parent getParent(IDocument doc) {
        Model model = null;
        MavenXpp3Reader mavenreader = new MavenXpp3Reader();
        InputStream in = null;
        try {
            in = IOUtils.toInputStream(doc.get(), "UTF-8");
            model = mavenreader.read(in);
            return model.getParent();
        } catch (Exception ex) {

        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {

                }
            }
        }
        return null;
    }
}
