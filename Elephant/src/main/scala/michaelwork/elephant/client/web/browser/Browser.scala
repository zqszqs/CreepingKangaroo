package com.stubhub.qe.platform.elephant.client.web.browser

import com.stubhub.qe.platform.elephant.agency.manage.UXDriverAgent
import com.stubhub.qe.platform.elephant.log.LogRepository
import com.stubhub.qe.platform.elephant.protocol.Locator
import org.openqa.selenium.{Cookie, Dimension, WebElement}

import scala.collection.JavaConverters._

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Aug. 21 2014
 */
class Browser(val agentId: String, val agent: UXDriverAgent) extends IBrowser {
    private def loggedBrowser: LoggedBrowser = LoggedBrowser(agentId, agent, LogRepository.current.newBlock)

    override def click(locator: Locator): IBrowser = {
        loggedBrowser.click(locator)
    }

    override def openURL(url: String): IBrowser = {
        loggedBrowser.openURL(url)
    }

    override def shutdown: IBrowser = loggedBrowser.shutdown

    override def getCookie(name: String): Cookie = agent.cookie(name).orNull

    override def authenticateAlert(name: String, password: String): IBrowser = loggedBrowser.authenticateAlert(name, password)

    override def executeJS(locator: Locator, javascript: String): String = loggedBrowser.executeJS(locator, javascript)

    override def input(locator: Locator, text: String): IBrowser = loggedBrowser.input(locator, text)

    override def available(locator: Locator): Boolean = agent.available(locator)

    override def getAllCookies: java.util.List[Cookie] = agent.cookies.asJava

    override def elementCss(locator: Locator, attrName: String): String = agent.css(locator, attrName).orNull

    override def dismissAlert(): IBrowser = loggedBrowser.dismissAlert()

    override def maximizeWindow(): IBrowser = loggedBrowser.maximizeWindow()

    override def windowSize: Dimension = agent.windowSize

    override def removeCooke(name: String): IBrowser = loggedBrowser.removeCooke(name)

    override def resizeWindow(width: Int, height: Int): IBrowser = loggedBrowser.resizeWindow(width, height)

    override def refresh(): IBrowser = loggedBrowser.refresh()

    override def currentUrl: String = agent.url

    override def visible(locator: Locator): Boolean = agent.visible(locator)

    override def forward(): IBrowser = loggedBrowser.forward()

    override def title: String = agent.title

    override def html(locator: Locator): String = agent.html(locator).orNull

    override def backward(): IBrowser = loggedBrowser.backward()

    override def getText(locator: Locator): String = agent.text(locator).orNull

    override def removeAllCookies(): IBrowser = loggedBrowser.removeAllCookies()

    override def switchToWindow(name: String): IBrowser = loggedBrowser.switchToWindow(name)

    override def clickAt(x: Int, y: Int): IBrowser = loggedBrowser.clickAt(x, y)

    override def switchToFrame(locator: Locator): IBrowser = loggedBrowser.switchToFrame(locator)

    override def captureElementScreen(locator: Locator): String = agent.takePicture(locator).orNull

    override def selected(locator: Locator): Boolean = agent.selected(locator).getOrElse(false)

    override def enabled(locator: Locator): Boolean = agent.enabled(locator).getOrElse(false)

    override def elementAttr(locator: Locator, attrName: String): String = agent.attribute(locator, attrName).orNull

    override def captureScreen: String = agent.takePicture

    override def acceptAlert(): IBrowser = loggedBrowser.acceptAlert()

    override def elements(locator: Locator): java.util.List[WebElement] = agent.findElements(locator).asJava

    override def element(locator: Locator): WebElement = agent.findElement(locator).orNull
}

object Browser {
    def apply(agentId: String, agent: UXDriverAgent): Browser = new Browser(agentId, agent)
}