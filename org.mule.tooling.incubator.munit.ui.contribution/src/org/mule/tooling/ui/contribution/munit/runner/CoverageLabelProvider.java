package org.mule.tooling.ui.contribution.munit.runner;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class CoverageLabelProvider extends LabelProvider {

    @Override
    public Image getImage(Object arg0) {
        Map.Entry<String, Double> entry = (Entry<String, Double>) arg0;
        if (entry.getValue() < 50.0) {
            return AbstractUIPlugin.imageDescriptorFromPlugin("org.mule.tooling.messageflow", "icons/bullet-red-alt.png").createImage();
        } else if (entry.getValue() < 70.0) {
            return AbstractUIPlugin.imageDescriptorFromPlugin("org.mule.tooling.messageflow", "icons/bullet-yellow-alt.png").createImage();
        }
        return AbstractUIPlugin.imageDescriptorFromPlugin("org.mule.tooling.messageflow", "icons/bullet-green-alt.png").createImage();
    }

    @Override
    public String getText(Object arg0) {
        Map.Entry<String, Double> entry = (Entry<String, Double>) arg0;
        return entry.getKey().replace("/", "") + "(" + MunitTestRunnerViewPart.numberFormat.format(entry.getValue()) + "%)";
    }

}
