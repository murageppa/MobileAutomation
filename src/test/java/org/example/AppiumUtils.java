package org.example;

import com.google.common.collect.ImmutableList;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import lombok.SneakyThrows;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class AppiumUtils {

    @SneakyThrows
    public static void scrollDownTo(By byOfElementToBeFound, AppiumDriver driver) {
        List<WebElement> elements;
        int i = 0;
        Thread.sleep(500);
        while (i < 12) {
            Thread.sleep(500);
            elements = driver.findElements(byOfElementToBeFound);
            if (!elements.isEmpty() && elements.get(0).isDisplayed()) return;
            scrollDown(driver);
            i++;
            AppiumUtils.scrollDown(driver);
        }
        Assert.fail("Did not find : " + byOfElementToBeFound.toString());
    }

    public static WebElement scrollToElementUsingText(String elementText, AppiumDriver driver) {
        if (driver instanceof AndroidDriver) {
            return driver.findElement(
                    new AppiumBy.ByAndroidUIAutomator(
                            "new UiScrollable(new UiSelector().scrollable(true).instance(0))"
                                    + ".scrollIntoView(new UiSelector().text(\"" + elementText + "\").instance(0));"
                    )
            );
        }
        return driver.findElement(AppiumBy.iOSNsPredicateString("name == '" + elementText + "'"));
    }

    @SneakyThrows
    public static void scrollUpTo(By byOfElementToBeFound, AppiumDriver driver) {
        List<WebElement> elements;
        int i = 0;
        Thread.sleep(500);
        while (i < 12) {
            Thread.sleep(500);
            elements = driver.findElements(byOfElementToBeFound);
            if (!elements.isEmpty() && elements.get(0).isDisplayed()) return;
            scrollUp(driver);
            i++;
        }
        Assert.fail("Did not find : " + byOfElementToBeFound.toString());
    }

    @SneakyThrows
    public static void scrollDownOnElementTo(By byOfElementToScrollOn, By byOfElementToBeFound, AppiumDriver driver) {
        int i = 0;
        while (i < 12) {
            Thread.sleep(500);
            if (!driver.findElements(byOfElementToBeFound).isEmpty()) return;
            scrollDownOnElement(byOfElementToScrollOn, driver);
            i++;
        }
        Assert.fail("Did not find : " + byOfElementToBeFound.toString());
    }

    public static void scrollDown(AppiumDriver driver) {
        int height = driver.manage().window().getSize().getHeight();
        int width = driver.manage().window().getSize().getWidth();
        swipe(width / 2, height * 2 / 7, width / 2, height / 7, 1000, driver);
    }

    public static void scrollUp(AppiumDriver driver) {
        int height = driver.manage().window().getSize().getHeight();
        int width = driver.manage().window().getSize().getWidth();
        swipe(width / 2, height / 4, width / 2, height * 2 / 4, 1000, driver);
    }

    /**
     * Scroll down to particular element
     * @param byOfElementToScrollOn
     * @param driver
     */
    public static void scrollDownOnElement(By byOfElementToScrollOn, AppiumDriver driver) {
        int x = driver.findElement(byOfElementToScrollOn).getLocation().getX();
        int y = driver.findElement(byOfElementToScrollOn).getLocation().getY();
        int height = driver.findElement(byOfElementToScrollOn).getSize().getHeight();
        int width = driver.findElement(byOfElementToScrollOn).getSize().getWidth();
        int startX = (width / 2) + x;
        int endX = (width / 2) + x;
        int startY = (height * 2 / 3) + y;
        int endY = (height / 3) + y;
        swipe(startX, startY, endX, endY, 1000, driver);
    }


    /**
     * General swipe function based on the co-ordinates
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @param durationInMilliSeconds
     * @param driver
     */
    public static void swipe(int startX, int startY, int endX, int endY, int durationInMilliSeconds, AppiumDriver driver) {

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence dragNDrop = new Sequence(finger, 1);

        dragNDrop.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        dragNDrop.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        dragNDrop.addAction(finger.createPointerMove(Duration.ofMillis(durationInMilliSeconds), PointerInput.Origin.viewport(), endX, endY));
        dragNDrop.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(ImmutableList.of(dragNDrop));
    }

    public static void tapOnElement(WebElement element, int durationInMilliSeconds, AppiumDriver driver) {

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence dragNDrop = new Sequence(finger, 1);

        dragNDrop.addAction(
                finger.createPointerMove(
                        Duration.ZERO,
                        PointerInput.Origin.viewport(),
                        element.getLocation().getX(),
                        element.getLocation().getY()
                )
        );
        dragNDrop.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        dragNDrop.addAction(new Pause(finger, Duration.ofMillis(durationInMilliSeconds)));
        dragNDrop.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(ImmutableList.of(dragNDrop));
    }

    public static WebElement waitForElementToVisible(AppiumDriver driver, WebElement element, int seconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        wait.until(ExpectedConditions.visibilityOf(element));
        return element;
    }

    /**
     * Switch the driver's context to a WebView in an Android application.
     * This method attempts to switch the context of the AndroidDriver to a WebView context up to five times.
     *
     * @param driver
     */
    @SneakyThrows
    public static void switchToContext(AndroidDriver driver) {
        int count = 0;
        while (count < 5) {
            Set<String> contextHandles = driver.getContextHandles();
            for (String contextName : contextHandles) {
                if (contextName.contains("WEBVIEW")) {
                    driver.context(contextName);
                    System.out.println("switched to context: " + contextName);
                    return;
                } else {
                    Thread.sleep(1000);
                    count++;
                }
            }

        }
    }

    public static void switchBackToNativeContext(AndroidDriver driver) {
        driver.context("NATIVE_APP");
    }

    /**
     * Generic Method to perform click action on Keypad for IOS
     *
     * @param cashAmount
     * @param driver
     */
    public static void enterAmount(int cashAmount, WebDriver driver) {
        String amount = String.valueOf(cashAmount);
        for (int amountIndex = 0; amountIndex < amount.length(); amountIndex++) {
            String path = "//XCUIElementTypeButton[contains(@name," + amount.charAt(amountIndex) + ")]";
            driver.findElement(By.xpath(path)).click();
        }
    }

    /**
     * tap on the given co-ordinates.
     * Specifically used to handle webDriverAgent notification after clicking on copyIcon
     *
     * @param driver
     * @param x
     * @param y
     */
    public static void tapByCoordinates(AppiumDriver driver, int x, int y) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1);
        tap.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), x, y));
        driver.perform(List.of(tap));
        //performing tap action again to handle native webDriverAgent notification for copyIcon functionality
        driver.perform(List.of(tap));
    }

    public static void scrollToElement(String text, String direction, IOSDriver driver) {
        HashMap<Object, Object> scrollObject = new HashMap<>();
        scrollObject.put("predicateString", "value == '" + text + "'");
        scrollObject.put("direction", direction);
        ((JavascriptExecutor) driver).executeScript("mobile: scroll", scrollObject);
    }

}

