package za.co.ashleagardens.coc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.xslf.usermodel.DrawingParagraph;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFCommonSlideData;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Deon
 */
public enum PptUtil {

    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(PptUtil.class);
    private static final String CLASS_NAME = PptUtil.class.getName();
    private static final PropertyUtil PROPERTY_UTIL = PropertyUtil.INSTANCE;
    private static final String VERSE_REGEX = "\\d-\\d";

    private PptUtil() {
    }

    /**
     * Determines the number of verses in the PowerPoint presentation.
     *
     * @param pptFile the file to read.
     * @return an integer indicating the number of verses.
     * @throws Exception may throw IO related exceptions.
     */
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

    /**
     * Writes the verse selection to a new PowerPoint presentation.
     *
     * @param pptFile the file to copy slides from.
     * @param verseSelectionMap the verse selection,
     * @throws Exception may throw IO related exceptions.
     */
    public void writePresentationWithSelectedVerses(File pptFile, Map<Integer, Boolean> verseSelectionMap, boolean addTimeStamp) throws Exception {
        try (FileInputStream fis = new FileInputStream(pptFile)) {
            XMLSlideShow ppt = new XMLSlideShow(fis);
            XMLSlideShow newPpt = new XMLSlideShow();

            Map<Integer, List<XSLFSlide>> verseSectionMap = breakUpSongByVerse(ppt);

            for (Map.Entry<Integer, List<XSLFSlide>> verseSectionEntry : verseSectionMap.entrySet()) {
                int verse = verseSectionEntry.getKey();
                if (verseSelectionMap.containsKey(verse) && verseSelectionMap.get(verse)) {
                    for (XSLFSlide slide : verseSectionEntry.getValue()) {
                        newPpt.createSlide().importContent(slide);
                    }
                }
            }

            String filePathString = PROPERTY_UTIL.getNewPptFolderLocation() + File.separator + (addTimeStamp ? new SimpleDateFormat("yyyyMMdd").format(new Date()) + " " : "") + pptFile.getName();
            try (FileOutputStream output = new FileOutputStream(new File(filePathString))) {
                newPpt.write(output);
            }
        }
    }

    /**
     * Helper method to break up song by verse. The method assumes that all the
     * slides following a verse up until the next verse belongs to the former
     * verse.
     *
     * @param ppt the PowerPoint slide show.
     * @return a map containing each verse in the song as key mapped to a list
     * of slides.
     */
    private Map<Integer, List<XSLFSlide>> breakUpSongByVerse(XMLSlideShow ppt) {
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
                LOGGER.info(CLASS_NAME + ":getSlideTag: " + ex.getMessage());
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
                LOGGER.info(CLASS_NAME + ":getVerseIndexFromSlide: " + ex.getMessage());
                return -1;
            }
        }

        return -1;
    }
}
