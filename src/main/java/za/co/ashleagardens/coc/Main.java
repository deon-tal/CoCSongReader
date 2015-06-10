/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.ashleagardens.coc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.xslf.usermodel.XMLSlideShow;

/**
 *
 * @author Deon
 */
public class Main {

    private static final String[] pptPresentations = new String[]{"Blessed Be Your Name-eSFP.pptx", "He is Exalted-eSFP.pptx", "Here I Am To Worship-eSFP.pptx"};

    public static void main(String... args) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(pptPresentations[2]);
            XMLSlideShow ppt = new XMLSlideShow(fis);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
