package org.mule.tooling.ui.contribution.sap.metadata;

import java.util.List;

import org.mule.common.DefaultResult;
import org.mule.common.FailureType;
import org.mule.common.MuleArtifact;
import org.mule.common.Result;
import org.mule.common.metadata.MetaDataKey;
import org.mule.tooling.metadata.api.utils.ExecuteGlobalElementArtifactMethodCallback;

import com.mulesoft.mule.transport.sap.Searchable;

public class ExecuteSapObjectSearchCallback implements ExecuteGlobalElementArtifactMethodCallback<Result<?>> {
    private String type;
    private String filter;
    
    public ExecuteSapObjectSearchCallback() {
    }

    @Override
    public String getArtifactMethodDescription() {
        return this.type + " search";
    }

    @Override
    public String getInfoMessage(String globalElementName) {
        return "Searching " + this.type + " objects matching filter '" + this.filter + "'";
    }

    @Override
    public Result<List<MetaDataKey>> executeArtifactMethod(MuleArtifact artifact, String globalElementName) {
        if (artifact.hasCapability(Searchable.class)) {
            Searchable connector = artifact.getCapability(Searchable.class);
            String startsWithFilter = this.filter != null && this.filter.endsWith("*") ? this.filter : this.filter + "*";
            return connector.search(this.type, startsWithFilter);
        }
        return new DefaultResult<List<MetaDataKey>>(null, Result.Status.FAILURE, "Artifact is not searchable", FailureType.UNSPECIFIED, null);
    }

    @Override
    public boolean displaySuccessMessage() {
        return false;
    }

    @Override
    public String getSuccessMessage() {
        // No need to display one!
        return null;
    }

    
    public String getType() {
        return type;
    }

    
    public void setType(String type) {
        this.type = type;
    }

    
    public String getFilter() {
        return filter;
    }

    
    public void setFilter(String filter) {
        this.filter = filter;
    }    
}
