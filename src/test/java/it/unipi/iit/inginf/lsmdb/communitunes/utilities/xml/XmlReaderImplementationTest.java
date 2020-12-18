package it.unipi.iit.inginf.lsmdb.communitunes.utilities.xml;

import org.junit.jupiter.api.*;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

public class XmlReaderImplementationTest {

    private XmlReaderImplementation reader;

    @Test
    public void WHEN_constructor_invokedWithNull_THEN_throw_NullPointerException(){
        assertThrows(NullPointerException.class, () -> new XmlReaderImplementation(null));
    }

    @BeforeEach
    public void init() throws IOException, ParserConfigurationException, SAXException {
        String testXmlString = "<?xml version=\"1.0\"?>\n" +
                                "<Test>\n" +
                                "    <MyElement attribute1=\"value1\"/>\n" +
                                "    <MyOtherElement attribute2=\"value2\" attribute3=\"value3\" attribute4=\"value4\"/>\n" +
                                "</Test>";
        reader = new XmlReaderImplementation(new ByteArrayInputStream(testXmlString.getBytes()));
    }

    @Test
    public void WHEN_getXmlNode_invokedWithNull_THEN_return_null(){
        assertNull(reader.getXmlNode(null));
    }

    @Test
    public void WHEN_getXmlNode_invokedWithTestDocAndNonExistingNode_THEN_return_null(){
        Node returnedNode = reader.getXmlNode("NonExistingElement");
        assertNull(returnedNode);
    }

    @Test
    public void WHEN_getXmlNode_invokedWithTestDocAndExistingNode_THEN_return_expectedNode(){
        String expectedNodeName = "MyElement";
        Node returnedNode = reader.getXmlNode("MyElement");
        assertEquals(expectedNodeName, returnedNode.getNodeName());
    }

    @Test
    public void WHEN_getXmlAttributeValue_invokedWithNullNode_THEN_return_null(){
        assertNull(reader.getXmlAttributeValue(null, "attribute1"));
    }

    @Test
    public void WHEN_getXmlAttributeValue_invokedWithNullAttribute_THEN_return_null(){
        assertNull(reader.getXmlAttributeValue("MyElement", null));
    }

    @Test
    public void WHEN_getXmlAttributeValue_invokedWithTestDocAndExistingNodeAndNonExistingAttribute_THEN_return_null(){
        assertNull(reader.getXmlAttributeValue("MyOtherElement", "NonExistingAttribute"));
    }

    @Test
    public void WHEN_getXmlAttributeValue_invokedWithTestDocAndNonExistingNodeAndNonExistingAttribute_THEN_return_null(){
        assertNull(reader.getXmlAttributeValue("NonExistingNode", "attribute4"));
    }

    @Test
    public void WHEN_getXmlAttributeValue_invokedWithTestDocAndExistingNodeAndExistingAttribute_THEN_return_expectedValue(){
        String expectedValue = "value4";
        assertEquals(reader.getXmlAttributeValue("MyOtherElement", "attribute4"), expectedValue);
    }

    @Test
    public void WHEN_getXmlAttribute_invokedWithNullNode_THEN_return_null(){
        assertNull(reader.getXmlAttribute(null, "attribute1"));
    }

    @Test
    public void WHEN_getXmlAttribute_invokedWithNullAttribute_THEN_return_null(){
        Node returnedNode = reader.getXmlNode("MyElement");
        assertNull(reader.getXmlAttribute(returnedNode, null));
    }

    @Test
    public void WHEN_getXmlAttribute_invokedWithTestDocAndExistingNodeAndNonExistingAttribute_THEN_return_null(){
        Node returnedNode = reader.getXmlNode("MyOtherElement");
        assertNull(reader.getXmlAttribute(returnedNode, "NonExistingAttribute"));
    }

    @Test
    public void WHEN_getXmlAttribute_invokedWithTestDocAndExistingNodeAndExistingAttribute_THEN_return_expectedValue(){
        String expectedValue = "value4";
        Node returnedNode = reader.getXmlNode("MyOtherElement");
        assertEquals(reader.getXmlAttribute(returnedNode, "attribute4").getNodeValue(), expectedValue);
    }
}
