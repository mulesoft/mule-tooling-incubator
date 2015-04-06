package org.mule.tooling.incubator.gradle;

import org.apache.maven.model.Repository;
import org.mule.tooling.maven.dependency.MavenDependency;

import com.google.common.base.Optional;

/**
 * Very ugly workaround for packaging inconsistencies, I hope to be able to get rid of this
 * very soon. 
 * @author juancavallotti
 */
public class WellBehavedMavenDependency implements MavenDependency {
	
	private final MavenDependency delegate;
	
	private String fixedGroupId;
	private String fixedArtifactId;
	
	public WellBehavedMavenDependency(MavenDependency delegate) {
		this.delegate = delegate;
		triggerUglyFixRules();
	}

	private void triggerUglyFixRules() {
		
		//this only luckily needs to be applied to apikit.
    	if (delegate.getArtifactId()!= null && delegate.getArtifactId().equals("mule-module-apikit")) {
    		fixedArtifactId = "mule-module-apikit-plugin";
    	}
	}

	@Override
	public String getArtifactId() {
		
		if (fixedArtifactId != null) {
			return fixedArtifactId;
		}
		
		return delegate.getArtifactId();
	}

	@Override
	public String getGroupId() {
		if (fixedGroupId != null) {
			return fixedGroupId;
		}
		
		return delegate.getGroupId();
	}

	@Override
	public Repository getReleaseRepository() {
		return delegate.getReleaseRepository();
	}

	@Override
	public Scope getScope() {
		return delegate.getScope();
	}

	@Override
	public Repository getSnapshotsRepository() {
		return delegate.getSnapshotsRepository();
	}

	@Override
	public String getVersion() {
		return delegate.getVersion();
	}

    @Override
    public Optional<String> getType() {

        return delegate.getType();
    }

    @Override
    public Optional<String> getSystemPath() {
        return delegate.getSystemPath();
    }

    @Override
    public Optional<Boolean> isOptional() {
        return delegate.isOptional();
    }

    @Override
    public Optional<String> getClassifier() {
        return delegate.getClassifier();
    }
	
}
