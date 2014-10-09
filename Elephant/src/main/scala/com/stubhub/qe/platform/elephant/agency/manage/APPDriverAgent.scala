package com.stubhub.qe.platform.elephant.agency.manage

import java.net.URL

import com.stubhub.qe.platform.elephant.context.ContextTree
import com.stubhub.qe.platform.elephant.protocol._
import io.appium.java_client.AppiumDriver
import org.apache.commons.codec.binary.Base64
import org.openqa.selenium.ScreenOrientation.{LANDSCAPE, PORTRAIT}
import org.openqa.selenium.{By, ScreenOrientation, WebElement}

import scala.collection.JavaConverters._

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Sep. 11 2014
 */
class APPDriverAgent(val appium: AppiumDriver, val context: ContextTree) extends DriverAgent(appium, context) {

    def reset(): Unit = appium.resetApp()

    def appString: String = appium.getAppStrings

    def currentActivity: String = appium.currentActivity()

    def importFile(path: String, data: String): Unit = appium.pushFile(path, Base64.encodeBase64(data.getBytes))

    def exportFile(path: String): Array[Byte] = appium.pullFile(path)

    def hideKeyboard(): Unit = appium.hideKeyboard()

    def backgroundApp(seconds: Int): Unit = appium.runAppInBackground(seconds)

    def tap(locator: Locator): Unit = findElement(locator).map(appium.tap(1, _, 1))

    def tap(locator: Locator, duration: Int): Unit = findElement(locator).map(appium.tap(1, _, duration))

    def tap(x: Int, y: Int, duration: Int): Unit = appium.tap(1, x, y, duration)

    def tap2(locator: Locator): Unit = findElement(locator).map(appium.tap(2, _, 1))

    def tap2(locator: Locator, duration: Int): Unit = findElement(locator).map(appium.tap(2, _, duration))

    def tap2(x: Int, y: Int, duration: Int): Unit = appium.tap(2, x, y, duration)

    def swipeLeft(row: Float): Unit = {
        val size = appium.manage().window().getSize
        val from = (int(size.getWidth * 0.1), int(size.getHeight * row))
        val to = (int(size.getWidth * 0.9), int(size.getHeight * row))
        appium.swipe(from._1, from._2, to._1, to._2, 2)
    }

    def swipeRight(row: Float): Unit = {
        val size = appium.manage().window().getSize
        val from = (int(size.getWidth * 0.9), int(size.getWidth * row))
        val to = (int(size.getWidth * 0.1), int(size.getWidth * row))
        appium.swipe(from._1, from._2, to._1, to._2, 2)
    }

    def pinch(locator: Locator): Unit = findElement(locator).map(appium.pinch)

    def zoom(locator: Locator): Unit = findElement(locator).map(appium.zoom)

    def appInstalled(bundleId: String): Boolean = appium.isAppInstalled(bundleId)

    def install(appPath: String): Unit = appium.installApp(appPath)

    def remove(bundleId: String): Unit = appium.removeApp(bundleId)

    def launchApp(): Unit = appium.launchApp()

    def closeApp(): Unit = appium.closeApp()

    def lockScreen(): Unit = appium.lockScreen(5)

    def lockScreen(duration: Int): Unit = appium.lockScreen(duration)

    def shake(): Unit = appium.shake()

    def scrollToText(text: String): Unit = appium.scrollTo(text)

    def appContext: String = appium.getContext

    def portrait(): Unit= appium.rotate(PORTRAIT)

    def landscape(): Unit = appium.rotate(LANDSCAPE)

    def orientation: ScreenOrientation = appium.getOrientation

    def remoteAddress: URL = appium.getRemoteAddress

    def eval(script: String): String = appium.executeScript(script).toString

    def drag = ???
    def drag2 = ???
    def longPress = ???
    def pressAndHold = ???
    def back = ???
    def home = ???
    def volumeUp = ???
    def volumeDown = ???
    def takePicture = ???

    def findElement(locator: Locator): Option[WebElement] = {
        try {
            locator.identifier match {
                case AndroidUI(using) => Some(appium.findElementByAndroidUIAutomator(using))
                case IosUI(using) => throw new IncompatibleClassChangeError("Cannot use IOS locator in Android")
                case id: Identifier => Some(appium.findElement(identifierToSeleniumBy(id)))
            }
        } catch {
            case e: NoSuchElementException => throw e
        }
    }

    def findElements(locator: Locator): List[WebElement] = locator.identifier match {
        case AndroidUI(using) => appium.findElementsByAndroidUIAutomator(using).asScala.toList
        case IosUI(using) => throw new IncompatibleClassChangeError("Cannot use IOS locator in Android")
        case id: Identifier => appium.findElements(identifierToSeleniumBy(locator.identifier)).asScala.toList
    }

    private def int(double: Double): Int = double.toInt

    private def identifierToSeleniumBy(identifier: Identifier): By = identifier match {
        case TEXT(text) => By.xpath("//android.widget.TextView[contains(@text, '%s')]" format text)
        case FULL_TEXT(text) => By.xpath("//android.widget.TextView[@text='%s']" format text)
        case TAG_NAME(name) => By.className(name)
        case NAME(name) => By.name(name)
        case FIND(value) => By.xpath(("//*[@content-desc=\"%s\" or @resource-id=\"%s\" or @text=\"%s\"] | " +
            "//*[contains(translate(@content-desc, \"%s\", \"%s\"), \"%s\") or contains(translate(@text, \"%s\", \"%s\"), \"%s\") or @resource-id=\"%s\"").format(
                value, value, value, value, value, value, value, value, value, value
            ))
        case XPATH_INDEX(index) => By.xpath("//android.widget.TextView[%d]" format index)

    }
}
