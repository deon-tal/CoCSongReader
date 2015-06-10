/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.ashleagardens.coc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.xslf.usermodel.DrawingParagraph;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFCommonSlideData;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import za.co.ashleagardens.coc.Main;

/**
 *
 * @author Deon
 */
public enum PptHelper {

    INSTANCE;

    private static final String VERSE_REGEX = "\\d-\\d";

    private PptHelper() {
    }

    public static int determineNumVerses(File file) throws Exception {
        try (FileInputStream fis = new FileInputStream(file);) {
            XMLSlideShow ppt = new XMLSlideShow(fis);

            XSLFSlide[] slides = ppt.getSlides();
            Pattern pattern = Pattern.compile(VERSE_REGEX);
            int currentVerseIndex = 0;

            for (XSLFSlide slide : slides) {
                XSLFCommonSlideData data = slide.getCommonSlideData();
                List<DrawingParagraph> drawingParagraphs = data.getText();

                if (drawingParagraphs.size() > 0) {
                    String slideTag;
                    try {
                        slideTag = drawingParagraphs.get(0).getText().toString();
                    } catch (IndexOutOfBoundsException ex) {
                        //TODO: try next index?
                        continue;
                    }

                    int verseIndex;
                    try {
                        if (slideTag != null) {
                            verseIndex = Integer.parseInt(String.valueOf(slideTag.charAt(0)));
                        } else {
                            continue;
                        }
                    } catch (NumberFormatException ex) {
                        //TODO: handle this appropriately?
                        continue;
                    }

                    //It should only reach this far when it is a verse with pattern \\d-\\d
                    Matcher match = pattern.matcher(slideTag.subSequence(0, 3));
                    if (match.matches() && verseIndex > currentVerseIndex) {
                        currentVerseIndex++;
                    }
                }
            }

            return currentVerseIndex;
        } catch (IOException ex) {
            throw ex;
        } catch (Exception ex) {
            //Safety net for any exception filtering through
            throw ex;
        }
    }

    public static XMLSlideShow getPresentationWithSelectedVerses(int[] verses) {
        return null;
    }
}
