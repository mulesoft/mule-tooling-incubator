package org.mule.tooling.ui.contribution.munit.coverage;

import org.eclipse.jface.resource.ImageDescriptor;
import org.mule.tooling.messageflow.figuredecorator.DefaultFigureDecorator;
import org.mule.tooling.messageflow.figuredecorator.IFigureDecorator;
import org.mule.tooling.messageflow.figuredecorator.IFigureDecoratorFactory;
import org.mule.tooling.messageflow.figuredecorator.OverlayIcon;
import org.mule.tooling.messageflow.util.MessageFlowEntityRef;
import org.mule.tooling.messageflow.util.MessageFlowUtils;
import org.mule.tooling.model.messageflow.MessageFlowEntity;
import org.mule.tooling.ui.contribution.munit.MunitPlugin;
import org.mule.tooling.ui.contribution.munit.runner.MunitTestRunnerViewPart;


public class CoverageFigureDecorator implements IFigureDecoratorFactory {

    public CoverageFigureDecorator() {
        super();

    }

    @Override
    public boolean decorates(MessageFlowEntity entity) {
        return true;
    }

    @Override
    public IFigureDecorator createDecorator(MessageFlowEntity entity) {
        MessageFlowUtils messageFlowUtils = MessageFlowUtils.getInstance();

        MessageFlowEntityRef path = messageFlowUtils.findMulePathForEntityInOpenEditors(entity);
        CoverageReport report = MunitTestRunnerViewPart.coverageReport;
        if (path == null || report == null) {
            return null;
        }

        if ( report.getCoveredPaths().contains(path.getMulePath().toString()) ){
                    final ImageDescriptor breakpointImg = MunitPlugin.CHECK_ICON_DESCRIPTOR;
                    OverlayIcon breakpointOverlay = new OverlayIcon(breakpointImg, null);
                    return new DefaultFigureDecorator(null, null, breakpointOverlay, null);
        }
        return null;

    }

}
