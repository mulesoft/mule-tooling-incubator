package org.mule.tooling.incubator.gradle.editors;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;


public class GradleDocumentSetupParticipant implements IDocumentSetupParticipant {

    @Override
    public void setup(IDocument document) {
        
        System.out.println("Called for document: " + document );
        IDocumentPartitioner partitioner = new FastPartitioner(new GradleDocumentPartitionerScanner(), GradleRuleBasedScanner.VALID_CONTENT_TYPES);
        document.setDocumentPartitioner(partitioner);
        //connect the partitioner to this document.
        partitioner.connect(document);
    }

}
