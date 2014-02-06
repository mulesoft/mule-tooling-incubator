package org.mule.tooling.ui.contribution.munit.editors;

import java.util.List;
import java.util.concurrent.Callable;

import javax.xml.bind.JAXBElement;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.contexts.IContextService;
import org.mule.tooling.core.MuleConfigurationsCache;
import org.mule.tooling.core.MuleConfigurationsCache.MuleConfigurationEntry;
import org.mule.tooling.core.StudioDesignContextRunner;
import org.mule.tooling.messageflow.editor.IPaletteCategoryFilter;
import org.mule.tooling.messageflow.editor.MessageFlowEditor;
import org.mule.tooling.messageflow.editor.MessageFlowEditorPaletteCategoryFilter;
import org.mule.tooling.messageflow.editpart.EntityEditPart;
import org.mule.tooling.messageflow.editpart.FlowEditPart;
import org.mule.tooling.messageflow.events.EditPartSelectedEvent;
import org.mule.tooling.model.messageflow.MessageFlowEntity;
import org.mule.tooling.model.messageflow.MuleConfiguration;
import org.mule.tooling.model.module.CategoryDefinition;
import org.mule.tooling.ui.contribution.munit.MunitPlugin;

/**
 * <p>
 * Munit flow editor. This is an extension of the {@link MessageFlowEditor} because it is basically the same thing. It keeps some differences though:
 * </p>
 * <ol>
 * <li>Has an extra toolkit to hide flows and show production code.</li>
 * <li>Shows the production code, to avoid users the context switching.</li>
 * <li>Handles the production code and test code at the same time</li>
 * </ol>
 */
public class MunitMessageFlowEditor extends MessageFlowEditor{

    private static boolean showTestsOnly = false;

    public static synchronized void showTestsOnly(boolean value){
        showTestsOnly = value;
    }
    
    @Override
    protected MunitConfigurationDecorator createModelRoot(
            MuleConfiguration config) {
        return new MunitConfigurationDecorator(config);
    }

 
    @Override
    protected void initListeners() {
        super.initListeners();
       
        addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                Object selectedEditPart = ((IStructuredSelection) event.getSelection()).getFirstElement();
                if (selectedEditPart instanceof EntityEditPart) {
                    final EntityEditPart<?> firstElement = (EntityEditPart<?>) selectedEditPart;
                    MunitPlugin.getEventBus().fireEvent(new EditPartSelectedEvent(muleConfiguration, firstElement));
                }
            }
        });

    }

    @Override
    protected IPaletteCategoryFilter createCategoryFilter() {
        return new MunitPalleteFilter();
    }

    public void reload() {
        reloadTests();
    }

    public void reloadTests() {
        EntityEditPart<?> editPart = ((EntityEditPart<?>) viewer.getContents());
        for ( Object child : editPart.getChildren()){
            if ( child instanceof FlowEditPart){
                    IFigure figure = ((EntityEditPart) child).getFigure();
                    figure.setVisible(!showTestsOnly);
                    
                    if ( showTestsOnly ){
                            hideConnections((EntityEditPart) child);
                    }
                    else{
                        showConnections((EntityEditPart) child);
                    }
                    
                }
               
            }
        getProductionMuleConfiguration();
        StudioDesignContextRunner.runSilentWithMuleProjectInUI( new Callable<Void>() {
			
			@Override
			public Void call() throws Exception {
			      ((EntityEditPart) viewer.getContents()).refresh();
			      ((EntityEditPart) viewer.getContents()).getFigure().repaint();
				return null;
			}
		}, project);
  
    }
    
    
    
    @Override
    protected void createForms(Composite parent) {
        super.createForms(parent);
        IContextService contextService = (IContextService) getSite().getService(IContextService.class);
        contextService.activateContext("org.mule.tooling.keybindings.contexts.munitEditor");
    }

    private void hideConnections(AbstractGraphicalEditPart abstractGraphicalEditPart) {
        if (abstractGraphicalEditPart == null) {
            return;
        }
        for (Object o : abstractGraphicalEditPart.getChildren()) {
            if (o instanceof AbstractGraphicalEditPart) {
                AbstractGraphicalEditPart child = (AbstractGraphicalEditPart) o;
                hideConnections(child);
                if (child instanceof EntityEditPart) {
                    EntityEditPart<?> p = (EntityEditPart<?>) child;
                    p.hideConnections();
                }
            }

        }
    }

    private void showConnections(AbstractGraphicalEditPart abstractGraphicalEditPart) {
        if (abstractGraphicalEditPart == null) {
            return;
        }
        for (Object o : abstractGraphicalEditPart.getChildren()) {
            if (o instanceof AbstractGraphicalEditPart) {
                AbstractGraphicalEditPart child = (AbstractGraphicalEditPart) o;
                showConnections(child);
                if (child instanceof EntityEditPart) {
                    EntityEditPart<?> p = (EntityEditPart<?>) child;
                    p.showConnections();
                }
            }

        }
    }


    public MuleConfiguration getProductionMuleConfiguration() {
        List<JAXBElement<? extends MessageFlowEntity>> globalEntries = modelRoot.getEntity().getGlobalEntries();

        ImportedFilesVisitor visitor = new ImportedFilesVisitor();
        for ( JAXBElement<? extends MessageFlowEntity> globalEntry : globalEntries){
            globalEntry.getValue().accept(visitor);
        }

        List<String> importedFiles = visitor.getFiles();
        MuleConfigurationsCache cache = MuleConfigurationsCache.getDefaultInstance();
        MuleConfiguration productionConfigurationFlows = new MuleConfiguration();
        List<MuleConfigurationEntry> configurationEntries = cache.getConfigurationEntries(getMuleProject());
        for ( MuleConfigurationEntry entry : configurationEntries){
            String fileName = entry.getConfigurationFile().getName().replace(".mflow", "");
            for ( String importedFile : importedFiles){
                if ( importedFile.contains(fileName) ){
                    productionConfigurationFlows.getFlows().addAll(entry.getMuleConfiguration().getFlows());
                }
            }
        }
        return productionConfigurationFlows;
    }
    

    private static class MunitPalleteFilter extends MessageFlowEditorPaletteCategoryFilter
    {

        @Override
        public boolean accepts(CategoryDefinition category) {
            return super.accepts(category) || (category.getId()!=null && category.getId().startsWith("org.mule.tooling.category.munit"));
        }

    }
    
    

}
