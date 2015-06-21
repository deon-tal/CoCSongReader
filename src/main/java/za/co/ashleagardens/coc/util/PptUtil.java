package za.co.ashleagardens.coc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.xslf.usermodel.DrawingParagraph;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFCommonSlideData;
import org.apache.poi.xslf.usermodel.XSLFSlide;

/**
 *
 * @author Deon
 */
public enum PptUtil {

    INSTANCE;

    private static final String VERSE_REGEX = "\\d-\\d";
    //TODO: add logging
//    private static final Logger LOGGER;

    private PptUtil() {
    }

    public int determineNumVerses(File pptFile) throws Exception {
        try (FileInputStream fis = new FileInputStream(pptFile)) {
            XMLSlideShow ppt = new XMLSlideShow(fis);
            XSLFSlide[] slides = ppt.getSlides();
            Pattern pattern = Pattern.compile(VERSE_REGEX);
            int currentVerseIndex = 0;

            for (XSLFSlide slide : slides) {
                String slideTag = getSlideTag(slide);
                int verseIndex = getVerseIndexFromSlide(slide);

                //It should only reach this far when it is a verse with pattern \\d-\\d
                if (slideTag != null && verseIndex > 0) {
                    Matcher match = pattern.matcher(slideTag.subSequence(0, 3));
                    if (match.matches() && verseIndex > currentVerseIndex) {
                        currentVerseIndex++;
                    }
                }
            }

            return currentVerseIndex;
        }
    }

    public void getPresentationWithSelectedVerses(File pptFile, Map<Integer, Boolean> verseSelectionMap) throws Exception {
        try (FileInputStream fis = new FileInputStream(pptFile)) {
            XMLSlideShow ppt = new XMLSlideShow(fis);
            XMLSlideShow newPpt = new XMLSlideShow();

            Map<Integer, List<XSLFSlide>> verseSectionMap = breakUpSong(ppt);

            for (Map.Entry<Integer, List<XSLFSlide>> verseSectionEntry : verseSectionMap.entrySet()) {
                int verse = verseSectionEntry.getKey();
                if (verseSelectionMap.containsKey(verse) && verseSelectionMap.get(verse)) {
                    for (XSLFSlide slide : verseSectionEntry.getValue()) {
                        newPpt.createSlide().importContent(slide);
                    }
                }
            }

            final String newPptFileName = "Temp " + pptFile.getName();

            try (FileOutputStream output = new FileOutputStream(new File(newPptFileName))) {
                newPpt.write(output);
            }
        }
    }

    private Map<Integer, List<XSLFSlide>> breakUpSong(XMLSlideShow ppt) {
        Map<Integer, List<XSLFSlide>> verseSectionMap = new HashMap<>();
        XSLFSlide[] slides = ppt.getSlides();
        Pattern pattern = Pattern.compile(VERSE_REGEX);
        int currentVerseIndex = 0;

        for (XSLFSlide slide : slides) {
            String slideTag = getSlideTag(slide);
            int verseIndex = getVerseIndexFromSlide(slide);

            //It should only reach this far when it is a verse with pattern \\d-\\d
            if (slideTag != null) {
                Matcher match = pattern.matcher(slideTag.subSequence(0, 3));
                if (match.matches() && verseIndex > currentVerseIndex) {
                    List<XSLFSlide> verseSectionSlides = new ArrayList<>();
                    verseSectionSlides.add(slide);
                    verseSectionMap.put(++currentVerseIndex, verseSectionSlides);
                } else {
                    verseSectionMap.get(currentVerseIndex).add(slide);
                }
            }
        }

        return verseSectionMap;
    }

    /**
     * A helper method that return the tag of the slide passed in as parameter.
     *
     * @param slide the slide from which to determine the verse index.
     * @return a non-null string if there exists a slide tag, otherwise null.
     */
    protected String getSlideTag(XSLFSlide slide) {
        XSLFCommonSlideData data = slide.getCommonSlideData();
        List<DrawingParagraph> drawingParagraphs = data.getText();

        if (drawingParagraphs.size() > 0) {
            try {
                return drawingParagraphs.get(0).getText().toString();
            } catch (IndexOutOfBoundsException ex) {
                //TODO: add logging
                return null;
            }
        }

        return null;
    }

    /**
     * A helper method that returns the verse index of the slide passed in as
     * parameter.
     *
     * @param slide the slide from which to determine the verse index.
     * @return a positive integer if the slide represents a verse, otherwise -1.
     */
    protected int getVerseIndexFromSlide(XSLFSlide slide) {
        XSLFCommonSlideData data = slide.getCommonSlideData();
        List<DrawingParagraph> drawingParagraphs = data.getText();

        if (drawingParagraphs.size() > 0) {
            String slideTag = getSlideTag(slide);

            try {
                if (slideTag != null) {
                    return Integer.parseInt(String.valueOf(slideTag.charAt(0)));
                }
            } catch (NumberFormatException ex) {
                //TODO: add logging
                return -1;
            }
        }

        return -1;
    }
}
