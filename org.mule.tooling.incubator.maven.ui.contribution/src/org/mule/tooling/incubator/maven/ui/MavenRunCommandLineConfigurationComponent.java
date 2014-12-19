package org.mule.tooling.incubator.maven.ui;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.LayoutConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.mule.tooling.maven.cmdline.MavenCommandLine;
import org.mule.tooling.maven.cmdline.MavenProperty;
import org.mule.tooling.ui.utils.UiUtils;
import org.mule.tooling.ui.wizards.extensible.PartStatusHandler;
import org.mule.tooling.ui.wizards.extensible.WizardContext;
import org.mule.tooling.ui.wizards.extensible.WizardPagePartExtension;

public class MavenRunCommandLineConfigurationComponent implements WizardPagePartExtension {

    private final class UpdateSnapshotsMutualExclusionListener extends SelectionAdapter {

        @Override
        public void widgetSelected(SelectionEvent e) {
            if (updateSnapshots.equals(e.widget) && updateSnapshots.getSelection()) {
                if (noSnapshotUpdates.getSelection()) {
                    noSnapshotUpdates.setSelection(false);
                    updateEffectiveCommandLine();
                }
            } else if (noSnapshotUpdates.equals(e.widget) && noSnapshotUpdates.getSelection()) {
                if (updateSnapshots.getSelection()) {
                    updateSnapshots.setSelection(false);
                    updateEffectiveCommandLine();
                }
            }
        }
    }

    private static class Option {

        private String label, value;

        static Option get(String label, String value) {
            Option option = new Option();
            option.label = label;
            option.value = value;
            return option;
        }

        public String getLabel() {
            return label;
        }

        public String getValue() {
            return value;
        }
    }

    private static class OptionLabelProvider extends LabelProvider {

        @Override
        public String getText(Object element) {
            Option option = (Option) element;
            return option.getLabel();
        }
    }

    private static final Option[] LOGGING_LEVELS = new Option[] { Option.get("INFO", ""), Option.get("DEBUG", "-X"), Option.get("ERROR", "-e") };
    private static final Option[] CHECKSUM_OPTIONS = new Option[] { Option.get("none", ""), Option.get("Lax", "-c"), Option.get("Strict", "-C") };

    private static final String TOOLTIP_TEXT = "<enter command line configuration parameters>";
    private Text goals;
    private Text profiles;
    private Text properties;
    private Button skipTests;
    private Button workOffline;
    private Button updateSnapshots;
    private Button noSnapshotUpdates;
    private Button quiet;
    private ComboViewer checksum;
    private ComboViewer loggingLevel;
    @WizardContext
    private PartStatusHandler statusHandler;

    @WizardContext
    private String commandLine;

    private Text effectiveCommand;

    private UpdateEffectiveCommandListener updateEffectiveComandListener = new UpdateEffectiveCommandListener(this);

    public Control createControl(Composite parent) {

        Composite composite = new Composite(parent, SWT.NULL);

        createGoalsProfilesGroup(composite);
        createPropertiesGroup(composite);
        createGeneralArgumentsGroup(composite);
        createEffectiveCommandGroup(composite);

        GridLayoutFactory.fillDefaults().margins(5, 5).applyTo(composite);
        GridDataFactory.fillDefaults().applyTo(composite);
        return composite;
    }

    private void createGoalsProfilesGroup(Composite composite) {
        Group group = UiUtils.createGroupWithTitle(composite, "Maven command line arguments", 2);
        GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(group);

        Point point = new Point(LayoutConstants.getSpacing().x, LayoutConstants.getSpacing().y * 3 / 2);
        GridLayoutFactory.swtDefaults().numColumns(2).spacing(point).applyTo(group);
        GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(group);

        goals = createTextSection(group, "Goals:");
        profiles = createTextSection(group, "Profiles:");
        profiles.addModifyListener(updateEffectiveComandListener);
    }

    private void createPropertiesGroup(Composite composite) {
        Group propertiesGroup = UiUtils.createGroupWithTitle(composite, "Other properties", 2);
        Label effectiveCommandLabel = new Label(propertiesGroup, SWT.NONE);
        effectiveCommandLabel.setText("Properties:");
        properties = new Text(propertiesGroup, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
        properties.setText("");
        properties.addModifyListener(updateEffectiveComandListener);
        GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).hint(200, 40).grab(true, false).applyTo(properties);
        GridDataFactory.swtDefaults().applyTo(effectiveCommandLabel);
    }

    private void createGeneralArgumentsGroup(Composite composite) {
        Group generalArgumentsGroup = UiUtils.createGroupWithTitle(composite, "General arguments", 2);
        skipTests = createCheckedButton("Skip Tests", generalArgumentsGroup);
        workOffline = createCheckedButton("Work offline", generalArgumentsGroup);
        updateSnapshots = createCheckedButton("Force snapshot updates", generalArgumentsGroup);
        noSnapshotUpdates = createCheckedButton("Prevent snapshot updates", generalArgumentsGroup);
        SelectionAdapter updateSnapshotsMutualExclusionListener = new UpdateSnapshotsMutualExclusionListener();
        updateSnapshots.addSelectionListener(updateSnapshotsMutualExclusionListener);
        noSnapshotUpdates.addSelectionListener(updateSnapshotsMutualExclusionListener);
        quiet = createCheckedButton("Quiet output", generalArgumentsGroup);
        checksum = createComboButton("Checksum policy", CHECKSUM_OPTIONS, generalArgumentsGroup, new OptionLabelProvider());

        goals.addModifyListener(updateEffectiveComandListener);
        loggingLevel = createComboButton("Logging level", LOGGING_LEVELS, generalArgumentsGroup, new OptionLabelProvider());
        GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(generalArgumentsGroup);
    }

    private void createEffectiveCommandGroup(Composite composite) {
        Group effectiveCommandLineGroup = UiUtils.createGroupWithTitle(composite, "Effective command line", 2);
        Label effectiveCommandLabel = new Label(effectiveCommandLineGroup, SWT.NONE);
        effectiveCommandLabel.setText("Command:");
        effectiveCommand = new Text(effectiveCommandLineGroup, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL | SWT.READ_ONLY);

        effectiveCommand.setBackground(effectiveCommandLabel.getBackground());
        GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).hint(200, 40).applyTo(effectiveCommand);
        GridDataFactory.swtDefaults().applyTo(effectiveCommandLabel);

        UiUtils.addGrayedTooltipWhenEmpty(effectiveCommand, TOOLTIP_TEXT);
        effectiveCommand.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                statusHandler.notifyUpdate(MavenRunCommandLineConfigurationComponent.this, MavenCommandLineConfigurationComponent.KEY_MVN_COMMAND_LINE, effectiveCommand.getText());
            }
        });
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
        checkButton.addSelectionListener(updateEffectiveComandListener);
        checkButton.setText(label);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(composite);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).span(2, 1).grab(true, false).applyTo(composite);
        return checkButton;
    }

    private ComboViewer createComboButton(String label, Object[] options, Group group, IBaseLabelProvider labelProvider) {
        Label effectiveCommandLabel = new Label(group, SWT.NONE);
        effectiveCommandLabel.setText(label);
        ComboViewer comboViewer = new ComboViewer(group, SWT.READ_ONLY);
        comboViewer.setContentProvider(new ArrayContentProvider());
        comboViewer.setInput(options);
        comboViewer.setLabelProvider(labelProvider);
        comboViewer.setSelection(new StructuredSelection(options[0]));
        comboViewer.addSelectionChangedListener(updateEffectiveComandListener);
        return comboViewer;
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
        this.effectiveCommand.setText(commandLine);
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
            effectiveCommand.append(" -P" + getProfilesString());
        }
        if (skipTests.getSelection()) {
            effectiveCommand.append(" -DskipTests");
        }
        effectiveCommand.append(" ");
        effectiveCommand.append(getOtherProperties().trim());
        if (workOffline.getSelection()) {
            effectiveCommand.append(" -o");
        }
        if (updateSnapshots.getSelection()) {
            effectiveCommand.append(" -U");
        }
        if (noSnapshotUpdates.getSelection()) {
            effectiveCommand.append(" -nsu");
        }
        if (quiet.getSelection()) {
            effectiveCommand.append(" -q");
        }
        effectiveCommand.append(" ");
        String checksumPolicy = getChecksumPolicy();
        if (!checksumPolicy.isEmpty()) {
            effectiveCommand.append(checksumPolicy);
            effectiveCommand.append(" ");
        }
        String loggingLevel = getLoggingLevel();
        if (!loggingLevel.isEmpty()) {
            effectiveCommand.append(loggingLevel);
        }
        this.effectiveCommand.setText(effectiveCommand.toString());
        this.commandLine = effectiveCommand.toString();
        statusHandler.notifyUpdate(this, MavenCommandLineConfigurationComponent.KEY_MVN_COMMAND_LINE, commandLine);
    }

    private String getProfilesString() {
        StringBuilder profilesString = new StringBuilder();
        String text = profiles.getText();
        List<String> profiles = Arrays.asList(text.split(","));
        Iterator<String> iterator = profiles.iterator();
        while (iterator.hasNext()) {
            profilesString.append(iterator.next().trim());
            if (iterator.hasNext()) {
                profilesString.append(",");
            }
        }
        return profilesString.toString();
    }

    private String getOtherProperties() {
        String commaSeparateValues = properties.getText();
        if (commaSeparateValues.isEmpty())
            return "";
        String[] values = commaSeparateValues.split(",");
        String result = org.apache.commons.lang.StringUtils.join(values, " -D");
        return "-D" + result;
    }

    private String getChecksumPolicy() {
        ISelection selection = checksum.getSelection();
        if (!selection.isEmpty()) {
            Option option = (Option) ((StructuredSelection) selection).getFirstElement();
            return option.getValue();
        }
        return null;
    }

    private String getLoggingLevel() {
        ISelection selection = loggingLevel.getSelection();
        if (!selection.isEmpty()) {
            Option option = (Option) ((StructuredSelection) selection).getFirstElement();
            return option.getValue();
        }
        return null;
    }

    public void setGoals(String goalds) {
        goals.setText(goalds.trim());
        updateEffectiveCommandLine();
    }

    public void setProperties(List<MavenProperty> properties) {
        this.properties.setText(buildPropertiesString(properties));
        updateEffectiveCommandLine();
    }

    private String buildPropertiesString(List<MavenProperty> properties) {
        StringBuilder propertiesString = new StringBuilder();
        Iterator<MavenProperty> iterator = properties.iterator();

        while (iterator.hasNext()) {
            MavenProperty mavenProperty = iterator.next();
            if (!isSkipTest(mavenProperty)) {
                buildPropertyString(propertiesString, mavenProperty);
                if (iterator.hasNext()) {
                    propertiesString.append(",");
                }
            }
        }
        return propertiesString.toString();
    }

    private boolean isSkipTest(MavenProperty mavenProperty) {
        return MavenProperty.SKIP_TEST_SHORT.equals(mavenProperty) || MavenProperty.SKIP_TEST_LONG.equals(mavenProperty);
    }

    private void buildPropertyString(StringBuilder propertiesString, MavenProperty mavenProperty) {
        propertiesString.append(mavenProperty.getKey());
        String value = mavenProperty.getValue();
        if (value != null && !value.isEmpty()) {
            propertiesString.append("=");
            propertiesString.append(value);
        }
    }

    private void setOfflineMode(Boolean offlineMode) {
        workOffline.setSelection(offlineMode);
    }

    private void setSkipTests(Boolean skip) {
        skipTests.setSelection(skip);
    }

    private void setProfiles(String profiles) {
        this.profiles.setText(profiles);
    }

    private void setQuietMode(Boolean quietMode) {
        this.quiet.setSelection(quietMode);
    }

    private void setLoggingLevel(Option option) {
        loggingLevel.setSelection(new StructuredSelection(option));
    }

    private void setChecksumPolicy(Option option) {
        checksum.setSelection(new StructuredSelection(option));
    }

    private void setUpdateSnapshots(boolean update) {
        this.updateSnapshots.setSelection(update);
    }

    private void setNoSnapshotUpdates(boolean update) {
        this.noSnapshotUpdates.setSelection(update);
    }

    public void loadFrom(MavenCommandLine mavenCommandLine) {
        String goals = StringUtils.join(mavenCommandLine.getGoals(), " ");
        this.setCommandLine(mavenCommandLine.getCommand());
        this.setGoals(goals);
        List<MavenProperty> properties = mavenCommandLine.getProperties();
        Boolean skip = properties.contains(MavenProperty.SKIP_TEST_LONG) || properties.contains(MavenProperty.SKIP_TEST_SHORT);
        this.setSkipTests(skip);
        this.setProperties(properties);
        Boolean offlineMode = mavenCommandLine.isOffline();
        this.setOfflineMode(offlineMode);
        Boolean quietMode = mavenCommandLine.isQuiet();
        this.setQuietMode(quietMode);
        String profiles = StringUtils.join(mavenCommandLine.getProfiles(), ", ");
        this.setProfiles(profiles);
        if (mavenCommandLine.isStrictChecksum()) {
            this.setChecksumPolicy(CHECKSUM_OPTIONS[2]);
        } else if (mavenCommandLine.isLaxChecksum()) {
            this.setChecksumPolicy(CHECKSUM_OPTIONS[1]);
        }

        if (mavenCommandLine.isDebug()) {
            this.setLoggingLevel(LOGGING_LEVELS[1]);
        } else if (mavenCommandLine.isLogErrors()) {
            this.setLoggingLevel(LOGGING_LEVELS[2]);
        }
        this.setUpdateSnapshots(mavenCommandLine.isUpdateSnapshots());
        this.setNoSnapshotUpdates(mavenCommandLine.isNoSnapshotUpdates());
        updateEffectiveCommandLine();
    }

    private static class UpdateEffectiveCommandListener implements ModifyListener, ISelectionChangedListener, SelectionListener {

        private MavenRunCommandLineConfigurationComponent component;

        public UpdateEffectiveCommandListener(MavenRunCommandLineConfigurationComponent component) {
            this.component = component;
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            component.updateEffectiveCommandLine();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            component.updateEffectiveCommandLine();
        }

        @Override
        public void modifyText(ModifyEvent e) {
            component.updateEffectiveCommandLine();
        }

        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            component.updateEffectiveCommandLine();
        }
    }
}
