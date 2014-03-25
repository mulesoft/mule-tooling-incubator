package org.mule.tooling.devkit.quickfix;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.mule.devkit.generation.api.gatherer.DevkitNotification;

public class MessageMatches implements ConditionMarkerEvaluator {
	final List<DevkitNotification> notifications;

	public MessageMatches(List<DevkitNotification> notifications) {
		this.notifications = notifications;
	}

	public MessageMatches(DevkitNotification notification) {
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
			Pattern p = Pattern.compile(escapeRE(notification.getMessage()).replaceAll(
					"%s", ".*"));
			Matcher m = p.matcher(problem);
			if (m.matches()) {
				return true;
			}
		}
		return false;
	}
	
	public static String escapeRE(String str) {
        //Pattern escaper = Pattern.compile("([^a-zA-z0-9])");
        //return escaper.matcher(str).replaceAll("\\\\$1");
        return str.replaceAll("\\[.*\\][^ ].*", "[%s]").replaceAll("([{}.\\[\\]])", "\\\\$1");
    }
}
