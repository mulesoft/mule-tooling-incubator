/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.tooling.ui.contribution.munit.runner;

import java.util.HashSet;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.ui.IWorkbenchPage;
import org.mule.tooling.ui.contribution.munit.MunitPlugin;

public class MunitModel
{

    private final ILaunchListener fLaunchListener = new MunitLaunchListener();

    public void start()
    {
        ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
        launchManager.addLaunchListener(fLaunchListener);
    }

    public void stop()
    {
        ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
        launchManager.removeLaunchListener(fLaunchListener);
    }


    private final class MunitLaunchListener implements ILaunchListener
    {

        private HashSet<ILaunch> fTrackedLaunches = new HashSet<ILaunch>(20);

        public void launchAdded(ILaunch launch)
        {
            fTrackedLaunches.add(launch);
        }

        public void launchRemoved(final ILaunch launch)
        {
            fTrackedLaunches.remove(launch);
        }

        public void launchChanged(final ILaunch launch)
        {
            if (!fTrackedLaunches.contains(launch))
            {
                return;
            }

            ILaunchConfiguration config = launch.getLaunchConfiguration();
            if (config == null)
            {
                return;
            }

            final IJavaProject javaProject = MunitLaunchConfigurationConstants.getJavaProject(config);
            if (javaProject == null)
            {
                return;
            }

            try
            {
                fTrackedLaunches.remove(launch);
                connectTestRunner(launch, javaProject);
            }
            catch (NumberFormatException e)
            {
                return;
            }
        }

        private void connectTestRunner(ILaunch launch, IJavaProject javaProject)
        {
            MunitPlugin.asyncShowTestRunnerViewPart();

            IWorkbenchPage page = MunitPlugin.getActivePage();
            if (page != null)
            {
                MunitTestRunnerViewPart view = (MunitTestRunnerViewPart) page.findView(MunitTestRunnerViewPart.NAME);
                if (view == null)
                {
                    page.hideView(view);
                }
            }


        }
    }
    
}
