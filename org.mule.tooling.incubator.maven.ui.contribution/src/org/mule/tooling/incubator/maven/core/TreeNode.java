package org.mule.tooling.incubator.maven.core;

import java.util.ArrayList;
import java.util.List;

public class TreeNode<T>{
    T nodeItem;
    public  T getNodeItem(){
        return nodeItem;
    }
    public List<TreeNode<T>> getChildItems(){
        return childItems;
    }
    List<TreeNode<T>> childItems = new ArrayList<TreeNode<T>>();
}