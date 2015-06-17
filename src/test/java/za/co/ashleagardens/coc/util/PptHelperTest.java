package za.co.ashleagardens.coc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.junit.Ignore;

/**
 *
 * @author Deon
 */
public final class PptHelperTest {

    private File pptFile;
    private final String fileName = "Here I Am To Worship-eSFP.pptx";

    @Before
    public void setup() throws URISyntaxException {
        pptFile = new File(PptHelperTest.class.getClassLoader().getResource(fileName).toURI());
    }

    @Test
    public void shouldDetermineNumVersesInSong() throws Exception {
        //Act
        int numVerses = PptHelper.determineNumVerses(pptFile);

        //Assert
        assertEquals("The number of verses in song is not equal to 2", 2, numVerses);
    }

    @Ignore("To be completed")
    @Test
    public void shouldGetPresentationWithSelectedVerses() throws Exception {
        //Arrange
        Map<Integer, Boolean> verseSelectionMap = new HashMap<>();
        verseSelectionMap.put(1, Boolean.TRUE);
        verseSelectionMap.put(2, Boolean.FALSE);
        
        //Act
        PptHelper.getPresentationWithSelectedVerses(pptFile, verseSelectionMap);
        
        //Assert
    }

    /**
     * Auxiliary method tests
     */
    @Test
    public void shouldGetSlideTag() throws IOException {
        //Arrange
        XMLSlideShow slideShow = getXmlSlideShowFromFile(pptFile);

        //Act
        String slideTag = PptHelper.getSlideTag(slideShow.getSlides()[0]);

        //Assert
        assertEquals("The slide tag does not match 1-1 Here I Am To Worship", "1-1 Here I Am To Worship", slideTag);
    }

    private XMLSlideShow getXmlSlideShowFromFile(File pptFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(pptFile)) {
            return new XMLSlideShow(fis);
        }
    }
}
