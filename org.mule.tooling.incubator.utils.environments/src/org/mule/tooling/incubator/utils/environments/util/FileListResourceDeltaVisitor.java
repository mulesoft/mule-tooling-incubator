package org.mule.tooling.incubator.utils.environments.util;

import java.io.File;
import java.util.Set;

import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

/**
 * Simple delta visitor that would check if the delta belongs to one of the provided files.
 * @author juancavallotti
 *
 */
public class FileListResourceDeltaVisitor implements IResourceDeltaVisitor {
	
	private final Set<File> filesToCheck;
	
	private boolean found;
	
	public FileListResourceDeltaVisitor(Set<File> filesToCheck) {
		this.filesToCheck = filesToCheck;
	}

	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {
		
		//for the time being only interested in changed or removed files.
		if (delta.getKind() != IResourceDelta.CHANGED && delta.getKind() != IResourceDelta.REMOVED) {
			return true;
		}
		
		if (delta.getKind() == IResourceDelta.CHANGED && (delta.getFlags() & IResourceDelta.CONTENT) == 0) {
			//if the file has changed but not the content, well, we don't care
			return true;
		}
		
		String filePath = delta.getFullPath().toString();
		
		for(File f : filesToCheck) {
			if  (f.getAbsolutePath().endsWith(filePath)) {
				found = true;
				break;
			}
		}
		
		return !found;
	}

	public boolean isFound() {
		return found;
	}
	
}
