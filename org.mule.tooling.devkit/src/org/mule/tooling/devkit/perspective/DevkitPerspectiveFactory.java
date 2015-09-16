package org.mule.tooling.devkit.perspective;

import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.progress.IProgressConstants;
import org.mule.tooling.devkit.treeview.DevkitView;

public class DevkitPerspectiveFactory implements IPerspectiveFactory {

    @Override
    public void createInitialLayout(IPageLayout layout) {
        // Get the editor area.
        String editorArea = layout.getEditorArea();

        // Top left: Resource Navigator view and Bookmarks view placeholder
        IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, 0.25f, editorArea);
        topLeft.addView(JavaUI.ID_PACKAGES);
        topLeft.addPlaceholder(IPageLayout.ID_BOOKMARKS);

        // Bottom right: Task List view
        layout.addView(IPageLayout.ID_PROBLEM_VIEW, IPageLayout.BOTTOM, 0.66f, editorArea);

        layout.addActionSet(IDebugUIConstants.LAUNCH_ACTION_SET);
        layout.addActionSet(JavaUI.ID_ACTION_SET);
        layout.addActionSet(JavaUI.ID_ELEMENT_CREATION_ACTION_SET);
        layout.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);

        // views - java
        layout.addShowViewShortcut(JavaUI.ID_PACKAGES);

        // views - search
        layout.addShowViewShortcut(NewSearchUI.SEARCH_VIEW_ID);

        // views - debugging
        layout.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);

        // views - standard workbench
        layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
        layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
        layout.addShowViewShortcut(IPageLayout.ID_TASK_LIST);
        layout.addShowViewShortcut(IProgressConstants.PROGRESS_VIEW_ID);
        layout.addShowViewShortcut(IPageLayout.ID_PROJECT_EXPLORER);

        // RIGT left: Resource Navigator view and Bookmarks view placeholder
        IFolderLayout topRight = layout.createFolder("topRight", IPageLayout.RIGHT, 0.77f, editorArea);
        topRight.addView(IPageLayout.ID_OUTLINE);
        topRight.addView(DevkitView.ID);
    }

}
