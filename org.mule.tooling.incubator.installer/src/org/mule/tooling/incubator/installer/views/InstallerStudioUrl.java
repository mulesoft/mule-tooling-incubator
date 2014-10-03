package org.mule.tooling.incubator.installer.views;

import org.mule.tooling.templatesrepo.TemplatesRepository;
import org.mule.tooling.templatesrepo.urls.BaseStudioUrl;

@SuppressWarnings("restriction")
public class InstallerStudioUrl extends BaseStudioUrl {
	
	private String url;
	private String featureId;
	private String version;
	
	private InstallerService service;
	
    public InstallerStudioUrl(String url, String featureId, String version) {
    	this.url = url;
    	this.featureId = featureId;
    	this.version = version;
    }
	
	@Override
	public void execute() {
		
		TemplatesRepository.getInstance().close();
		
		service = new InstallerService(url);
		
		InstallationStatus status = service.checkInstalled(featureId, version);
		
		if (InstallationStatus.NOT_INSTALLED.equals(status))
			service.install(featureId, version);
		else if (InstallationStatus.NEEDS_UPDATE.equals(status))
			service.update(featureId, version);
		else if (InstallationStatus.INSTALLED.equals(status))
			TemplatesRepository.getInstance().openDialogOn(TemplatesRepository.getInstance().getRepositoryUrl() + "&objectStatus=INSTALLED");
		

	}
	
	

}
