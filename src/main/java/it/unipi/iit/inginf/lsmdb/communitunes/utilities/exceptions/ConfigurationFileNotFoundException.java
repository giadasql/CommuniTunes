package it.unipi.iit.inginf.lsmdb.communitunes.utilities.exceptions;

public class ConfigurationFileNotFoundException extends Throwable {

    public final String settingsFileName;

    public ConfigurationFileNotFoundException(String settingsFileName) {
        this.settingsFileName = settingsFileName;
    }
}
