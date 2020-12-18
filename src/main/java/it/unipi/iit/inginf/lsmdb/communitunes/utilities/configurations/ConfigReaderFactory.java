package it.unipi.iit.inginf.lsmdb.communitunes.utilities.configurations;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

public class ConfigReaderFactory {

    private ConfigReaderFactory(){ }

    public static ConfigReader CreateConfigReader(ConfigReaderType type, InputStream source) throws ParserConfigurationException, SAXException, IOException {
        if(type == ConfigReaderType.Xml && source != null){
            return new XmlConfigReader(source);
        }
        else{
            return null;
        }
    }

    public static ConfigReader createConfigReaderFromFile(ConfigReaderType type, File file) throws IOException, ParserConfigurationException, SAXException {
        if(file == null){
            return null;
        }
        return CreateConfigReader(type, new FileInputStream(file));
    }

    public static ConfigReader createConfigReaderFromFile(ConfigReaderType type, String fileName) throws IOException, ParserConfigurationException, SAXException {
        if(fileName == null){
            return null;
        }
        return CreateConfigReader(type, new FileInputStream(fileName));
    }

    public static ConfigReader createConfigReaderFromString(ConfigReaderType type, String text) throws IOException, SAXException, ParserConfigurationException {
        if(text == null){
            return null;
        }
        return CreateConfigReader(type, new ByteArrayInputStream(text.getBytes()));
    }
}
