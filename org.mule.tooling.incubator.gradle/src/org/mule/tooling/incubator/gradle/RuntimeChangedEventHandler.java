package org.mule.tooling.incubator.gradle;

import org.mule.tooling.core.event.IMuleRuntimeChangedListener;
import org.mule.tooling.core.model.IMuleProject;

public class RuntimeChangedEventHandler implements IMuleRuntimeChangedListener {

	@Override
	public void afterChanging(String arg0, String arg1, IMuleProject arg2) {
		System.out.println(String.format("After change: %s %s %s", arg0, arg1, arg2.getName()));
	}

	@Override
	public void beforeChanging(String arg0, String arg1, IMuleProject arg2) {
		System.out.println(String.format("Before change: %s %s %s", arg0, arg1, arg2.getName()));
	}

}
