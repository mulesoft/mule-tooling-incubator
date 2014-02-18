package org.mule.tooling.ui.contribution.munit.runner;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class CoverageTreeContentProvider implements ITreeContentProvider {

    @Override
    public Object[] getElements(Object arg0) {
        Map<String, Double> map = (Map<String, Double>) arg0;
        Entry<String, Double>[] entries = new Map.Entry[0];
        return map.entrySet().toArray(entries);
    }

    @Override
    public void dispose() {

    }

    @Override
    public void inputChanged(Viewer arg0, Object arg1, Object arg2) {

    }

    @Override
    public Object[] getChildren(Object arg0) {
        return new Object[0];
    }

    @Override
    public Object getParent(Object arg0) {
        return null;
    }

    @Override
    public boolean hasChildren(Object arg0) {
        return false;
    }

}
