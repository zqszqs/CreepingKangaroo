package com.stubhub.qe.platform.elephant.agency.manage

import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

import com.stubhub.qe.platform.elephant.context.ContextTree
import com.stubhub.qe.platform.elephant.protocol._
import org.apache.commons.codec.binary.Base64.encodeBase64
import org.openqa.selenium.OutputType._
import org.openqa.selenium._
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.security.UserAndPassword
import org.openqa.selenium.support.ui.Select

import scala.collection.JavaConverters._



class UXDriverAgent(val remoteDriver: RemoteWebDriver, val context: ContextTree) extends DriverAgent(remoteDriver, context) {

    private def actions: Actions = new Actions(remoteDriver)
    private def intercept(locator: Locator, name: String): Unit = locator.config.get(name).map(_.handle(locator, this))

    def openURL(url: String): Unit = remoteDriver.get(url)

    def click(locator: Locator): Unit = {
        intercept(locator, "click:pre")
        findElement(locator).map(_.click())
        intercept(locator, "click:post")
    }

    def doubleClick(locator: Locator): Unit = {
        findElement(locator).map(actions.doubleClick)
    }

    def clickAt(coord: Coordinate): Unit = {
        actions.moveByOffset(coord.x, coord.y).click().perform()
    }

    def clickAndHold(locator: Locator): Unit = {
        findElement(locator).map(actions.clickAndHold)
    }

    def dragAndDrop(source: Locator, target: Locator): Unit = {
        (findElement(source), findElement(target)) match {
            case (Some(srcElement), Some(targetElement)) => actions.dragAndDrop(srcElement, targetElement)
            case _ =>
        }
    }

    def url: String = remoteDriver.getCurrentUrl

    def attribute(locator: Locator, name: String): Option[String] = findElement(locator).map(_.getAttribute(name))

    def css(locator: Locator, name: String): Option[String] = findElement(locator).map(_.getCssValue(name))

    def eval(locator: Locator, javascript: String): Option[String] = {
        findElement(locator).map((e) => {
            remoteDriver.asInstanceOf[JavascriptExecutor].executeScript(javascript, e).asInstanceOf[String]
        })
    }

    def eval(javascript: String): String = {
        remoteDriver.asInstanceOf[JavascriptExecutor].executeScript(javascript).asInstanceOf[String]
    }

    def point(locator: Locator): Option[Point] = findElement(locator).map(_.getLocation)

    def size(locator: Locator): Option[Dimension] = findElement(locator).map(_.getSize)

    def text(locator: Locator): Option[String] = {
        intercept(locator, "text:pre")
        findElement(locator).map(_.getText)
    }

    def visible(locator: Locator): Boolean = findElement(locator) match {
        case None => false
        case Some(e) => e.isDisplayed
    }

    def available(locator: Locator): Boolean = findElement(locator).isDefined

    def enabled(locator: Locator): Option[Boolean] = findElement(locator).map(_.isEnabled)

    def selected(locator: Locator): Option[Boolean] = findElement(locator).map(_.isSelected)

    def input(locator: Locator, text: String): Unit = {
        findElement(locator).map(_.sendKeys(text))
    }

    def takePicture: String = remoteDriver.asInstanceOf[TakesScreenshot].getScreenshotAs(BASE64)

    def takePicture(locator: Locator): Option[String] = {
        findElement(locator).map((e) => {
            val fullImage = ImageIO.read(remoteDriver.asInstanceOf[TakesScreenshot].getScreenshotAs(OutputType.FILE))
            val location = e.getLocation
            val size = e.getSize
            val out = new ByteArrayOutputStream()

            ImageIO.write(
                fullImage.getSubimage(location.getX, location.getY, size.width, size.height),
                "png",
                out
            )

            "data:image/png;base64,%s" format encodeBase64(out.toByteArray)
        })
    }

    def windowSize: Dimension = remoteDriver.manage().window().getSize

    def resizeWindow(width: Int, height: Int): Unit = {
        try{
            remoteDriver.manage().window().setPosition(new Point(0, 0))
            remoteDriver.manage().window().setSize(new Dimension(width, height))
        } catch {
            case e: Exception =>
        }
    }

    def maximizeWindow(): Unit = {
        remoteDriver.manage().window().maximize()
    }

    def forward(): Unit = remoteDriver.navigate().forward()

    def backward(): Unit = remoteDriver.navigate().back()

    def refresh(): Unit = remoteDriver.navigate().refresh()

    def goToUrl(url: String): Unit = remoteDriver.navigate().to(url)

    def cookie(name: String): Option[Cookie] = Option(remoteDriver.manage().getCookieNamed(name))

    def cookies: List[Cookie] = remoteDriver.manage().getCookies.asScala.toList

    def removeCookie(name: String) = remoteDriver.manage().deleteCookieNamed(name)

    def addCookie(name: String, value: String): Unit = {
        remoteDriver.manage().addCookie(new Cookie(name, value))
    }

    def clearCookies(): Unit = {
        remoteDriver.manage().deleteAllCookies()
    }

    def acceptAlert(accept: Boolean): Unit = {
        if (accept) remoteDriver.switchTo().alert().accept()
        else remoteDriver.switchTo().alert().dismiss()

        remoteDriver.switchTo().defaultContent()
    }

    def authenticateAlert(name: String, password: String): Unit = {
        remoteDriver.switchTo().alert().authenticateUsing(new UserAndPassword(name, password))
    }

    def findElement(locator: Locator): Option[WebElement] = {
        try {
            intercept(locator, "findElement:pre")
            Some(remoteDriver.findElement(identifierToSeleniumBy(locator.identifier)))
        } catch {
            case e: NoSuchElementException => throw e
        }
    }

    def findElements(locator: Locator): List[WebElement] = {
        remoteDriver.findElements(identifierToSeleniumBy(locator.identifier)).asScala.toList
    }

    def frame(locator: Locator): Unit = {
        findElement(locator).map(remoteDriver.switchTo().frame)
    }

    def window(name: String): Unit = {
        remoteDriver.switchTo().window(name)
    }

    def currentWindowHandle: String = remoteDriver.getWindowHandle

    def windowHandles: List[String] = remoteDriver.getWindowHandles.asScala.toList

    def html: String = remoteDriver.getPageSource

    def html(locator: Locator): Option[String] = eval(locator, "return arguments[0].innerHTML;")

    def title: String = remoteDriver.getTitle

    def pressKey(chars: String): Unit = remoteDriver.getKeyboard.pressKey(chars)

    def releaseKey(chars: String): Unit = remoteDriver.getKeyboard.releaseKey(chars)

    def sendKeys(chars: String): Unit = remoteDriver.getKeyboard.sendKeys(chars)

    def scrollDown(height: Int): Unit = {
        val scrollDownJS = "window.scrollBy(0, %d);" format height
        eval(scrollDownJS)
    }

    def mouseTo(locator: Locator): Unit = {
        findElement(locator).map(new Actions(remoteDriver).moveToElement)
    }

    def select(locator: Locator, index: Int): Unit = {
        findElement(locator).map(new Select(_).selectByIndex(index))
    }

    def select(locator: Locator, text: String): Unit = {
        findElement(locator).map(new Select(_).selectByVisibleText(text))
    }

    private def identifierToSeleniumBy(identifier: Identifier): By = identifier match {
        case ID(id) => By.id(id)
        case NAME(name) => By.name(name)
        case CLASS_NAME(className) => By.className(className)
        case LINK_TEXT(linkText) => By.linkText(linkText)
        case PARTIAL_LINK_TEXT(partial) => By.partialLinkText(partial)
        case CSS_SELECTOR(cssSelector) => By.cssSelector(cssSelector)
        case TAG_NAME(tag) => By.tagName(tag)
        case XPATH(xpath) => By.xpath(xpath)
        case _ => throw new Error("Unsupported identifier")
    }
}
