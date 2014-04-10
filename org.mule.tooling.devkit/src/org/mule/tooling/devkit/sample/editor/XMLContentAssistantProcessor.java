/*******************************************************************************
 * Copyright (c) 2005 Prashant Deva.
 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License - v 1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.mule.tooling.devkit.sample.editor;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateProposal;
import org.eclipse.swt.graphics.Image;
import org.mule.tooling.devkit.DevkitImages;

public class XMLContentAssistantProcessor implements IContentAssistProcessor {
	private static final String CONTEXT_ID = "devkit.sample";

	TemplateContextType templateContext = new TemplateContextType(CONTEXT_ID,
			"Samples Templates");

	Template template = new Template(
			"Sample",
			"Create a sample with no attributes or childs",
			CONTEXT_ID,
			"<!-- BEGIN_INCLUDE(${prefix}:${operation}) -->\n" +
			"\t<${prefix}:${operation}/>\n" +
			"<!-- END_INCLUDE(${prefix}:${operation}) -->\n",
			true);

	Template templateWithAttribute = new Template(
			"Sample",
			"Create a sample with an attributes",
			CONTEXT_ID,
			"<!-- BEGIN_INCLUDE(${prefix}:${operation}) -->\n"
					+ "\t<${prefix}:${operation} ${attributeName}=\"${attributeValue}\" />\n"
					+ "<!-- END_INCLUDE(${prefix}:${operation}) -->\n", true);

	Template templateWithChild = new Template(
			"Sample",
			"Create a sample with a childs that takes its value from Payload",
			CONTEXT_ID,
			"<!-- BEGIN_INCLUDE(${prefix}:${operation}) -->\n"
					+ "\t<${prefix}:${operation}>\n"
					+ "\t\t<${prefix}:${childName} value-ref=\"#[payload]\"/>\n"
					+ "\t</${prefix}:${operation}>\n"
					+ "<!-- END_INCLUDE(${prefix}:${operation}) -->\n", true);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#
	 * computeCompletionProposals(org.eclipse.jface.text.ITextViewer, int)
	 */
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {
		Image templateImage = DevkitImages.getManagedImage("","template.gif");
		ICompletionProposal[] completionProposals = new ICompletionProposal[3];
		Region region = new Region(offset - 1, 1);
		TemplateContext context = new DocumentTemplateContext(templateContext,
				viewer.getDocument(), offset - 1, 1);
		completionProposals[0] = new TemplateProposal(template, context,
				region, templateImage);
		completionProposals[1] = new TemplateProposal(templateWithAttribute,
				context, region, templateImage);
		completionProposals[2] = new TemplateProposal(templateWithChild,
				context, region, templateImage);

		return completionProposals;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#
	 * computeContextInformation(org.eclipse.jface.text.ITextViewer, int)
	 */
	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int offset) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#
	 * getCompletionProposalAutoActivationCharacters()
	 */
	public char[] getCompletionProposalAutoActivationCharacters() {
		return new char[] { 's', '.', '@' };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#
	 * getContextInformationAutoActivationCharacters()
	 */
	public char[] getContextInformationAutoActivationCharacters() {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.text.contentassist.IContentAssistProcessor#getErrorMessage
	 * ()
	 */
	public String getErrorMessage() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#
	 * getContextInformationValidator()
	 */
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

}
