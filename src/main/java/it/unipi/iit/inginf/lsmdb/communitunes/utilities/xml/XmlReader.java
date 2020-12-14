package it.unipi.iit.inginf.lsmdb.communitunes.utilities.xml;

import org.w3c.dom.Node;

import java.io.Closeable;

public interface XmlReader extends AutoCloseable, Closeable {

    Node getXmlNode(String name);

    String getXmlAttribute(Node node, String attributeName);

}
