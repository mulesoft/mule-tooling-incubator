package org.mule.tooling.incubator.maven.ui.view;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;

/**
 * Because of a bug in eclipse platform, when using a filter in the tree the Filter expects to get an ILabelProvider, 
 * but IStyledLabelProvider doesn't extends from ILabelProvider. This allows you sort the ClassCastException.
 */
public class FilteredDelegatingStyledCellLabelProvider extends DelegatingStyledCellLabelProvider implements ILabelProvider {

    ILabelProvider labelProvider;

    public FilteredDelegatingStyledCellLabelProvider(IStyledLabelProvider labelProvider) {
        super(labelProvider);
        if (labelProvider instanceof ILabelProvider)
            this.labelProvider = (ILabelProvider) labelProvider;
    }

    @Override
    public String getText(Object element) {
        if (labelProvider != null)
            return labelProvider.getText(element);
        return getStyledText(element).getString();
    }

}
