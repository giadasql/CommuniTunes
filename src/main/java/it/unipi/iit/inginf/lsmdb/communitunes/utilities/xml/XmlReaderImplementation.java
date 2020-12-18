package it.unipi.iit.inginf.lsmdb.communitunes.utilities.xml;

import org.w3c.dom.*;
import com.google.common.annotations.VisibleForTesting;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
    public Node getXmlChild(Node node, String childName) {
        if(node == null || childName == null){
            return null;
        }
        NodeList nodes = node.getChildNodes();
        for(int i = 0; i < nodes.getLength(); i++){
            Node current = nodes.item(i);
            if(current instanceof Element){
                if(childName.equals(current.getNodeName())){
                    return current;
                }
            }
        }
        return null;
    }

    @Override
    public List<Node> getXmlChildren(Node node) {
        if(node == null){
            return null;
        }
        NodeList nodes = node.getChildNodes();
        List<Node> elements = new ArrayList<>();
        for(int i = 0; i < nodes.getLength(); i++){
            Node current = nodes.item(i);
            if(current instanceof Element){
                elements.add(current);
            }
        }
        return elements;
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

    public Node getLastNodeOfPath(List<String> path) {
        Node lastNode = null;
        if(path == null){
            return null;
        }
        for (String nodeName:
                path) {
            if(lastNode == null){
                lastNode = getXmlNode(nodeName);
            }
            else{
                lastNode = getXmlChild(lastNode, nodeName);
            }
            if(lastNode == null){
                return null;
            }
        }
        return lastNode;
    }
}
