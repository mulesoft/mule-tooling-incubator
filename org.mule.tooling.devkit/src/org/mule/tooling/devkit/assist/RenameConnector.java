package org.mule.tooling.devkit.assist;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.internal.corext.refactoring.changes.RenameCompilationUnitChange;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jface.window.Window;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.NullChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.resource.RenameResourceChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.mule.tooling.devkit.common.DevkitUtils;
import org.mule.tooling.devkit.quickfix.LocateModuleNameVisitor;

public class RenameConnector extends CompositeChange {

	final IInvocationContext context;

	public RenameConnector(IInvocationContext context) {
		super("Rename Connector Composite");
		this.context = context;

	}

	@Override
	public Change perform(IProgressMonitor pm) throws CoreException {
		NewNameDialog dialog = new NewNameDialog(null, context
				.getCompilationUnit()
				.getElementName()
				.substring(
						0,
						context.getCompilationUnit().getElementName()
								.indexOf("Connector")));
		dialog.create();
		if (dialog.open() == Window.OK) {
			IProject project = context.getCompilationUnit().getJavaProject()
					.getProject();

			LocateModuleNameVisitor locator = new LocateModuleNameVisitor();
			context.getASTRoot().accept(locator);
			String namespace = locator.getValue();

			String lowerCaseName = dialog.getFirstName().toLowerCase();

			CompositeChange complexChange = new CompositeChange(
					"Rename + modify content");

			renameNamespace(
					context.getCompilationUnit().getResource(),
					project,
					namespace,
					lowerCaseName,
					new ReplaceEdit(locator.getLocation() + 1, namespace
							.length(), lowerCaseName));
			complexChange.add(new RenameCompilationUnitChange(context
					.getCompilationUnit(), dialog.getFirstName()
					+ "Connector.java"));

			add(complexChange);

			if (project != null) {

				IFolder folder = project.getFolder(DevkitUtils.ICONS_FOLDER);

				Pattern patternIcon = Pattern
						.compile("(.*)(-connector.*\\.png)");
				try {
					for (IResource resource : folder.members()) {
						Matcher matcher = patternIcon.matcher(resource
								.getName());
						if (matcher.find()) {
							add(new RenameResourceChange(
									resource.getFullPath(), lowerCaseName
											+ matcher.group(2)));
						}
					}
				} catch (CoreException e1) {
					e1.printStackTrace();
				}

				folder = project.getFolder(DevkitUtils.DOCS_FOLDER);

				Pattern patternSample = Pattern
						.compile("(.*)(-connector\\.xml\\.sample)");
				try {
					for (IResource resource : folder.members()) {
						Matcher matcher = patternSample.matcher(resource
								.getName());
						if (matcher.find()) {
							renameNamespace(resource, project, namespace,
									lowerCaseName, null);
							add(new RenameResourceChange(
									resource.getFullPath(), lowerCaseName
											+ matcher.group(2)));
						}
					}
				} catch (CoreException e1) {
					e1.printStackTrace();
				}

				folder = project.getFolder(DevkitUtils.TEST_RESOURCES_FOLDER);

				try {
					Pattern patternConfig = Pattern
							.compile("(.*)(-config\\.xml)");
					for (IResource resource : folder.members()) {
						Matcher matcher = patternConfig.matcher(resource
								.getName());
						if (matcher.find()) {
							renameNamespace(resource, project, namespace,
									lowerCaseName, null);
							add(new RenameResourceChange(
									resource.getFullPath(), lowerCaseName
											+ matcher.group(2)));
						}
					}
				} catch (CoreException e1) {
					e1.printStackTrace();
				}
			}
			return super.perform(pm);
		}
		return new NullChange();
	}

	private TextFileChange renameNamespace(IResource resource, IProject project,
			String name, String newName, TextEdit extraEdit) {

		TextFileChange textChange = new TextFileChange(
				"Update sample filename", (IFile) resource);
		add(textChange);

		MultiTextEdit change = new MultiTextEdit();
		textChange.setEdit(change);
		if (extraEdit != null) {
			change.addChild(extraEdit);
		}
		IFile file = project.getFile(resource.getProjectRelativePath());
		InputStreamReader isr = null;
		try {
			isr = new InputStreamReader(file.getContents());
			int character = 0;
			int index = -1;
			while ((character = isr.read()) != -1) {
				index++;
				int nameIndex = 0;
				int suffixIndex = 0;
				while (nameIndex < name.length()
						&& name.charAt(nameIndex) == character) {
					character = isr.read();
					nameIndex++;
				}
				if (nameIndex == name.length() && character == ':') {
					change.addChild(new ReplaceEdit(index, name.length(),
							newName));
					// Matcheo
				}
				if (character == '-') {
					String sampleSuffix = "-connector.xml.sample";
					while (suffixIndex < sampleSuffix.length()
							&& sampleSuffix.charAt(suffixIndex) == character) {
						character = isr.read();
						suffixIndex++;
					}
					if (nameIndex == name.length()
							&& suffixIndex == sampleSuffix.length()) {
						change.addChild(new ReplaceEdit(index, name.length(),
								newName));
						// Matcheo
					}
				}

				index += nameIndex + suffixIndex;
			}

		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (isr != null) {
				try {
					isr.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return textChange;
	}
}
