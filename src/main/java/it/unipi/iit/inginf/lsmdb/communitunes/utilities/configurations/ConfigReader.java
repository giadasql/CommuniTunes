package it.unipi.iit.inginf.lsmdb.communitunes.utilities.configurations;

import java.util.List;

/**
 *
 */
public interface ConfigReader {

    /**
     * Searches the configuration source following the provided path and looking for an attribute with the provided name, returning its value.
     *
     * @param   pathToSection    List of strings representing subsequent steps of a path. The path can be composed by names
     *                           of XML nested nodes, json nested documents, or other objects depending on the configuration format
     *                           requested when creating the instance of reader.
     * @param attribute String representing the name of the attribute, located in the last element of the path, of which the value has to be read.
     * @return The value of the configuration attribute when found, Null otherwise.
     */
    String getStringConfigValue(List<String> pathToSection, String attribute);

    /**
     * Searches the configuration source following the provided path and looking for an attribute with the provided name, returning its value.
     *
     * @param   element String containing the name of the element (eg. XML node).
     * @param attribute String representing the name of an attribute of which the value has to be read.
     * @return The value of the configuration attribute when found, Null otherwise.
     */
    String getStringConfigValue(String element, String attribute);
}
