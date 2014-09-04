package org.mule.tooling.devkit.quickfix;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;

import org.eclipse.jdt.core.search.TypeNameMatch;
import org.eclipse.jdt.core.search.TypeNameMatchRequestor;

public class TypeNameMatchCollector extends TypeNameMatchRequestor {

    private final Collection<TypeNameMatch> fCollection;

    public TypeNameMatchCollector(Collection<TypeNameMatch> collection) {
        Assert.isNotNull(collection);
        fCollection = collection;
    }

    @Override
    public void acceptTypeNameMatch(TypeNameMatch match) {

        fCollection.add(match);
    }

}
