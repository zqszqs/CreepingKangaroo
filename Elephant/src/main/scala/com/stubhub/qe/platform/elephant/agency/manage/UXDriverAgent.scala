package com.stubhub.qe.platform.elephant.agency.manage

import com.stubhub.qe.platform.elephant.protocal._
import org.openqa.selenium.{WebElement, By}
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.remote.{RemoteWebElement, RemoteWebDriver}

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Aug. 13 2014
 */

case class ChromeUXDriverAgent(driver: ChromeDriver) extends UXDriverAgent(driver)

case class FirefoxUXDriverAgent(driver: FirefoxDriver) extends UXDriverAgent(driver)

class UXDriverAgent(val remoteDriver: RemoteWebDriver) {
    def retire(): Unit = remoteDriver.quit()

    def click(locator: Locator): Unit = {
        findElement(locator).map(_.click())
    }

    def findElement(locator: Locator): Option[WebElement] = {
        try {
            Some(remoteDriver.findElement(identifierToSeleniumBy(locator.identifier)))
        } catch {
            case e: NoSuchElementException => None
        }
    }

    def identifierToSeleniumBy(identifier: Identifier): By = identifier match {
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
