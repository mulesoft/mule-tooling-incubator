package org.mule.tooling.devkit.popup.actions;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;
import org.mule.tooling.devkit.common.DevkitUtils;
import org.mule.tooling.devkit.dialogs.InteropRunOptionsSelectionDialog;


public class RunAsRemoteInteropCommand extends AbstractHandler{

	public enum ConfigKeys {destinationEmail, testDataPath, testDataOverridePath,
		repository, serverURL, selectedDebug, selectedVerbose, selectedWindows, selectedLinux};

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
								HttpClient client = HttpClientBuilder.create().build();

								HttpPost httppost = createPostRequest();
								
								System.out.println("executing request " + httppost.getRequestLine());
								
								HttpResponse response = client.execute(httppost);
								showResponseInformationDialog(response);

								System.out.println(response.getEntity().toString());
								
							} catch (ConnectException e){
								e.printStackTrace();
								MessageDialog.openError(Display.getCurrent().getActiveShell(), "Unexpected error occurred", e.getLocalizedMessage());
								throw new RuntimeException(e.getMessage());
								
							} catch (ClientProtocolException e) {
								e.printStackTrace();
								MessageDialog.openError(Display.getCurrent().getActiveShell(), "Unexpected error occurred", e.getLocalizedMessage());
								throw new RuntimeException(e.getMessage());
								
							} catch (IOException e) {
								e.printStackTrace();
								MessageDialog.openError(Display.getCurrent().getActiveShell(), "Unexpected error occurred", e.getLocalizedMessage());
								throw new RuntimeException(e.getMessage());
							}
						}	

					}
				}
			}


			return null;
		}


		private void showResponseInformationDialog(HttpResponse response)
				throws IOException 
		{
			String message = EntityUtils.toString(response.getEntity());
			
			switch (response.getStatusLine().getStatusCode()) {
			case HttpStatus.SC_OK:
				MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Server response", message);
				break;

			case HttpStatus.SC_NOT_FOUND:
				MessageDialog.openError(Display.getCurrent().getActiveShell(), "Server response", "An unexpected error occurred: " + response.getStatusLine());
				break;

			case HttpStatus.SC_REQUEST_TIMEOUT:
				MessageDialog.openError(Display.getCurrent().getActiveShell(), "Server Timeout", "The connection timed out");
				break;

			default :
				MessageDialog.openError(Display.getCurrent().getActiveShell(), "Unexpected error", "An unexpected error occurred: " + response.getStatusLine());
				break;
			}	
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
			
			MultipartEntityBuilder mpBuilder = MultipartEntityBuilder.create();        

			
			mpBuilder.addTextBody("email", runnerConfig.get(ConfigKeys.destinationEmail));

			mpBuilder.addTextBody("debug", runnerConfig.get(ConfigKeys.selectedDebug));

			mpBuilder.addTextBody("logLevel", getLogLevel());

			mpBuilder.addTextBody("connectorRepository", runnerConfig.get(ConfigKeys.repository));

			mpBuilder.addBinaryBody("testDataFile", testData, ContentType.APPLICATION_XML, testData.getName());
			mpBuilder.addBinaryBody("testDataOverrideFile", override, ContentType.APPLICATION_XML, override.getName());


			httppost.setEntity(mpBuilder.build());
			
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
