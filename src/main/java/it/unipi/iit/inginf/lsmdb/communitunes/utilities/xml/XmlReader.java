package it.unipi.iit.inginf.lsmdb.communitunes.utilities.xml;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;

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
     * Searches the XML node for the first child with the specified name and returns the child node.
     *
     * @param   node    The node in which the attribute has to be found.
     * @param childName The name of the child to find.
     * @return The child when found, Null otherwise.
     */
    Node getXmlChild(Node node, String childName);

    /**
     * Returns all the children of the XML node.
     *
     * @param   node    The node in which the attribute has to be found.
     * @return The attribute when found, Null otherwise.
     */
    List<Node> getXmlChildren(Node node);

    /**
     * Searches the XML node for the first attribute with the specified name and returns the value.
     *
     * @param   nodeName    The name of the node in which the attribute has to be found.
     * @param attributeName The name of the attribute to find.
     * @return The attribute value when found, Null otherwise.
     */
    String getXmlAttributeValue(String nodeName, String attributeName);

    /**
     * Follows a path of nodes and returns the last node.
     *
     * @param   path A list containing the names of the nodes in the path.
     * @return The last node of the path when found, Null otherwise.
     */
    Node getLastNodeOfPath(List<String> path);
}
