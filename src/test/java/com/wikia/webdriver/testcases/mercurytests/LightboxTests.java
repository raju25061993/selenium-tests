package com.wikia.webdriver.testcases.mercurytests;

import com.wikia.webdriver.common.contentpatterns.MercuryArticles;
import com.wikia.webdriver.common.core.Assertion;
import com.wikia.webdriver.common.core.imageutilities.ImageComparison;
import com.wikia.webdriver.common.core.imageutilities.Shooter;
import com.wikia.webdriver.common.driverprovider.NewDriverProvider;
import com.wikia.webdriver.common.logging.PageObjectLogging;
import com.wikia.webdriver.common.templates.NewTestTemplate;
import com.wikia.webdriver.pageobjectsfactory.componentobject.mercury.LightboxComponentObject;
import com.wikia.webdriver.pageobjectsfactory.pageobject.mercury.BasePageObject;
import com.wikia.webdriver.pageobjectsfactory.pageobject.mercury.PerformTouchAction;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.remote.DriverCommand;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @authors: Rodrigo Gomez, Łukasz Nowak, Tomasz Napieralski
 * @ownership: Content - Mercury mobile
 */
public class LightboxTests extends NewTestTemplate {

  @BeforeMethod(alwaysRun = true)
  public void prepareTest() {
    driver.manage().timeouts().pageLoadTimeout(100, TimeUnit.SECONDS);
  }

  private boolean failTest = false;

  private static final String DIRECTION_LEFT = "left";
  private static final String DIRECTION_RIGHT = "right";
  private static final String DIRECTION_UP = "up";
  private static final String DIRECTION_DOWN = "down";

  // MT01
  @Test(groups = {"MercuryLightboxTest_001", "MercuryLightboxTests", "Mercury"})
  public void MercuryLightboxTest_001_Open_Close() {
    BasePageObject base = new BasePageObject(driver);
    base.openMercuryArticleByName(wikiURL, MercuryArticles.MERCURY_GALLERY_TEST_TWO);
    LightboxComponentObject lightbox = new LightboxComponentObject(driver);
    lightbox.clickGalleryImage(0);
    Assertion.assertTrue(lightbox.isLightboxOpened(), "Lightbox is closed");
    PageObjectLogging.log("Lightbox", "is opened", true);
    PageObjectLogging.log("Current image", "is visible", "is not visible", lightbox.isCurrentImageVisible());
    if (! lightbox.isCurrentImageVisible()) failTest = true;
    lightbox.clickCloseButton();
    PageObjectLogging.log("Lightbox", "is closed", "is opened", ! lightbox.isLightboxOpened());
    if (lightbox.isLightboxOpened()) failTest = true;
    base.failTest(failTest);
  }

  // MT02
  @Test(groups = {"MercuryLightboxTest_002", "MercuryLightboxTests", "Mercury"})
  public void MercuryLightboxTest_002_TapOnEdgesChangeImages_SwipeChangeImages() {
    BasePageObject base = new BasePageObject(driver);
    base.openMercuryArticleByName(wikiURL, MercuryArticles.MERCURY_GALLERY_TEST_TWO);
    PerformTouchAction touchAction = new PerformTouchAction(driver);
    LightboxComponentObject lightbox = new LightboxComponentObject(driver);
    lightbox.clickGalleryImage(0);
    Assertion.assertTrue(lightbox.isCurrentImageVisible(), "Image is not visible");
    PageObjectLogging.log("Current image", "is visible", true);
    String currentImageSrc = lightbox.getCurrentImagePath();
    touchAction.tapOnPointXY(25, 50, 500, 5000);
    String nextImageSrc = lightbox.getCurrentImagePath();
    PageObjectLogging.log("Change image by tap left edge", "works", "doesn't work", ! currentImageSrc.equals(nextImageSrc));
    if (currentImageSrc.equals(nextImageSrc)) failTest = true;
    currentImageSrc = lightbox.getCurrentImagePath();
    touchAction.tapOnPointXY(75, 50, 500, 5000);
    nextImageSrc = lightbox.getCurrentImagePath();
    PageObjectLogging.log("Change image by tap right edge", "works", "doesn't work", ! currentImageSrc.equals(nextImageSrc));
    if (currentImageSrc.equals(nextImageSrc)) failTest = true;
    lightbox.clickCloseButton();
    lightbox.clickGalleryImage(0);
    Assertion.assertTrue(lightbox.isCurrentImageVisible(), "Image is not visible");
    PageObjectLogging.log("Current image", "is visible", true);
    currentImageSrc = lightbox.getCurrentImagePath();
    boolean imageChanged = false;
    for (int i = 0; i < 10; ++i) {
      touchAction.swipeFromPointToPoint(70, 50, 20, 50, 300, 5000);
      nextImageSrc = lightbox.getCurrentImagePath();
      if (!nextImageSrc.contains(currentImageSrc)) {
        imageChanged = true;
        break;
      }
    }
    PageObjectLogging.log("Change image by swipe left", "works", "does not work", imageChanged);
    if (! imageChanged) failTest = true;
    currentImageSrc = lightbox.getCurrentImagePath();
    imageChanged = false;
    for (int i = 0; i < 10; ++i) {
      touchAction.swipeFromPointToPoint(20, 50, 70, 50, 300, 5000);
      nextImageSrc = lightbox.getCurrentImagePath();
      if (!nextImageSrc.contains(currentImageSrc)) {
        imageChanged = true;
        break;
      }
    }
    PageObjectLogging.log("Change image by swipe right", "works", "does not work", imageChanged);
    if (! imageChanged) failTest = true;
    base.failTest(failTest);
  }

  // MT03
  @Test(groups = {"MercuryLightboxTest_003", "MercuryLightboxTests", "Mercury"})
  public void MercuryLightboxTest_003_ZoomByGesture_ZoomByDoubleTap() {
    BasePageObject base = new BasePageObject(driver);
    base.openMercuryArticleByName(wikiURL, MercuryArticles.MERCURY_GALLERY_TEST_TWO);
    PerformTouchAction touchAction = new PerformTouchAction(driver);
    LightboxComponentObject lightbox = new LightboxComponentObject(driver);
    lightbox.clickGalleryImage(0);
    Assertion.assertTrue(lightbox.isCurrentImageVisible(), "Image is not visible");
    PageObjectLogging.log("Current image", "is visible", true);
    File beforeZooming = new Shooter().capturePage(driver);
    touchAction.zoomInOutPointXY(50, 50, 50, 100, PerformTouchAction.ZOOM_WAY_IN, 3000);
    File afterZooming = new Shooter().capturePage(driver);
    PageObjectLogging.log("Zooming in by gesture", "works", "does not work",
                          !new ImageComparison().areFilesTheSame(beforeZooming, afterZooming));
    if (new ImageComparison().areFilesTheSame(beforeZooming, afterZooming)) failTest = true;
    touchAction.zoomInOutPointXY(50, 50, 50, 140, PerformTouchAction.ZOOM_WAY_OUT, 3000);
    afterZooming = new Shooter().capturePage(driver);
    PageObjectLogging.log("Zooming out by gesture", "works", "does not work",
                          new ImageComparison().areFilesTheSame(beforeZooming, afterZooming));
    if (! new ImageComparison().areFilesTheSame(beforeZooming, afterZooming)) failTest = true;
    lightbox.clickCloseButton();
    lightbox.clickGalleryImage(0);
    Assertion.assertTrue(lightbox.isCurrentImageVisible(), "Image is not visible");
    PageObjectLogging.log("Current image", "is visible", true);
    beforeZooming = new Shooter().capturePage(driver);
    touchAction.tapOnPointXY(50, 50, 140, 0);
    touchAction.tapOnPointXY(50, 50, 140, 3000);
    afterZooming = new Shooter().capturePage(driver);
    PageObjectLogging.log("Zooming in by double tap", "works", "does not work",
                          !new ImageComparison().areFilesTheSame(beforeZooming, afterZooming));
    if (new ImageComparison().areFilesTheSame(beforeZooming, afterZooming)) failTest = true;
    touchAction.tapOnPointXY(50, 50, 140, 0);
    touchAction.tapOnPointXY(50, 50, 140, 3000);
    afterZooming = new Shooter().capturePage(driver);
    PageObjectLogging.log("Zooming out by double tap", "works", "does not work", new ImageComparison().areFilesTheSame(beforeZooming, afterZooming));
    if (! new ImageComparison().areFilesTheSame(beforeZooming, afterZooming)) failTest = true;
    base.failTest(failTest);
  }

  // MT04
  @Test(groups = {"MercuryLightboxTest_004", "MercuryLightboxTests", "Mercury"})
  public void MercuryLightboxTest_004_UIShow_UIHide() {
    BasePageObject base = new BasePageObject(driver);
    base.openMercuryArticleByName(wikiURL, MercuryArticles.MERCURY_GALLERY_TEST_TWO);
    PerformTouchAction touchAction = new PerformTouchAction(driver);
    LightboxComponentObject lightbox = new LightboxComponentObject(driver);
    lightbox.clickGalleryImage(0);
    Assertion.assertTrue(lightbox.isLightboxHeaderDisplayed(), "Lightbox header isn't displayed");
    Assertion.assertTrue(lightbox.isLightboxFooterDisplayed(), "Lightbox footer isn't displayed");
    touchAction.tapOnPointXY(50, 50, 500, 5000);
    Assertion.assertFalse(lightbox.isLightboxHeaderDisplayed(), "Lightbox header is displayed");
    Assertion.assertFalse(lightbox.isLightboxFooterDisplayed(), "Lightbox footer is displayed");
    touchAction.tapOnPointXY(50, 50, 500, 5000);
    Assertion.assertTrue(lightbox.isLightboxHeaderDisplayed(), "Lightbox header isn't displayed");
    Assertion.assertTrue(lightbox.isLightboxFooterDisplayed(), "Lightbox footer isn't displayed");
    base.failTest(failTest);
  }

  // MT05
  @Test(groups = {"MercuryLightboxTest_005", "MercuryLightboxTests", "Mercury"})
  public void MercuryLightboxTest_005_BackButtonCloseLightbox() {
    AndroidDriver mobileDriver = NewDriverProvider.getMobileDriver();
    BasePageObject base = new BasePageObject(driver);
    base.openMercuryArticleByName(wikiURL, MercuryArticles.MERCURY_GALLERY_TEST_TWO);
    LightboxComponentObject lightbox = new LightboxComponentObject(driver);
    lightbox.clickGalleryImage(0);
    Assertion.assertTrue(lightbox.isLightboxOpened(), "Lightbox is closed");
    mobileDriver.execute(DriverCommand.GO_BACK, null);
    Assertion.assertFalse(lightbox.isLightboxOpened(), "Lightbox is opened");
    base.failTest(failTest);
  }

  // MT06
  @Test(groups = {"MercuryLightboxTest_006", "MercuryLightboxTests", "Mercury"})
  public void MercuryLightboxTest_006_MovingOnZoomedImage() {
    BasePageObject base = new BasePageObject(driver);
    base.openMercuryArticleByName(wikiURL, MercuryArticles.MERCURY_GALLERY_TEST_TWO);
    PerformTouchAction touchAction = new PerformTouchAction(driver);
    LightboxComponentObject lightbox = new LightboxComponentObject(driver);
    lightbox.clickGalleryImage(0);
    String direction = DIRECTION_LEFT;
    Assertion.assertTrue(lightbox.isCurrentImageVisible(), "Image is not visible");
    PageObjectLogging.log("Current image", "is visible", true);
    File beforeZooming = new Shooter().capturePage(driver);
    touchAction.tapOnPointXY(50, 50, 140, 0);
    touchAction.tapOnPointXY(50, 50, 140, 2000);
    File afterZooming = new Shooter().capturePage(driver);
    PageObjectLogging.log("Zooming in", "works", "does not work", ! new ImageComparison().areFilesTheSame(beforeZooming, afterZooming));
    if (new ImageComparison().areFilesTheSame(beforeZooming, afterZooming)) failTest = true;
    touchAction.swipeFromCenterToDirection(direction, 200, 200, 2000);
    File afterMoving = new Shooter().capturePage(driver);
    PageObjectLogging.log("Moving " + direction, "works", "does not work",
                          !new ImageComparison().areFilesTheSame(afterZooming, afterMoving));
    if (new ImageComparison().areFilesTheSame(afterZooming, afterMoving)) failTest = true;
    lightbox.clickCloseButton();
    lightbox.clickGalleryImage(0);
    direction = DIRECTION_RIGHT;
    Assertion.assertTrue(lightbox.isCurrentImageVisible(), "Image is not visible");
    PageObjectLogging.log("Current image", "is visible", true);
    beforeZooming = new Shooter().capturePage(driver);
    touchAction.tapOnPointXY(50, 50, 140, 0);
    touchAction.tapOnPointXY(50, 50, 140, 2000);
    afterZooming = new Shooter().capturePage(driver);
    PageObjectLogging.log("Zooming in", "works", "does not work",
                          !new ImageComparison().areFilesTheSame(beforeZooming, afterZooming));
    if (new ImageComparison().areFilesTheSame(beforeZooming, afterZooming)) failTest = true;
    touchAction.swipeFromCenterToDirection(direction, 200, 200, 2000);
    afterMoving = new Shooter().capturePage(driver);
    PageObjectLogging.log("Moving " + direction, "works", "does not work",
                          !new ImageComparison().areFilesTheSame(afterZooming, afterMoving));
    if (new ImageComparison().areFilesTheSame(afterZooming, afterMoving)) failTest = true;
    lightbox.clickCloseButton();
    lightbox.clickGalleryImage(0);
    direction = DIRECTION_UP;
    Assertion.assertTrue(lightbox.isCurrentImageVisible(), "Image is not visible");
    PageObjectLogging.log("Current image", "is visible", true);
    beforeZooming = new Shooter().capturePage(driver);
    touchAction.tapOnPointXY(50, 50, 140, 0);
    touchAction.tapOnPointXY(50, 50, 140, 2000);
    afterZooming = new Shooter().capturePage(driver);
    PageObjectLogging.log("Zooming in", "works", "does not work",
                          !new ImageComparison().areFilesTheSame(beforeZooming, afterZooming));
    if (new ImageComparison().areFilesTheSame(beforeZooming, afterZooming)) failTest = true;
    touchAction.swipeFromCenterToDirection(direction, 200, 200, 2000);
    afterMoving = new Shooter().capturePage(driver);
    PageObjectLogging.log("Moving " + direction, "works", "does not work",
                          !new ImageComparison().areFilesTheSame(afterZooming, afterMoving));
    if (new ImageComparison().areFilesTheSame(afterZooming, afterMoving)) failTest = true;
    lightbox.clickCloseButton();
    lightbox.clickGalleryImage(0);
    direction = DIRECTION_DOWN;
    Assertion.assertTrue(lightbox.isCurrentImageVisible(), "Image is not visible");
    PageObjectLogging.log("Current image", "is visible", true);
    beforeZooming = new Shooter().capturePage(driver);
    touchAction.tapOnPointXY(50, 50, 140, 0);
    touchAction.tapOnPointXY(50, 50, 140, 2000);
    afterZooming = new Shooter().capturePage(driver);
    PageObjectLogging.log("Zooming in", "works", "does not work",
                          !new ImageComparison().areFilesTheSame(beforeZooming, afterZooming));
    if (new ImageComparison().areFilesTheSame(beforeZooming, afterZooming)) failTest = true;
    touchAction.swipeFromCenterToDirection(direction, 200, 200, 2000);
    afterMoving = new Shooter().capturePage(driver);
    PageObjectLogging.log("Moving " + direction, "works", "does not work", ! new ImageComparison().areFilesTheSame(afterZooming, afterMoving));
    if (new ImageComparison().areFilesTheSame(afterZooming, afterMoving)) failTest = true;
    base.failTest(failTest);
  }
}
