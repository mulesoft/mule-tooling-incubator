package org.mule.tooling.ui.contribution.munit;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.core.model.IMuleProjectAware;
import org.mule.tooling.model.messageflow.decorator.NestedContainerDecorator;

public class MunitEditPartFactory implements EditPartFactory, IMuleProjectAware{

	private IMuleProject muleProject;

	@Override
	public EditPart createEditPart(EditPart context, Object model) {
		return new MunitSetupNestedContainer((NestedContainerDecorator) model, muleProject, context);
	}

	@Override
	public void setMuleProject(IMuleProject muleProject) {
		this.muleProject = muleProject;
	}

	@Override
	public IMuleProject getMuleProject() {
		return muleProject;
	}

}
