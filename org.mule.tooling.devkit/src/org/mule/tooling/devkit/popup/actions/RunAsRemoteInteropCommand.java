package org.mule.tooling.devkit.popup.actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.management.RuntimeErrorException;

import org.apache.commons.httpclient.params.HttpParams;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;
import org.mule.tooling.devkit.common.DevkitUtils;
import org.mule.tooling.devkit.dialogs.InteropRunOptionsSelectionDialog;
import org.mule.tooling.devkit.dialogs.TestdataOptionsSelectionDialog;


public class RunAsRemoteInteropCommand extends AbstractHandler{

	public enum ConfigKeys {destinationEmail, testDataPath, testDataOverridePath,
		repository, serverURL, selectedDebug, selectedVerbose};

		private Map<ConfigKeys, String> runnerConfig = null;

		@Override
		public Object execute(ExecutionEvent event) throws ExecutionException {

			ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event)
					.getActivePage().getSelection();
			if (selection != null & selection instanceof IStructuredSelection) {
				Object selected = ((IStructuredSelection) selection)
						.getFirstElement();

				if (selected instanceof IJavaElement) {
					final IProject selectedProject = ((IJavaElement) selected)
														.getJavaProject().getProject();
					if (selectedProject != null) {

						setTestdataFilesInProjectAsDefault(selectedProject);

						if ( getConfigurationAndContinue() ){

							try {
								DefaultHttpClient httpclient = new DefaultHttpClient();

								HttpPost httppost = createPostRequest();
								
								System.out.println("executing request " + httppost.getRequestLine());
								
								HttpResponse response = httpclient.execute(httppost);

								System.out.println(response.getEntity().toString());

							} catch (ClientProtocolException e) {
								e.printStackTrace();
								throw new RuntimeException();
							} catch (IOException e) {
								e.printStackTrace();
								throw new RuntimeException();
							}
						}	

					}
				}
			}


			return null;
		}


		private void setTestdataFilesInProjectAsDefault(final IProject selectedProject) {
			try {

				final String testDataNameFormat = ".*test(d|D)ata\\.xml";
				final String testDataOverrideNameFormat = ".*test(d|D)ata.*(o|O)verride\\.xml";

				String testDataPath = "";
				String testDataOverridePath = "";
				
				IContainer testResourcesFolder = selectedProject.getFolder(DevkitUtils.TEST_RESOURCES_FOLDER);
				IContainer generatedResourcesFolder = testResourcesFolder.getFolder(new Path("generated"));
				
				
				testDataPath = findResourceInFolder(generatedResourcesFolder, testDataNameFormat);
				testDataOverridePath = findResourceInFolder(generatedResourcesFolder, testDataOverrideNameFormat);
				

				if ( testDataPath.equals("")){
					testDataPath = findResourceInFolder(testResourcesFolder, testDataNameFormat);
				}
				
				if ( testDataOverridePath.equals("")){
					testDataOverridePath = findResourceInFolder(testResourcesFolder, testDataOverrideNameFormat);
				}
				
				InteropRunOptionsSelectionDialog.testDataDefault = testDataPath;
				InteropRunOptionsSelectionDialog.testDataOverrideDefault = testDataOverridePath;

			} catch (CoreException e) {
				e.printStackTrace();
			}
		}


		private HttpPost createPostRequest() throws UnsupportedEncodingException {
			HttpPost httppost = new HttpPost(runnerConfig.get(ConfigKeys.serverURL));

			File testData = new File(runnerConfig.get(ConfigKeys.testDataPath));
			File override = new File(runnerConfig.get(ConfigKeys.testDataOverridePath));

			MultipartEntity mpEntity = new MultipartEntity();

			mpEntity.addPart("email", 
					new StringBody(runnerConfig.get(ConfigKeys.destinationEmail)));

			mpEntity.addPart("debug", 
					new StringBody(runnerConfig.get(ConfigKeys.selectedDebug)));

			mpEntity.addPart("logLevel", new StringBody(getLogLevel()));

			mpEntity.addPart("connectorRepository", 
					new StringBody(runnerConfig.get(ConfigKeys.repository)));

			mpEntity.addPart("testDataFile", new FileBody(testData, "Application/xml"));
			mpEntity.addPart("testDataOverrideFile", new FileBody(override, "Application/xml"));


			httppost.setEntity(mpEntity);
			return httppost;
		}


		private String getLogLevel(){
			return ( new Boolean(runnerConfig.get("selectedVerbose")) ? "verbose": "info" );
		}

		private Boolean getConfigurationAndContinue() {

			InteropRunOptionsSelectionDialog dialog= new InteropRunOptionsSelectionDialog(Display.getCurrent().getActiveShell());
			int returnStatus =  dialog.open();

			runnerConfig = dialog.getConfigKeys();

			return returnStatus == 0;
		}
		
		
		private String findResourceInFolder(IContainer folder, String resourceNameFormat)
				throws CoreException
		{
			
			String resourceName = "";
			
			IResource[] resources = folder.members();
			for(int i=0; i<resources.length; i++){

				IResource resource = resources[i];
				
				if (resource.getName().matches(resourceNameFormat) && resourceName.equals("")){
					resourceName = resource.getLocation().toOSString();
				}

				if ( !resourceName.equals(""))
					break;
			}
			
			return resourceName;
		}
}
