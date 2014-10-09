package com.stubhub.qe.platform.elephant.client.web.browser

import java.util

import com.stubhub.qe.platform.elephant.agency.manage.UXDriverAgent
import com.stubhub.qe.platform.elephant.log.{Log, LogBlock}
import com.stubhub.qe.platform.elephant.protocol.{Coordinate, Locator}
import org.openqa.selenium.{Cookie, Dimension, WebElement}

import scala.collection.JavaConverters._

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Aug. 21 2014
 */
class LoggedBrowser(val agentId: String, val agent: UXDriverAgent, val logBlock: LogBlock) extends IBrowser {
    private def addLog(text: String): Unit = logBlock.addLog(BrowserLog(text))

    override def click(locator: Locator): IBrowser = {
        agent.click(locator)
        addLog("Click " + locator.name + " with " + locator.identifier.toString)
        this
    }

    override def openURL(url: String): IBrowser = {
        addLog("Opening url: " + url)
        agent.openURL(url)
        this
    }

    override def shutdown: IBrowser = {
        addLog("Shutting down browser")
        agent.retire()
        addLog("Browser shut down")
        this
    }

    override def clickAt(x: Int, y: Int): IBrowser = {
        addLog("Click at point(%d, %d)".format(x, y))
        agent.clickAt(new Coordinate(x, y))
        this
    }

    override def getCookie(name: String): Cookie = agent.cookie(name).orNull

    override def authenticateAlert(name: String, password: String): IBrowser = {
        addLog("Authenticate alert with %s/%s".format(name, password))
        agent.authenticateAlert(name, password)
        this
    }

    override def executeJS(locator: Locator, javascript: String): String = {
        addLog("Executing JS << %s >>".format(javascript))
        agent.eval(locator, javascript).orNull
    }

    override def input(locator: Locator, text: String): IBrowser = {
        addLog("Input %s to %s".format(text, locator.name))
        agent.input(locator, text)
        this
    }

    override def available(locator: Locator): Boolean = agent.available(locator)

    override def getAllCookies: util.List[Cookie] = agent.cookies.asJava

    override def elementCss(locator: Locator, attrName: String): String = agent.css(locator, attrName).orNull

    override def dismissAlert(): IBrowser = {
        addLog("Dismiss alert")
        agent.acceptAlert(accept = false)
        this
    }

    override def maximizeWindow(): IBrowser = {
        addLog("Maximize window to %sX%s".format(windowSize.width, windowSize.height))
        agent.maximizeWindow()
        this
    }

    override def windowSize: Dimension = agent.windowSize

    override def removeCooke(name: String): IBrowser = {
        addLog("Remove cookie %s:%s".format(name, getCookie(name).getValue))
        agent.removeCookie(name)
        this
    }

    override def resizeWindow(width: Int, height: Int): IBrowser = {
        addLog("Resize window to %sX%s".format(width, height))
        agent.resizeWindow(width, height)
        this
    }

    override def refresh(): IBrowser = {
        addLog("Refresh page")
        agent.refresh()
        this
    }

    override def currentUrl: String = agent.url

    override def visible(locator: Locator): Boolean = agent.visible(locator)

    override def forward(): IBrowser = {
        addLog("Browser moving forward")
        agent.forward()
        this
    }

    override def title: String = agent.title

    override def html(locator: Locator): String = agent.html(locator).orNull

    override def backward(): IBrowser = {
        addLog("Browser moving backward")
        agent.backward()
        this
    }

    override def getText(locator: Locator): String = agent.text(locator).orNull

    override def removeAllCookies(): IBrowser = {
        addLog("Remove all cookies")
        agent.clearCookies()
        this
    }

    override def switchToWindow(name: String): IBrowser = {
        addLog("Switch to window [%s]".format(name))
        agent.window(name)
        this
    }

    override def captureElementScreen(locator: Locator): String = agent.takePicture(locator).orNull

    override def switchToFrame(locator: Locator): IBrowser = {
        addLog("Switch to frame [%s]".format(locator.name))
        agent.frame(locator)
        this
    }

    override def elementAttr(locator: Locator, attrName: String): String = agent.attribute(locator, attrName).orNull

    override def selected(locator: Locator): Boolean = agent.selected(locator).getOrElse(false)

    override def enabled(locator: Locator): Boolean = agent.enabled(locator).getOrElse(false)

    override def acceptAlert(): IBrowser = {
        addLog("Accept the alert")
        agent.acceptAlert(accept = true)
        this
    }

    override def captureScreen: String = agent.takePicture

    override def elements(locator: Locator): java.util.List[WebElement] = agent.findElements(locator).asJava

    override def element(locator: Locator): WebElement = agent.findElement(locator).orNull
}

object LoggedBrowser {
    def apply(agentId: String, agent: UXDriverAgent, logBlock: LogBlock): LoggedBrowser = new LoggedBrowser(agentId, agent, logBlock)
}

class BrowserLog(content: String) extends Log("BROWSER", content)

object BrowserLog {
    def apply(content: String): BrowserLog = new BrowserLog(content)
}
