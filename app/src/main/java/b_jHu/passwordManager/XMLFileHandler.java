package b_jHu.passwordManager;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class XMLFileHandler {

	public XMLFileHandler() {

	}

	public void createXMLFile(String fileDir, String rootElementName) {
		try {
			Document doc = DocumentBuilderFactory.newInstance()
								.newDocumentBuilder().newDocument();
			
			Element rootElement = doc.createElement(rootElementName);
			doc.appendChild(rootElement);

			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(fileDir));
			
			transformer.transform(source, result);
		} catch (Exception e) {
			new ErrorMessage("Error whilst creating the config file", e);
		}

	}

	public Document openXMLFile(String fileDir) {
		try {
			File xmlFile = new File(fileDir);
			Document doc = DocumentBuilderFactory.newInstance()
								.newDocumentBuilder().parse(xmlFile);

			doc.getDocumentElement().normalize();

			return doc;
		} catch (ParserConfigurationException | SAXException | IOException e) {
			new ErrorMessage("Error opening the config file", e);
			return null;
		}


	}


	public String getValue(String fileDir, String key) {
		Document doc = openXMLFile(fileDir);
		String val = doc.getElementsByTagName(key).item(0).getTextContent();
		return val;
	}

	public void appendNode(String fileDir, String parentNodeName, String keyName, String value) {

		try {
			Document doc = openXMLFile(fileDir);

			Element newElem = doc.createElement(keyName);
			newElem.appendChild(doc.createTextNode(value));
			Node parentNode = doc.getElementsByTagName(parentNodeName).item(0);
			parentNode.appendChild(newElem);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(fileDir));
			transformer.transform(source, result);
		} catch (Exception e) {
			new ErrorMessage("Error modifying the config file", e);
		}


	}
}
