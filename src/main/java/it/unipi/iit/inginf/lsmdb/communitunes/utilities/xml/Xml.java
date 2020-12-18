package it.unipi.iit.inginf.lsmdb.communitunes.utilities.xml;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

public class Xml {

    private Xml(){ }

    public static XmlReader createReader(InputStream xmlSource) throws ParserConfigurationException, SAXException, IOException {
        return new XmlReaderImplementation(xmlSource);
    }

    public static XmlReader createReaderFromFile(File file) throws IOException, ParserConfigurationException, SAXException {
        return new XmlReaderImplementation(new FileInputStream(file));
    }

    public static XmlReader createReaderFromFile(String fileName) throws IOException, ParserConfigurationException, SAXException {
        return new XmlReaderImplementation(new FileInputStream(fileName));
    }

    public static XmlReader createReaderFromString(String xmlString) throws ParserConfigurationException, SAXException, IOException {
        return new XmlReaderImplementation(new ByteArrayInputStream(xmlString.getBytes()));
    }
}
