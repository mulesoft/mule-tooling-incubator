package org.mule.tooling.incubator.maven.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.LayoutConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.mule.tooling.ui.utils.UiUtils;
import org.mule.tooling.ui.wizards.extensible.PartStatusHandler;
import org.mule.tooling.ui.wizards.extensible.WizardContext;
import org.mule.tooling.ui.wizards.extensible.WizardPagePartExtension;

public class MavenRunCommandLineConfigurationComponent implements WizardPagePartExtension {

    private static final String[] LOGGING_LEVELS = new String[] { "INFO", "DEBUG", "ERROR" };
    private static final String TOOLTIP_TEXT = "<enter command line configuration parameters>";
    final private String baseCommandLine;
    private static final String[] CHECKSUM_OPTIONS = new String[] { "none", "Lax", "Strict" };
    private Text goals;
    private Text profiles;
    private Text other;
    private Button skipTests;
    private Button workOffline;
    private Button updateSnapshots;
    private Button quiet;
    private Combo checksum;
    private Combo loggingLevel;
    @WizardContext
    private PartStatusHandler statusHandler;

    @WizardContext
    private String commandLine;

    private Text effectiveCommand;

    public MavenRunCommandLineConfigurationComponent() {
        this(null);
    }

    public MavenRunCommandLineConfigurationComponent(String baseCommandLine) {
        this.baseCommandLine = baseCommandLine;
    }

    public Control createControl(Composite parent) {

        Composite composite = new Composite(parent, SWT.NULL);

        Group group = UiUtils.createGroupWithTitle(composite, "Maven command line arguments", 2);
        goals = createTextSection(group, "Goals:");
        profiles = createTextSection(group, "Profiles:");

        GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(group);

        Point point = new Point(LayoutConstants.getSpacing().x, LayoutConstants.getSpacing().y * 3 / 2);
        GridLayoutFactory.swtDefaults().numColumns(2).spacing(point).applyTo(group);
        GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(group);

        group = UiUtils.createGroupWithTitle(composite, "Other properties", 2);
        Label effectiveCommandLabel = new Label(group, SWT.NONE);
        effectiveCommandLabel.setText("Properties:");
        other = new Text(group, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
        GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.BEGINNING).grab(true, false).applyTo(other);
        GridDataFactory.swtDefaults().applyTo(effectiveCommandLabel);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.widthHint = 200;
        gd.heightHint = 40;
        other.setLayoutData(gd);
        other.setText("");

        group = UiUtils.createGroupWithTitle(composite, "General arguments", 2);
        skipTests = createCheckedButton("Skip Tests", group);
        workOffline = createCheckedButton("Work offline", group);
        ButtonChangeListener buttonListener = new ButtonChangeListener();
        workOffline.addSelectionListener(buttonListener);
        skipTests.addSelectionListener(buttonListener);
        updateSnapshots = createCheckedButton("Update Snapshots", group);
        updateSnapshots.addSelectionListener(buttonListener);
        quiet = createCheckedButton("Quiet output", group);
        quiet.addSelectionListener(buttonListener);
        checksum = createComboButton("Checksum policy", CHECKSUM_OPTIONS, group);
        ComboChangeListener comboChange = new ComboChangeListener();
        checksum.addModifyListener(comboChange);
        goals.addModifyListener(comboChange);
        profiles.addModifyListener(comboChange);
        loggingLevel = createComboButton("Logging level", LOGGING_LEVELS, group);
        loggingLevel.addModifyListener(comboChange);
        other.addModifyListener(comboChange);
        GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(group);
        GridLayoutFactory.fillDefaults().margins(5, 5).applyTo(composite);
        GridDataFactory.fillDefaults().applyTo(composite);

        group = UiUtils.createGroupWithTitle(composite, "Effective command line", 2);
        effectiveCommandLabel = new Label(group, SWT.NONE);
        effectiveCommandLabel.setText("Command:");
        effectiveCommand = new Text(group, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL | SWT.READ_ONLY);

        effectiveCommand.setBackground(effectiveCommandLabel.getBackground());
        GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.BEGINNING).grab(true, false).applyTo(effectiveCommand);
        GridDataFactory.swtDefaults().applyTo(effectiveCommandLabel);
        gd = new GridData(GridData.FILL_BOTH);
        gd.widthHint = 200;
        gd.heightHint = 40;
        effectiveCommand.setLayoutData(gd);
        effectiveCommand.setText(baseCommandLine);

        UiUtils.addGrayedTooltipWhenEmpty(effectiveCommand, TOOLTIP_TEXT);
        effectiveCommand.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                statusHandler.notifyUpdate(MavenRunCommandLineConfigurationComponent.this, MavenCommandLineConfigurationComponent.KEY_MVN_COMMAND_LINE, effectiveCommand.getText());
            }
        });
        return composite;
    }

    private Text createTextSection(Group group, String label) {
        Label effectiveCommandLabel = new Label(group, SWT.NONE);
        effectiveCommandLabel.setText(label);
        GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.BEGINNING).grab(false, false).applyTo(effectiveCommandLabel);
        Text commandLineText = new Text(group, SWT.BORDER | SWT.WRAP | SWT.LEFT | SWT.SINGLE);
        GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).applyTo(commandLineText);
        return commandLineText;
    }

    private Button createCheckedButton(String label, Group group) {
        Composite composite = new Composite(group, SWT.NONE | SWT.LEFT_TO_RIGHT);
        Button checkButton = new Button(composite, SWT.NONE | SWT.CHECK);
        checkButton.setSelection(false);
        Label effectiveCommandLabel = new Label(composite, SWT.NONE);
        effectiveCommandLabel.setText(label);
        GridDataFactory.fillDefaults().applyTo(effectiveCommandLabel);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(composite);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).span(2, 1).grab(true, false).applyTo(composite);
        return checkButton;
    }

    private Combo createComboButton(String label, String[] options, Group group) {
        Label effectiveCommandLabel = new Label(group, SWT.NONE);
        effectiveCommandLabel.setText(label);
        Combo comboButton = new Combo(group, SWT.NONE | SWT.READ_ONLY);
        comboButton.setItems(options);
        comboButton.setText(options[0]);
        return comboButton;
    }

    protected void onCommandLineModified(String text) {
        String newCommandLine = effectiveCommand.getText();

        if (baseCommandLine != null) {
            if (newCommandLine.equals(TOOLTIP_TEXT)) {
                commandLine = baseCommandLine;
            } else {
                if (newCommandLine.startsWith(baseCommandLine)) {
                    commandLine = newCommandLine;
                } else {
                    commandLine = baseCommandLine + " " + newCommandLine;
                }
            }
            effectiveCommand.setText(commandLine);
        }
        statusHandler.notifyUpdate(this, MavenCommandLineConfigurationComponent.KEY_MVN_COMMAND_LINE, commandLine);
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
            if (commandLine.trim().equals(baseCommandLine.trim())) {
                commandlineWithoutBase = "";
            } else {
                commandlineWithoutBase = commandLine.substring(baseCommandLine.length() + 1);
            }
            this.effectiveCommand.setText(commandlineWithoutBase);
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

    private void updateEffectiveCommandLine() {
        StringBuilder effectiveCommand = new StringBuilder();
        effectiveCommand.append("mvn");
        effectiveCommand.append(" " + goals.getText().trim());
        if (!profiles.getText().isEmpty()) {
            effectiveCommand.append(" -P" + profiles.getText().trim());
        }
        if (skipTests.getSelection()) {
            effectiveCommand.append(" -DskipTests");
        }
        if (workOffline.getSelection()) {
            effectiveCommand.append(" -o");
        }
        if (updateSnapshots.getSelection()) {
            effectiveCommand.append(" -U");
        }
        if (quiet.getSelection()) {
            effectiveCommand.append(" -q");
        }
        effectiveCommand.append(" ");
        effectiveCommand.append(getChecksumPolicy());
        effectiveCommand.append(getLogginLevel());
        effectiveCommand.append(getOtherProperties().trim());
        this.effectiveCommand.setText(effectiveCommand.toString());
    }

    private String getOtherProperties() {
        String commaSeparateValues = other.getText();
        if (commaSeparateValues.isEmpty())
            return "";
        String[] values = commaSeparateValues.split(",");
        String result = org.apache.commons.lang.StringUtils.join(values, " -D");
        return " -D" + result;
    }

    private String getChecksumPolicy() {
        if (checksum.getText().equals(CHECKSUM_OPTIONS[0]))
            return "";
        if (checksum.getText().equals(CHECKSUM_OPTIONS[1]))
            return " --lax-checksums";
        if (checksum.getText().equals(CHECKSUM_OPTIONS[2]))
            return " --strict-checksums";
        return null;
    }

    private String getLogginLevel() {
        if (loggingLevel.getText().equals(LOGGING_LEVELS[0]))
            return "";
        if (loggingLevel.getText().equals(LOGGING_LEVELS[1]))
            return " -X";
        if (loggingLevel.getText().equals(LOGGING_LEVELS[2]))
            return " -e";
        return null;
    }

    class ButtonChangeListener implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent e) {
            updateEffectiveCommandLine();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            updateEffectiveCommandLine();
        }

    }

    class ComboChangeListener implements ModifyListener {

        @Override
        public void modifyText(ModifyEvent e) {
            updateEffectiveCommandLine();
        }
    }

    public void setGoals(String goalds) {
        goals.setText(goalds.trim());
        updateEffectiveCommandLine();
    }

    public void setOfflineMode(Boolean offlineMode) {
        workOffline.setSelection(offlineMode);
        updateEffectiveCommandLine();
    }

    public void setSkipTests(Boolean skip) {
        skipTests.setSelection(skip);
        updateEffectiveCommandLine();
    }

    public void setProfiles(String profiles) {
        this.profiles.setText(profiles);
        updateEffectiveCommandLine();
    }

    public void setQuietMode(Boolean quietMode) {
        this.quiet.setSelection(quietMode);
        updateEffectiveCommandLine();
    }
}
