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
	
	public PropertyKeyTreeNode findItem(String key) {
		if (StringUtils.isEmpty(key)) {
			return null;
		}
		
		if (!key.contains(".")) {
			return children.get(key);
		}
		
		int dotIndex = key.indexOf('.');
		
		String childKey = key.substring(0, dotIndex);
		
		if (children.containsKey(childKey)) {
			return children.get(childKey).findItem(key.substring(dotIndex + 1));
		} else {
			return null;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((keyFragment == null) ? 0 : keyFragment.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PropertyKeyTreeNode other = (PropertyKeyTreeNode) obj;
		if (keyFragment == null) {
			if (other.keyFragment != null)
				return false;
		} else if (!keyFragment.equals(other.keyFragment))
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		return true;
	}


	
}
