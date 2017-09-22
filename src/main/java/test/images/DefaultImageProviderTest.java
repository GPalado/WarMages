package test.images;

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import main.images.DefaultImageProvider;
import main.images.GameImage;
import org.junit.Test;

/**
 * Test names here follow the test naming convention:
 * unitOfWorkUnderTest_typeOfInput_expectedResult.
 */
public class DefaultImageProviderTest {

  public static final String IMAGE_PROVIDER_DIRECTORY = "src/main/resources/";

  @Test
  public void load_usingTestImage_resultShouldLookCorrect() throws IOException {
    DefaultImageProvider imageProvider = new DefaultImageProvider(IMAGE_PROVIDER_DIRECTORY);
    BufferedImage image = GameImage._TEST_FULL_SIZE.load(imageProvider);

    assertEquals(20, image.getWidth());
    assertEquals(15, image.getHeight());

    assertEquals(Color.WHITE.getRGB(), image.getRGB(0, 0));
    assertEquals(Color.BLACK.getRGB(), image.getRGB(1, 1));
  }

  @Test(expected = IOException.class)
  public void load_nonExistentImage_throwsException() throws IOException {
    new DefaultImageProvider(IMAGE_PROVIDER_DIRECTORY).load("some_file_that_doesnt_exist.png");
  }

}