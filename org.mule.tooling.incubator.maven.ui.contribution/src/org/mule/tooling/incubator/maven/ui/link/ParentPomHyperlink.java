package org.mule.tooling.incubator.maven.ui.link;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.model.Parent;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.mule.tooling.incubator.maven.core.MavenArtifactResolver;

public class ParentPomHyperlink implements IHyperlink {

    final Parent parent;
    final IRegion region;

    public ParentPomHyperlink(IRegion region, Parent parent) {
        this.parent = parent;
        this.region = region;
    }

    @Override
    public IRegion getHyperlinkRegion() {
        return region;
    }

    @Override
    public String getTypeLabel() {
        return null;
    }

    @Override
    public String getHyperlinkText() {
        return "Open to parent pom.";
    }

    @Override
    public void open() {
        DefaultArtifact artifact = new DefaultArtifact(parent.getGroupId(), parent.getArtifactId(), VersionRange.createFromVersion(parent.getVersion()), null, "pom", null,
                new DefaultArtifactHandler("pom"));
        File file;
        try {
            file = MavenArtifactResolver.getInstance().getLocalArtifactPath(artifact);
            if (file.exists() && file.isFile()) {
                // Hack to open the parent pom in the default xml editor
                File temp = new File(FileUtils.getTempDirectory(), artifact.getGroupId()+File.separator+artifact.getArtifactId()+File.pathSeparator+artifact.getVersion()+".pom.xml");
                if (temp.exists()) {
                    temp.delete();
                }
                FileUtils.copyFile(file, temp);
                temp.setReadOnly();
                IFileStore fileStore = EFS.getLocalFileSystem().getStore(temp.toURI());
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

                try {
                    IDE.openEditorOnFileStore(page, fileStore);
                } catch (PartInitException e) {
                    // Put your exception handler here if you wish to
                }
            } else {
                // Do something if the file does not exist
            }
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

}
