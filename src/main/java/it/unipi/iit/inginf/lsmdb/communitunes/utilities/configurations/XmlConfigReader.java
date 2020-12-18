package it.unipi.iit.inginf.lsmdb.communitunes.utilities.configurations;

import com.google.common.annotations.VisibleForTesting;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.xml.Xml;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.xml.XmlReader;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

class XmlConfigReader implements  ConfigReader{

    private XmlReader xmlReader;

    @VisibleForTesting
    XmlConfigReader(InputStream source) throws IOException, SAXException, ParserConfigurationException {
        if(source != null){
            xmlReader = Xml.createReader(source);
        }
    }

    @Override
    public String getStringConfigValue(List<String> pathToSection, String attribute) {
        if(attribute == null){
            return null;
        }
        Node lastNode = xmlReader.getLastNodeOfPath(pathToSection);
        if(lastNode != null){
            Node attributeNode = xmlReader.getXmlAttribute(lastNode, attribute);
            if(attributeNode != null){
                return  attributeNode.getNodeValue();
            }
        }
        return null;
    }

}
