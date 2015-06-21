package za.co.ashleagardens.coc.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 *
 * @author Deon
 */
public enum PropertyUtil {

    INSTANCE;

    private static final String APP_PROPERTY_FILE_NAME = "application.properties";
    private static final String LOG_PROPERTY_FILE_NAME = "log4j.properties";

    private PropertyUtil() {
    }

    public Map<String, String> getApplicationProperties() throws ConfigurationException {
        return getPropertiesForFile(APP_PROPERTY_FILE_NAME);
    }

    public Map<String, String> getLoggerProperties() throws ConfigurationException {
        return getPropertiesForFile(LOG_PROPERTY_FILE_NAME);
    }

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

    public void updatePropertyForApplication(Map<String, String> propertyMap) throws IOException, ConfigurationException {
        updatePropertyForFile(APP_PROPERTY_FILE_NAME, propertyMap);
    }

    public void updatePropertyForLogger(Map<String, String> propertyMap) throws IOException, ConfigurationException {
        updatePropertyForFile(LOG_PROPERTY_FILE_NAME, propertyMap);
    }

    private void updatePropertyForFile(String fileName, Map<String, String> propertyMap) throws IOException, ConfigurationException {
        PropertiesConfiguration config = new PropertiesConfiguration(PropertyUtil.class.getClassLoader().getResource(fileName));

        for (Map.Entry<String, String> entry : propertyMap.entrySet()) {
            config.setProperty(entry.getKey(), entry.getValue());
        }

        config.save();
    }
}
