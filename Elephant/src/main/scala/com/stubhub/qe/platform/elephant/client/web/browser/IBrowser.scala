package com.stubhub.qe.platform.elephant.client.web.browser

import com.stubhub.qe.platform.elephant.protocol.Locator
import org.openqa.selenium.{Cookie, Dimension, WebElement}

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Aug. 21 2014
 */
trait IBrowser {
    def click(locator: Locator): IBrowser

    def openURL(url: String): IBrowser

    def shutdown: IBrowser

    def clickAt(x: Int, y: Int): IBrowser

    def currentUrl: String

    def elementAttr(locator: Locator, attrName: String): String

    def elementCss(locator: Locator, attrName: String): String

    def executeJS(locator: Locator, javascript: String): String

    def windowSize: Dimension

    def getText(locator: Locator): String

    def visible(locator: Locator): Boolean

    def available(locator: Locator): Boolean

    def enabled(locator: Locator): Boolean

    def selected(locator: Locator): Boolean

    def input(locator: Locator, text: String): IBrowser

    def captureScreen: String

    def captureElementScreen(locator: Locator): String

    def resizeWindow(width: Int, height: Int): IBrowser

    def maximizeWindow(): IBrowser

    def forward(): IBrowser

    def backward(): IBrowser

    def refresh(): IBrowser

    def getCookie(name: String): Cookie

    def getAllCookies: java.util.List[Cookie]

    def removeCooke(name: String): IBrowser

    def removeAllCookies(): IBrowser

    def acceptAlert(): IBrowser

    def dismissAlert(): IBrowser

    def authenticateAlert(name: String, password: String): IBrowser

    def switchToFrame(locator: Locator): IBrowser

    def switchToWindow(name: String): IBrowser

    def html(locator: Locator): String

    def title: String

    def elements(locator: Locator): java.util.List[WebElement]

    def element(locator: Locator): WebElement
}
