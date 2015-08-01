package org.mule.tooling.incubator.utils.environments.model;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

public class PropertyKeyTreeNode {
	private String keyFragment;
	private PropertyKeyTreeNode parent;
	private Map<String, PropertyKeyTreeNode> children;
	
	public PropertyKeyTreeNode(String keyFragment, PropertyKeyTreeNode parent) {
		super();
		this.keyFragment = keyFragment;
		this.parent = parent;
		children = new TreeMap<String, PropertyKeyTreeNode>();
	} 
	
	public void storeKey(String key) {
		
		if (key == null) {
			throw new IllegalArgumentException("Cannot send null key!");
		}

		if (StringUtils.isEmpty(key)) {
			//we have nothing else to do.
			return;
		}

		
		int dotIndex = key.indexOf('.');
		
		String childKey = null;
		
		//leaf
		if (dotIndex == -1) {
			childKey = key;
		} else {
			childKey = key.substring(0, dotIndex);			
		}
		
		
		//store the new children.
		PropertyKeyTreeNode childNode = children.get(childKey);
		
		if (childNode == null) {
			childNode = new PropertyKeyTreeNode(childKey, this);
			children.put(childKey, childNode);
		}
		
		if (dotIndex == -1) {
			//nothing else to do
			return;
		}
		
		//continue to store any subkeys that might be
		childNode.storeKey(key.substring(dotIndex + 1));
	}
	
	public String buildCompleteKey() {
		String ret = keyFragment;
		PropertyKeyTreeNode currentParent = this.parent;
		while (currentParent != null && currentParent.getKeyFragment() != null) {
			ret = currentParent.getKeyFragment() + "." + ret;
			currentParent = currentParent.parent;
		}
		
		return ret;
	}
	
	public PropertyKeyTreeNode[] getChildrenArray() {
		PropertyKeyTreeNode[] ret = new PropertyKeyTreeNode[children.size()];
		
		int i = 0;
		
		for(String key : children.keySet()) {
			ret[i++] = children.get(key);
		}
		
		return ret;
	}
	
	
	public String getKeyFragment() {
		return keyFragment;
	}

	public PropertyKeyTreeNode getParent() {
		return parent;
	}

	public Map<String, PropertyKeyTreeNode> getChildren() {
		return children;
	}
	
	public boolean isLeaf() {
		return !children.isEmpty();
	}
	
}
