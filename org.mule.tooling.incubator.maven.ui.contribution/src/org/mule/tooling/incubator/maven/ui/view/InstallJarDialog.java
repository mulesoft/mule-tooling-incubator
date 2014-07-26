package org.mule.tooling.incubator.maven.ui.view;

import java.io.File;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.versioning.VersionRange;
import org.codehaus.plexus.util.StringUtils;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.mule.tooling.ui.MuleUiConstants;
import org.mule.tooling.ui.utils.UiUtils;

public class InstallJarDialog extends TitleAreaDialog {

    public InstallJarDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    public void create() {
        super.create();
        setTitle("Install Jar into local repository");
        setMessage("Install the selected jar using the information provided");
    }

    Text groupId;
    String gId;
    Text artifactId;
    String aId;
    Text version;
    String v;
    Text jar;
    String jarPath;
    Text pom;
    String pomPath;

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);

        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout(1, false);
        container.setLayout(layout);
        layout.verticalSpacing = 6;

        GridData gdata = new GridData();
        gdata.horizontalAlignment = GridData.FILL;
        gdata.grabExcessHorizontalSpace = true;

        container.setLayoutData(gdata);
        Group jarGroupBox = UiUtils.createGroupWithTitle(container, "Configure jar", 3);
        groupId = createTextInput(jarGroupBox, "Group Id", "groupId", 2);
        ModifyListener listener = new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                gId = groupId.getText();
                aId = artifactId.getText();
                v = version.getText();
                jarPath = jar.getText();
                pomPath = pom.getText();
            }

        };

        artifactId = createTextInput(jarGroupBox, "Artifact Id", "artifactId", 2);
        groupId.addModifyListener(listener);
        version = createTextInput(jarGroupBox, "Version", "1.0.0-SNAPSHOT", 2);
        jar = createTextInput(jarGroupBox, "Jar File", "");

        createBrowser(jarGroupBox, new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell());
                fileDialog.setText("Select Jar");

                fileDialog.setFilterExtensions(new String[] { "jar" });

                String selected = fileDialog.open();

                jar.setText(selected);
            }
        });

        pom = createTextInput(jarGroupBox, "Pom File", "");

        createBrowser(jarGroupBox, new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell());
                fileDialog.setText("Select Pom");

                fileDialog.setFileName("pom");
                fileDialog.setFilterExtensions(new String[] { "xml" });

                String selected = fileDialog.open();

                pom.setText(selected);
            }
        });
        groupId.addModifyListener(listener);
        artifactId.addModifyListener(listener);
        version.addModifyListener(listener);
        jar.addModifyListener(listener);
        return area;
    }

    private Text createTextInput(Composite container, String label, String defaultValue) {

        Text textField = initializeTextField(container, label, defaultValue, 1);
        textField.setText(defaultValue);
        return textField;
    }

    private Text createTextInput(Composite container, String label, String defaultValue, int inputSpan) {

        Text textField = initializeTextField(container, label, defaultValue, inputSpan);
        textField.setText(defaultValue);
        return textField;
    }

    private Text initializeTextField(Composite container, String labelText, String defaultValue, int span) {

        createLabel(container, labelText);

        Text textField = new Text(container, SWT.BORDER);

        GridData dataFileName = new GridData();
        dataFileName.grabExcessHorizontalSpace = true;
        dataFileName.horizontalAlignment = SWT.FILL;
        dataFileName.horizontalSpan = span;

        textField.setLayoutData(dataFileName);
        textField.setText(defaultValue);

        return textField;
    }

    private void createLabel(Composite container, String text) {

        Label label = new Label(container, SWT.NULL);
        label.setText(text);
        label.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).hint(MuleUiConstants.LABEL_WIDTH, SWT.DEFAULT).create());

    }

    private void createBrowser(Composite container, SelectionAdapter selectionListener) {

        Button button = new Button(container, SWT.PUSH);
        GridData dataButton = new GridData();
        dataButton.grabExcessHorizontalSpace = false;
        dataButton.horizontalAlignment = SWT.RIGHT;
        dataButton.horizontalSpan = 1;
        button.setText("Browse");
        button.setLayoutData(dataButton);
        button.addSelectionListener(selectionListener);

    }

    public Artifact getArtifact() {
        return new DefaultArtifact(gId, aId, VersionRange.createFromVersion(v), "", "", "", null);
    }

    public File getJarFile() {
        return new File(jarPath);
    }

    public File getPomFile() {
        if (StringUtils.isEmpty(pomPath)) {
            return null;
        }
        return new File(pomPath);
    }
}
