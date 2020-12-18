package it.unipi.iit.inginf.lsmdb.communitunes.utilities.xml;

import org.w3c.dom.Node;

/**
 * Interface for reading XML nodes and attributes from an XML source.
 */
public interface XmlReader {

    /**
     * Searches the XML source for the first node with the specified name and returns the node.
     *
     * @param   name    String representing the name of the node to find.
     * @return The node when found, Null otherwise.
     */
    Node getXmlNode(String name);

    /**
     * Searches the XML node for the first attribute with the specified name and returns the attribute node.
     *
     * @param   node    The node in which the attribute has to be found.
     * @param attributeName The name of the attribute to find.
     * @return The attribute when found, Null otherwise.
     */
    Node getXmlAttribute(Node node, String attributeName);

    /**
     * Searches the XML node for the first attribute with the specified name and returns the value.
     *
     * @param   nodeName    The name of the node in which the attribute has to be found.
     * @param attributeName The name of the attribute to find.
     * @return The attribute value when found, Null otherwise.
     */
    String getXmlAttributeValue(String nodeName, String attributeName);
}
