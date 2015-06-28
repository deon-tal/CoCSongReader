package za.co.ashleagardens.coc.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Deon
 */
public enum PropertyUtil {

    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyUtil.class);
    private static final String CLASS_NAME = PropertyUtil.class.getName();
    private static final String APP_PROPERTIES_FILE_NAME = "application.properties";
    private static final String LOG_PROPERTIES_FILE_NAME = "log4j.properties";
    private static final String APP_BUILD_PROPERTIES_FILE_NAME = "build.properties";
    private static final String PPT_FOLDER_LOCATION_KEY = "presentations.location";
    private static final String NEW_PPT_FOLDER_LOCATION_KEY = "new.presentations.location";

    private PropertyUtil() {
    }

    public Map<String, String> getApplicationProperties() throws ConfigurationException {
        return getPropertiesForFile(APP_PROPERTIES_FILE_NAME);
    }

    public Map<String, String> getLoggerProperties() throws ConfigurationException {
        return getPropertiesForFile(LOG_PROPERTIES_FILE_NAME);
    }

    public Map<String, String> getApplicationBuildProperties() throws ConfigurationException {
        return getPropertiesForFile(APP_BUILD_PROPERTIES_FILE_NAME);
    }

    /**
     * Helper method that retrieves the key value pairs for the file passed as a
     * parameter.
     *
     * @param fileName the file from which to retrieve the properties.
     * @return a map of key value pairs.
     * @throws ConfigurationException an exception might be thrown.
     */
    private Map<String, String> getPropertiesForFile(String fileName) throws ConfigurationException {
        Map<String, String> propertyMap = new HashMap<>();

        Configuration config = new PropertiesConfiguration(PropertyUtil.class.getClassLoader().getResource(fileName));
        Iterator<String> keys = config.getKeys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            propertyMap.put(key, config.getString(key));
        }

        return propertyMap;
    }

    public void updatePropertyForApplication(Map<String, String> propertyMap) throws ConfigurationException {
        updatePropertyForFile(APP_PROPERTIES_FILE_NAME, propertyMap);
    }

    public void updatePropertyForLogger(Map<String, String> propertyMap) throws ConfigurationException {
        updatePropertyForFile(LOG_PROPERTIES_FILE_NAME, propertyMap);
    }

    /**
     * A helper method that updates the property file passed as a parameter.
     *
     * @param fileName the file that needs to be updated.
     * @param propertyMap the update map of key value pairs.
     * @throws ConfigurationException an exception might be thrown.
     */
    private void updatePropertyForFile(String fileName, Map<String, String> propertyMap) throws ConfigurationException {
        PropertiesConfiguration config = new PropertiesConfiguration(PropertyUtil.class.getClassLoader().getResource(fileName));

        for (Map.Entry<String, String> entry : propertyMap.entrySet()) {
            config.setProperty(entry.getKey(), entry.getValue());
        }

        config.save();
    }

    /**
     * Helper method to return the folder location for presentations.
     *
     * @return a string representing the path.
     */
    public String getPptFolderLocation() {
        return getFolderLocation(PPT_FOLDER_LOCATION_KEY);
    }

    /**
     * Helper method to return the folder location for new presentations.
     *
     * @return a string representing the path.
     */
    public String getNewPptFolderLocation() {
        return getFolderLocation(NEW_PPT_FOLDER_LOCATION_KEY);
    }

    private String getFolderLocation(final String key) {
        String folderLocation = ".";
        try {
            folderLocation = getApplicationProperties().get(key);
        } catch (ConfigurationException ex) {
            LOGGER.warn(CLASS_NAME + ":getFolderLocation: " + ex.getMessage());
        }
        return folderLocation;
    }
}
