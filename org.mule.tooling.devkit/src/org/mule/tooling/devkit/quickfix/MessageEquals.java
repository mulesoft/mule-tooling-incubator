package org.mule.tooling.devkit.quickfix;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.mule.devkit.generation.api.gatherer.DevkitNotification;

public class MessageEquals implements ConditionMarkerEvaluator {
	final List<DevkitNotification> notifications;

	public MessageEquals(List<DevkitNotification> notifications) {
		this.notifications = notifications;
	}

	public MessageEquals(DevkitNotification notification) {
		this.notifications = new ArrayList<DevkitNotification>();
		notifications.add(notification);
	}

	@Override
	public boolean hasFixForMarker(IMarker marker) {
		String problem = "";
		try {
			problem = (String) marker.getAttribute(IMarker.MESSAGE);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		for (DevkitNotification notification : notifications) {
			if (problem.equals(notification.getMessage())) {
				return true;
			}
		}
		return false;
	}

}
