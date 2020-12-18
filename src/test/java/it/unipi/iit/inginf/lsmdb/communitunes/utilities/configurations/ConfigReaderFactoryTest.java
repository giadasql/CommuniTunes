package it.unipi.iit.inginf.lsmdb.communitunes.utilities.configurations;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigReaderFactoryTest {

    String testXmlString = "<?xml version=\"1.0\"?>\n" +
            "<Test>\n" +
            "    <MyElement attribute1=\"value1\"/>\n" +
            "    <MyOtherElement attribute2=\"value2\" attribute3=\"value3\" attribute4=\"value4\"/>\n" +
            "</Test>";

    @Test
    public void WHEN_CreateConfigReader_invokedWithNull_THEN_returnNull() throws IOException, SAXException, ParserConfigurationException {
        assertNull(ConfigReaderFactory.CreateConfigReader(null, null));
    }

    @Test
    public void WHEN_CreateConfigReaderFromString_invokedWithNullType_THEN_returnNull() throws ParserConfigurationException, SAXException, IOException {
        assertNull(ConfigReaderFactory.createConfigReaderFromString(null, testXmlString));
    }

    @Test
    public void WHEN_CreateConfigReader_invokedWithNullSource_THEN_returnNull() throws IOException, SAXException, ParserConfigurationException {
        assertNull(ConfigReaderFactory.CreateConfigReader(ConfigReaderType.Xml, null));
    }

    @Test
    public void WHEN_CreateConfigReaderFromString_invokedWithXMLAndValidSource_THEN_returnXmlConfigReader() throws ParserConfigurationException, SAXException, IOException {
        ConfigReader reader = ConfigReaderFactory.createConfigReaderFromString(ConfigReaderType.Xml, testXmlString);
        Assertions.assertTrue(reader instanceof XmlConfigReader);
    }


}
