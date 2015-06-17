package org.mule.tooling.incubator.utils.xml;

import java.io.IOException;
import java.io.Writer;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Class that simplifies a bit the creation and manipulation of XML documents.
 * @author juancavallotti
 *
 */
public final class XMLDomHelper {
	
	private final Document domDocument;
	
	public XMLDomHelper(Document domDocument) {
		this.domDocument = domDocument;
	}
	
	public Element createRootElement(String elementName) {
		return createElement(elementName, domDocument);
	}
	
	public Element createElement(String elementName, Node parent) {
		Element ret = domDocument.createElement(elementName);
		parent.appendChild(ret);
		return ret;
	}
	
	public void insertComment(String comment, Node to) {
		Node ret = domDocument.createComment(comment);
		to.appendChild(ret);
	}
	
	
	public void writeToWriter(Writer outWriter, int indentValue) throws IOException, TransformerException {
		TransformerFactory tf = TransformerFactory.newInstance();
		tf.setAttribute("indent-number", indentValue);
		Transformer t = tf.newTransformer();
		t.setOutputProperty(OutputKeys.INDENT, "yes");
		t.transform(new DOMSource(domDocument), new StreamResult(outWriter));
		outWriter.flush();
		outWriter.close();
	}
	
}
