package org.mule.tooling.devkit.links;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.mule.tooling.devkit.common.DevkitUtils;

public class SampleHyperlink implements IHyperlink {

	private final IRegion region;
	private final String label;
	private final String text;
	private final String link;
	private final IProject project;

	public SampleHyperlink(int offset, int length, String label, String text,
			String link, IProject project) {
		this(new Region(offset, length), label, text, link, project);

	}

	public SampleHyperlink(IRegion region, String label, String text,
			String link, IProject project) {
		this.region = region;
		this.label = label;
		this.text = text;
		this.link = link;
		this.project = project;
	}

	@Override
	public IRegion getHyperlinkRegion() {
		return region;
	}

	@Override
	public String getTypeLabel() {
		return label;
	}

	@Override
	public String getHyperlinkText() {
		return text;
	}

	@Override
	public void open() {
		IFile file = getFileFromResource();
		if (file != null) {
			InputStreamReader isr = null;
			try {
				int startPos = link.indexOf("@sample.xml");
				String sample = link.substring(startPos).split(" ")[2];
				isr = new InputStreamReader(file.getContents());
				BufferedReader ir = new BufferedReader(isr);
				int lineNumber = 0;
				String line;
				boolean found = false;
				while ((line = ir.readLine()) != null) {
					lineNumber++;
					if (line.contains(sample)) {
						found = true;
						break;
					}

				}
				if (!found) {
					lineNumber = 0;
				}
				openSampleAtLine(file, lineNumber);
			} catch (CoreException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (isr != null) {
					try {
						isr.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
    private void openSampleAtLine(IFile file, int lineNumber)
			throws CoreException, PartInitException {
		HashMap map = new HashMap();
		map.put(IMarker.LINE_NUMBER, new Integer(lineNumber));
		map.put(IWorkbenchPage.EDITOR_ID_ATTR,
				"org.mule.tooling.devkit.sample.editor.XMLEditor");
		IMarker marker;

		marker = file.createMarker(IMarker.TEXT);

		marker.setAttributes(map);
		// page.openEditor(marker); //2.1 API
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		// page.openEditor(new SampleDocEditorInput(file, module),
		// "org.mule.tooling.devkit.sample.editor.XMLEditor");
		IDE.openEditor(page, marker); // 3.0 API
		marker.delete();
	}

	private IFile getFileFromResource() {
		IFolder folder = project.getFolder(DevkitUtils.DOCS_FOLDER);
		IFile file = null;
		try {
			for (IResource resource : folder.members()) {
				if (resource.getName().matches(".*.sample")) {
					file = project.getFile(resource.getProjectRelativePath());
					break;
				}
			}
		} catch (CoreException e1) {
			e1.printStackTrace();
		}
		return file;
	}

}
