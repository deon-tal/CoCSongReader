/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.ashleagardens.coc.util;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.configuration.ConfigurationException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author Deon
 */
public class PropertyUtilTest {

    private static final PropertyUtil PROPERTY_UTIL = PropertyUtil.INSTANCE;
    private static final String PROPERTY_FILE_NAME = "application.properties";

    @Test
    public void shouldGetPropertiesForFile() throws ConfigurationException {
        //Act
        Map<String, String> propertyMap = PROPERTY_UTIL.getApplicationProperties();

        //Assert
        assertNotNull("propertyMap is null", propertyMap);
        assertEquals("prpoertyMap size not equal to 2", 2, propertyMap.size());
    }

    @Test
    public void shouldUpdatePropertiesFile() throws ConfigurationException {
        //Arrange
        final String locationKey = "presentations.location";
        final String newLocation = "c:\\test";
        Map<String, String> updatedPropertyMap = new HashMap<>();
        updatedPropertyMap.put(locationKey, newLocation);

        //Act
        PROPERTY_UTIL.updatePropertyForApplication(updatedPropertyMap);
        String locationKeyValue = PROPERTY_UTIL.getApplicationProperties().get(locationKey);

        //Assert
        assertEquals("Location key was not updated", newLocation, locationKeyValue);
    }

    @Test
    public void shouldGetPptFolderLocation() {
        //Act
        String folderLocation = PROPERTY_UTIL.getPptFolderLocation();

        //Assert
        assertNotNull("Folder location is null", folderLocation);
        assertFalse("Folder location is empty", folderLocation.isEmpty());
    }
}
