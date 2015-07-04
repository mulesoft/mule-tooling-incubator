package org.mule.tooling.incubator.gradle.template;

import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.mule.tooling.incubator.gradle.model.GradleProject;

public class VelocityReplacer implements Replacer {

    final private GradleProject model;
    final private HashMap<String, Object> mapModel;
    
    public VelocityReplacer(GradleProject project) {
        model = project;
        mapModel = null;
    }
    
    public VelocityReplacer(HashMap<String, Object> mapModel) {
		this.model = null;
    	this.mapModel = mapModel;
	}




	@Override
    public void replace(Reader reader, Writer writer) throws Exception {
        Velocity.init();
        VelocityContext context = new VelocityContext();
        
        //avoid having to copy the entire set of properties.
        //see template.
        if (model != null) {
        	context.put("project", this.model);
        }
        
        if (mapModel != null) {
        	context.put("model", this.mapModel);
        }
        
        boolean evaluate = Velocity.evaluate(context, writer, "velocity class rendering", reader);

        if (evaluate == false) {
            throw new Exception("Evaluation of the template failed.");
        }

    }

}
