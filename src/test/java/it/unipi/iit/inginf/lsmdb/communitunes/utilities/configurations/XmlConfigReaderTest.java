package it.unipi.iit.inginf.lsmdb.communitunes.utilities.configurations;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class XmlConfigReaderTest {

    XmlConfigReader xmlReader;

    @BeforeEach
    public void init() throws ParserConfigurationException, SAXException, IOException {
        String textXML =    "<?xml version=\"1.0\"?>\n" +
                            "<Test>\n" +
                            "    <MyElement attribute1=\"value1\"/>\n" +
                            "    <MyOtherElement attribute2=\"value2\" attribute3=\"value3\" attribute4=\"value4\"/>\n" +
                            "    <Parent>\n" +
                            "       <Child childAttribute=\"myVal\"/>\n" +
                            "    </Parent>\n" +
                            "</Test>";
        xmlReader = new XmlConfigReader(new ByteArrayInputStream(textXML.getBytes()));
    }

    @Test
    public void WHEN_getStringConfigValue_invokedWithNullPath_THEN_returnNull(){
        assertNull(xmlReader.getStringConfigValue((List<String>) null, "attribute1"));
    }

    @Test
    public void WHEN_getStringConfigValue_invokedWithNullAttribute_THEN_returnNull(){
        List<String> path = new ArrayList<>(Arrays.asList("Parent", "Child"));
        assertNull(xmlReader.getStringConfigValue(path, null));
    }

    @Test
    public void WHEN_getStringConfigValue_invokedWithNonExistingPath_THEN_returnNull(){
        List<String> path = new ArrayList<>(Arrays.asList("Parent", "GrandChild"));
        assertNull(xmlReader.getStringConfigValue(path, "childAttribute"));
    }

    @Test
    public void WHEN_getStringConfigValue_invokedWithNonExistingAttribute_THEN_returnNull(){
        List<String> path = new ArrayList<>(Arrays.asList("Parent", "Child"));
        assertNull(xmlReader.getStringConfigValue(path, "NonAttribute"));
    }

    @Test
    public void WHEN_getStringConfigValue_invokedWithExistingAttributeAndExistingPath_THEN_returnExpectedValue(){
        List<String> path = new ArrayList<>(Arrays.asList("Parent", "Child"));
        assertEquals("myVal", xmlReader.getStringConfigValue(path, "childAttribute"));
    }
}
