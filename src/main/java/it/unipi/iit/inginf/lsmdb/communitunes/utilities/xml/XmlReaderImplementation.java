package it.unipi.iit.inginf.lsmdb.communitunes.utilities.xml;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import com.google.common.annotations.VisibleForTesting;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

class XmlReaderImplementation implements XmlReader {

    @VisibleForTesting
    private final Document parsedDocument;

    @VisibleForTesting
    XmlReaderImplementation(InputStream xmlSource) throws NullPointerException, IOException, ParserConfigurationException, SAXException {
        if(xmlSource == null){
            throw new NullPointerException("Provided stream is null.");
        }
        else {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            builder = domFactory.newDocumentBuilder();
            parsedDocument = builder.parse(xmlSource);
        }
        xmlSource.close();
    }

    @Override
    public Node getXmlNode(String name) {
        if(name == null){
            return null;
        }
        if(parsedDocument != null){
            NodeList elements = parsedDocument.getElementsByTagName(name);
            if(elements.getLength() > 0){
                return elements.item(0);
            }
            else{
                return null;
            }
        }
        else{
            return null;
        }
    }

    @Override
    public Node getXmlAttribute(Node node, String attributeName) {
        if(node == null || attributeName == null){
            return null;
        }
        NamedNodeMap attributes = node.getAttributes();
        if(attributes.getLength() > 0){
            return attributes.getNamedItem(attributeName);
        }
        else{
            return null;
        }
    }

    @Override
    public String getXmlAttributeValue(String nodeName, String attributeName) {
        Node node = getXmlNode(nodeName);
        if(node != null){
            Node attribute = getXmlAttribute(node, attributeName);
            if(attribute != null){
                return attribute.getNodeValue();
            }
        }
        return null;
    }
}
