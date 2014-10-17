package org.mule.tooling.incubator.gradle;

import org.mule.tooling.core.dependencymanagement.EclipseDependencyManager;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.core.module.ExternalContributionMuleModule;
import org.mule.tooling.incubator.gradle.jobs.AddDependencyToProjectJob;
import org.mule.tooling.incubator.gradle.jobs.RemoveDependencyFromProjectJob;
import org.mule.tooling.model.messageflow.MessageFlowNode;


public class GradleDependencyManager extends EclipseDependencyManager {
    
    @Override
    public void addDependency(ExternalContributionMuleModule contributionModule, IMuleProject muleProject) {
        super.addDependency(contributionModule, muleProject);
        AddDependencyToProjectJob job = new AddDependencyToProjectJob(contributionModule, muleProject);
        job.configureAndSchedule();
    }

    @Override
    public void addDependency(MessageFlowNode arg0, IMuleProject arg1) {
        Activator.logError("Could not add dependency from flow node.", null);
    }

    @Override
    public boolean appliesTo(IMuleProject project) {
        return super.appliesTo(project) && GradlePluginUtils.shallowCheckIsGradleproject(project.getJavaProject().getProject());
    }

    @Override
    public String getName() {
        return "Mule Gradle Dependency Manager";
    }

    @Override
    public void removeDependency(ExternalContributionMuleModule contributionModule, IMuleProject muleProject) {
        //for the time being, this is taken care by the gradle plugin itself when running 'studio' task
        //super.removeDependency(contributionModule, muleProject);
        //RemoveDependencyFromProjectJob job = new RemoveDependencyFromProjectJob(contributionModule, muleProject);
        //job.schedule();
    }
}
