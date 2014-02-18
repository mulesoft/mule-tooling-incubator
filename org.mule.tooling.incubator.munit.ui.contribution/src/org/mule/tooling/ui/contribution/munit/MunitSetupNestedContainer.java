package org.mule.tooling.ui.contribution.munit;

import org.eclipse.gef.EditPart;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.messageflow.editpart.CollapsableNestedContainerFigureFactory;
import org.mule.tooling.messageflow.editpart.NestedContainerEditPart;
import org.mule.tooling.model.messageflow.decorator.EntityDecorator;
import org.mule.tooling.model.messageflow.decorator.NestedContainerDecorator;

/**
 * <p>
 * The Setup containter of the Munit test, the only difference with the {@link NestedContainerEditPart} is that it doesn't draw the connections
 * </p>
 */
public class MunitSetupNestedContainer extends NestedContainerEditPart {

    public MunitSetupNestedContainer(NestedContainerDecorator nestedContainerDecorator, IMuleProject project, EditPart context) {
        super(nestedContainerDecorator, project, new CollapsableNestedContainerFigureFactory(context));
    }

    @Override
    protected void drawConnections(EntityDecorator<?> last, EntityDecorator<?> wrappedEntity) {
    }

}
