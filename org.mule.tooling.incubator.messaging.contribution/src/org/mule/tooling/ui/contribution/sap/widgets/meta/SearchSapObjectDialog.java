package org.mule.tooling.ui.contribution.sap.widgets.meta;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.mule.common.Result;
import org.mule.common.Result.Status;
import org.mule.common.metadata.MetaDataKey;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.core.utils.CoreUtils;
import org.mule.tooling.messageflow.action.ExecuteGlobalElementArtifactMethodFromMuleConfigAction;
import org.mule.tooling.messageflow.util.MessageFlowUtils;
import org.mule.tooling.metadata.utils.MetadataUtils;
import org.mule.tooling.model.messageflow.MessageFlowNode;
import org.mule.tooling.model.messageflow.MuleConfiguration;
import org.mule.tooling.ui.contribution.sap.metadata.ExecuteSapObjectSearchCallback;
import org.mule.tooling.ui.contribution.sap.widgets.ExportXmlTemplateAction;
import org.mule.tooling.ui.contribution.sap.widgets.ExportXsdAction;
import org.mule.tooling.ui.contribution.sap.widgets.ResultTableLabelProvider;
import org.mule.tooling.ui.modules.core.widgets.AttributesPropertyPage;

import com.mulesoft.mule.transport.sap.Searchable;

public class SearchSapObjectDialog extends BaseSapDialog {

    private static final String RESULTS_LABEL = "Results";

    private Button searchButton = null;
    private Text filterNameText = null;
    private TableViewer resultsTableViewer = null;
    private Group searchResultsGroup = null;
    private Composite composite = null;
    private MetaDataKey selectedResultElement = null;
    
    public SearchSapObjectDialog(Shell parentShell, AttributesPropertyPage page) {
        super(parentShell, page);
    }

    public MetaDataKey getSelectedResultElement() {
        return selectedResultElement;
    }

    private void setSelectedResultElement(MetaDataKey selectedResultElement) {
        this.selectedResultElement = selectedResultElement;
    }
    
    public String getSelectedSapObjectName() {
        MetaDataKey element = getSelectedResultElement();
        return element != null ? element.getId() : null;
    }
    
    @Override
    protected Control createDialogArea(Composite parent) {

        setTitle("SAP " + getSapTypeDescription() + " search");
        setMessage("SAP " + getSapTypeDescription() + " search to select the SAP object. You can also export XML templates/examples or the SAP object XSD.");

        composite = (Composite) super.createDialogArea(parent);
        GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false).applyTo(composite);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(composite);

        Group connectionSettingsGroup = new Group(composite, SWT.NULL);
        connectionSettingsGroup.setText(getSapTypeDescription() + " Filter");
        connectionSettingsGroup.setLayout(new GridLayout(3, false));
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).hint(SWT.DEFAULT, SWT.DEFAULT).grab(true, true).span(2, 1).applyTo(connectionSettingsGroup);

        Label filterNameLabel = new Label(connectionSettingsGroup, SWT.NONE);
        filterNameLabel.setText(getSapTypeDescription() + " name:");
        filterNameLabel.setToolTipText(getSapTypeDescription() + " search filter. Use * as wildcard in your filter");
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).hint(SWT.DEFAULT, SWT.DEFAULT).grab(false, false).span(1, 1).applyTo(filterNameLabel);

        filterNameText = new Text(connectionSettingsGroup, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).hint(SWT.DEFAULT, SWT.DEFAULT).grab(true, false).span(1, 1).applyTo(filterNameText);
        filterNameText.addKeyListener(new KeyAdapter(){ 
            public void keyPressed(KeyEvent e) {
                String filter = filterNameText != null ? filterNameText.getText().trim() : "";
                boolean canSearch = filter.length() >= 0;
                searchButton.setEnabled(canSearch);
                if (canSearch && (e.keyCode == SWT.LF || e.keyCode == SWT.CR)) {
                    doSearch();
                }
            }
        });
        
        searchButton = new Button(connectionSettingsGroup, SWT.PUSH);
        searchButton.setText("Search");
        searchButton.setEnabled(false);
        searchButton.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                doSearch();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).hint(SWT.DEFAULT, SWT.DEFAULT).grab(false, false).span(1, 1).applyTo(searchButton);

        searchResultsGroup = new Group(composite, SWT.NULL);
        searchResultsGroup.setText(RESULTS_LABEL);
        GridLayoutFactory.swtDefaults().numColumns(1).equalWidth(false).applyTo(searchResultsGroup);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).hint(SWT.DEFAULT, SWT.DEFAULT).grab(true, true).span(2, 1).applyTo(searchResultsGroup);

        TableLayout tableLayout = new TableLayout();
        tableLayout.addColumnData(new ColumnWeightData(1));
        tableLayout.addColumnData(new ColumnWeightData(1));
        final Table resultsTable = new Table(searchResultsGroup, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL);
        resultsTable.setLinesVisible(true);
        resultsTable.setHeaderVisible(true);
        resultsTable.setLayout(tableLayout); 
        
        resultsTableViewer = new TableViewer(resultsTable);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).hint(SWT.DEFAULT, 250).grab(true, true).applyTo(resultsTableViewer.getControl());
        TableViewerColumn sapObjectColumn = new TableViewerColumn(resultsTableViewer, SWT.NONE);
        sapObjectColumn.getColumn().setText(getSapTypeDescription());

        TableViewerColumn descriptionColumn = new TableViewerColumn(resultsTableViewer, SWT.NONE);
        descriptionColumn.getColumn().setText("Description");
        
        resultsTableViewer.setContentProvider(new ArrayContentProvider());
        resultsTableViewer.setLabelProvider(new ResultTableLabelProvider());
        resultsTableViewer.setInput(new ArrayList<MetaDataKey>());
        resultsTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                selectResult();
                StructuredSelection selection = (StructuredSelection) event.getSelection();
                setSelectedResultElement((MetaDataKey) selection.getFirstElement());
            }
        });

        MenuManager menuMgr = new MenuManager("#ExportSapObjectsMenu");
        menuMgr.setRemoveAllWhenShown(true);
        Menu menu = menuMgr.createContextMenu(resultsTableViewer.getControl());
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                if (isIDocType()) {
                    manager.add(new ExportXsdContextMenuAction(null));
                    manager.add(new ExportXmlContextMenuAction(null));
                } else if(isFunctionType()) {
                    manager.add(new ExportXsdContextMenuAction(new Integer(1)));
                    manager.add(new ExportXmlContextMenuAction(new Integer(1)));
                    manager.add(new Separator());
                    manager.add(new ExportXsdContextMenuAction(new Integer(2)));
                    manager.add(new ExportXmlContextMenuAction(new Integer(2)));
                }
            }
        });        
        resultsTableViewer.getControl().setMenu(menu);
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart().getSite().registerContextMenu(menuMgr, resultsTableViewer);
        
        
        return composite;
    }

    private void doSearch() {
        IMuleProject muleProject = getPage().getMuleProject();
        MuleConfiguration muleConfiguration = MessageFlowUtils.getMuleConfigFromActivePage();
        final MessageFlowNode node = getPage().getNode();
        final MessageFlowNode nodeToBeTested = MessageFlowUtils.generateNodeToTest(node, getPage().getHost());
        final String configRefName = MetadataUtils.getGlobalReference(nodeToBeTested);
        MessageFlowNode globalElement = CoreUtils.retrieveGlobalElement(muleProject, configRefName);

        ExecuteSapObjectSearchCallback callback = new ExecuteSapObjectSearchCallback();
        callback.setFilter(filterNameText.getText());
        callback.setType(getPropertyValue("type"));
        
        ExecuteGlobalElementArtifactMethodFromMuleConfigAction action = new ExecuteGlobalElementArtifactMethodFromMuleConfigAction(muleProject, muleConfiguration,
                Searchable.class.getClassLoader(), globalElement, callback);

        action.run();
        Result<?> result = action.getResult();
        
        if (!action.isInterrupted() && result != null) {
            if (result.getStatus() == Status.SUCCESS) {
                List<?> resultList = (List<?>) result.get();
                
                if (resultList != null && resultList.size() > 0) {
                    searchResultsGroup.setText(RESULTS_LABEL +  " (Found " + resultList.size() + " results matching '" + filterNameText.getText() + "')");
                } else {
                    searchResultsGroup.setText(RESULTS_LABEL +  " (No results found matching '" + filterNameText.getText() + "')");;
                }
                composite.layout();
                // Clean table first
                resultsTableViewer.setInput(null);
                // This may take a while when returning a long result
                resultsTableViewer.setInput(resultList);
                unselectResult();
            }
        } else {
            // User hit cancel. Nothing should change
            
        }
    }
    
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        getButton(IDialogConstants.OK_ID).setText("Select");
        getButton(IDialogConstants.OK_ID).setEnabled(false);
    }
    
    private void unselectResult() {
        setEnabledButtons(false);
    }

    private void selectResult() {
        setEnabledButtons(true);
    }

    private void setEnabledButtons(boolean enabled) {
        if (getButton(IDialogConstants.OK_ID) != null) {
            getButton(IDialogConstants.OK_ID).setEnabled(enabled);
        }
    }
    
    private boolean isResultRowSelected() {
        return resultsTableViewer.getSelection() != null && !resultsTableViewer.getSelection().isEmpty();
    }
    
    private class ExportXsdContextMenuAction extends Action {
        private Integer xmlVersion;
        
        public ExportXsdContextMenuAction(Integer version) {
            String text = "Export XSD" + (version != null ? " (v" + version.toString() + ")" : "");
            setToolTipText(text);
            setText(text);
            this.xmlVersion = version;
        }

        @Override
        public boolean isEnabled() {
            return isResultRowSelected();
        }

        @Override
        public void run() {
            ExportXsdAction action = new ExportXsdAction();
            action.setSapObject(getSelectedSapObjectName());
            action.setXmlVersion(this.xmlVersion);
            action.click(getPage());             
        }
    }
    
    private class ExportXmlContextMenuAction extends Action {
        private Integer xmlVersion;
        public ExportXmlContextMenuAction(Integer version) {
            String text = "Export XML Template" + (version != null ? " (v" + version.toString() + ")" : "");
            setToolTipText(text);
            setText(text);
            this.xmlVersion = version;
        }

        @Override
        public boolean isEnabled() {
            return isResultRowSelected();
        }

        @Override
        public void run() {
            ExportXmlTemplateAction action = new ExportXmlTemplateAction();
            action.setSapObject(getSelectedSapObjectName());
            action.setXmlVersion(this.xmlVersion);
            action.click(getPage());            
        }
    }    
}

