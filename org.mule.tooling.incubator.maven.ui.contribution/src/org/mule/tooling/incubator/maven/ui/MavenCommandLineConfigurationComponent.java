package org.mule.tooling.incubator.maven.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.LayoutConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.mule.tooling.ui.utils.UiUtils;
import org.mule.tooling.ui.wizards.extensible.PartStatusHandler;
import org.mule.tooling.ui.wizards.extensible.WizardContext;
import org.mule.tooling.ui.wizards.extensible.WizardPagePartExtension;

public class MavenCommandLineConfigurationComponent implements WizardPagePartExtension {

    private static final String TOOLTIP_TEXT = "<enter command line configuration parameters>";

    final private String baseCommandLine;

    public static final String KEY_MVN_COMMAND_LINE = "key.mvn.command.line";
    public static final String KEY_MVN_OFFLINE = "key.mvn.offline";
    public static final String KEY_MVN_UPDATE_SNAPSHOTS = "key.mvn.snapshots";
    public static final String KEY_MVN_QUIET = "key.mvn.quiet";
    public static final String KEY_MVN_CHECKSUM = "key.mvn.checksum";
    public static final String KEY_MVN_LOGGING_LEVEL = "key.mvn.logging.level";
    public static final String KEY_MVN_PROFILES = "key.mvn.profiles";
    public static final String KEY_MVN_GOALS = "key.mvn.goals";
    public static final String KEY_MVN_PROPERTIES = "key.mvn.properties";
    public static final String KEY_MVN_SKIP_TESTS = "key.mvn.skip.tests";
    
    private Text commandLineText;

    @WizardContext
    private PartStatusHandler statusHandler;

    @WizardContext
    private String commandLine;
    @WizardContext
    private Boolean skipTests;
    @WizardContext
    private Boolean offLine;
    @WizardContext
    private Boolean updateSnapshots;
    @WizardContext
    private Boolean quietMode;
    @WizardContext
    private String checksumPolicy;
    @WizardContext
    private String loggingLevel;
    @WizardContext
    private String goals;
    @WizardContext
    private String profiles;

    private Label effectiveCommand;

    public MavenCommandLineConfigurationComponent() {
        this(null);
    }

    public MavenCommandLineConfigurationComponent(String baseCommandLine) {
        this.baseCommandLine = baseCommandLine;
    }

    public Control createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);

        Group group = UiUtils.createGroupWithTitle(composite, "Maven command line arguments", 2);

        commandLineText = new Text(group, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        if (baseCommandLine != null) {
            createEffectiveCommandlineText(group);
        }
        UiUtils.addGrayedTooltipWhenEmpty(commandLineText, TOOLTIP_TEXT);
        commandLineText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                String text = commandLineText.getText();
                if (!text.equals(TOOLTIP_TEXT)) {
                    onCommandLineModified(text);
                }
            }
        });

        GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).span(2, 1).applyTo(commandLineText);
        GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(group);

        Point point = new Point(LayoutConstants.getSpacing().x, LayoutConstants.getSpacing().y * 3 / 2);
        GridLayoutFactory.swtDefaults().numColumns(2).spacing(point).applyTo(group);
        GridLayoutFactory.fillDefaults().margins(5, 5).applyTo(composite);
        GridDataFactory.fillDefaults().applyTo(composite);
        return composite;
    }

    private void createEffectiveCommandlineText(Group group) {
        Label effectiveCommandLabel = new Label(group, SWT.NONE);
        effectiveCommandLabel.setText("Command:");
        effectiveCommand = new Label(group, SWT.NONE);
        effectiveCommand.setText(baseCommandLine);
    }

    protected void onCommandLineModified(String text) {
        String newCommandLine = commandLineText.getText();

        if (baseCommandLine != null) {
            if (newCommandLine.equals(TOOLTIP_TEXT)) {
                commandLine = baseCommandLine;
            } else {
                commandLine = baseCommandLine + " " + newCommandLine;
            }
            effectiveCommand.setText(commandLine);
        }
        statusHandler.notifyUpdate(this, KEY_MVN_COMMAND_LINE, commandLine);
    }

    public PartStatusHandler getStatusHandler() {
        return statusHandler;
    }

    public void setStatusHandler(PartStatusHandler statusHandler) {
        this.statusHandler = statusHandler;
    }

    public String getCommandLine() {
        return commandLine;
    }

    public void setCommandLine(String commandLine) {
        this.commandLine = commandLine;
        if (commandLine.startsWith(baseCommandLine)) {
            String commandlineWithoutBase;
            if (baseCommandLine.isEmpty()) {
                commandlineWithoutBase = commandLine;
            } else if (commandLine.trim().equals(baseCommandLine.trim())) {
                commandlineWithoutBase = "";
            } else {
                commandlineWithoutBase = commandLine.substring(baseCommandLine.length() + 1);
            }
            commandLineText.setText(commandlineWithoutBase);
        } else {
            MavenUIPlugin.getDefault().logWarning(
                    "Trying to initialize Maven command line editor with a command line that does not start with the editor's base command line. (commandline=" + commandLine
                            + ", base=" + baseCommandLine + ")");
        }
    }

    @Override
    public void initializeDefaults() {
    }

    @Override
    public void performFinish(IProgressMonitor monitor) {
    }
    
}
