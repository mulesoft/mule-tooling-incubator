package org.mule.tooling.devkit.perspective;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.mule.tooling.devkit.treeview.DevkitView;

public class DevkitPerspectiveFactory implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		// Get the editor area.
        String editorArea = layout.getEditorArea();

        // Top left: Resource Navigator view and Bookmarks view placeholder
        IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, 0.25f,
                editorArea);
        topLeft.addView("org.eclipse.jdt.ui.PackageExplorer");
        topLeft.addPlaceholder(IPageLayout.ID_BOOKMARKS);

        // Bottom right: Task List view
        layout.addView(IPageLayout.ID_PROBLEM_VIEW, IPageLayout.BOTTOM, 0.66f, editorArea);
        
        // RIGT left: Resource Navigator view and Bookmarks view placeholder
        IFolderLayout topRight = layout.createFolder("topRight", IPageLayout.RIGHT, 0.77f,
                editorArea);
        topRight.addView(IPageLayout.ID_OUTLINE);
        topRight.addView(DevkitView.ID);
	}

}
