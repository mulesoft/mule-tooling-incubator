package org.mule.tooling.incubator.installer.views;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.mule.tooling.templatesrepo.urls.BaseStudioUrlProvider;
import org.mule.tooling.templatesrepo.urls.StudioUrl;

@SuppressWarnings("restriction")
public class InstallerStudioUrlProvider extends BaseStudioUrlProvider{
	
    public static final String ACTION_NAME = "installInStudio";

    public static final String LAB_URL_PARAMETER_KEY = "url";
    public static final String LAB_FEATURE_PARAMETER_KEY = "feature";
    public static final String LAB_VERSION_PARAMETER_KEY = "version";

	@Override
	public String getActionName() {
		return ACTION_NAME;
	}

	@Override
	protected List<String> getRequiredParameterKeys() {
		return Arrays.asList(LAB_URL_PARAMETER_KEY, LAB_FEATURE_PARAMETER_KEY, LAB_VERSION_PARAMETER_KEY );
	}

	@Override
	protected StudioUrl doCreateStudioUrl(String actionName, Properties parameters) {
		
		return new InstallerStudioUrl(parameters.getProperty(LAB_URL_PARAMETER_KEY), 
									  parameters.getProperty(LAB_FEATURE_PARAMETER_KEY), 
									  parameters.getProperty(LAB_VERSION_PARAMETER_KEY));
	}

}
