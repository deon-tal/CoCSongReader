/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.ashleagardens.coc.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.ConfigurationException;

/**
 *
 * @author Deon
 */
public class Main {

    private static final PropertyUtil propertyUtil = PropertyUtil.INSTANCE;

    public static void main(String... args) {
//        try {
//            Map<String, String> propertyMap = new HashMap<>();
//            propertyMap.put("presentations.home.location", "testy test");
//            
//            propertyUtil.updatePropertyForApplication(propertyMap);
//        }  catch (ConfigurationException ex) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        try {
            Map<String, String> propertyMap = propertyUtil.getApplicationProperties();
            
            System.out.println(propertyMap);
        }  catch (ConfigurationException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
