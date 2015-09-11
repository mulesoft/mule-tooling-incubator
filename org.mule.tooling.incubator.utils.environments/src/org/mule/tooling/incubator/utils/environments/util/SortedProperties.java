package org.mule.tooling.incubator.utils.environments.util;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;

public class SortedProperties extends Properties {

	private static final long serialVersionUID = 1L;

	@Override
	public synchronized Enumeration<Object> keys() {
		System.out.println("Called Keys on sorted properties!!");
		return Collections.enumeration(new TreeSet<Object>(super.keySet()));
	}
}
