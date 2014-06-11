package org.mule.tooling.ui.contribution.munit.editors;


import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.PlatformUI;
import org.mule.tooling.core.MuleCorePlugin;
import org.mule.tooling.core.cache.IXmlConfigurationProvider;
import org.mule.tooling.messageflow.editor.MessageFlowEditor;
import org.mule.tooling.messageflow.editor.MultiPageMessageFlowEditor;
import org.mule.tooling.model.messageflow.util.ObjectHolder;
/**
 * <p>
 * The {@link MultiPageMessageFlowEditor} for Munit, the only difference is that it creates a {@link MunitMessageFlowEditor}
 * <p>
 */
public class MunitMultiPageEditor extends MultiPageMessageFlowEditor implements IResourceChangeListener {

    public MunitMultiPageEditor() {
        setFlowEditor(createMessageFlowEditor());
    }

    @Override
    protected MessageFlowEditor createMessageFlowEditor() {
        return new MunitMessageFlowEditor(new IXmlConfigurationProvider() {

            @Override
            public String getXml() throws Exception {
                final ObjectHolder<String> result = new ObjectHolder<String>();
                PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            result.set(generateXmlConfiguration());
                        } catch (CoreException e) {
                            MuleCorePlugin.logError("Error while generating xml", e);
                        }

                    }
                });
                return result.get();
            }
        });
    }

}
