package com.stubhub.qe.platform.elephant.client.web

import com.stubhub.qe.platform.elephant.client.StepChain
import com.stubhub.qe.platform.elephant.client.web.browser.IBrowser
import com.stubhub.qe.platform.elephant.protocol.Locator
import michaelwork.elephant.web.WebPage

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Aug. 15 2014
 */
abstract class WebStepChain extends StepChain {
    protected def browser: IBrowser

    protected def openURL(url: String): IBrowser = {
        browser.openURL(url)
    }

    protected def open(page: WebPage): IBrowser = {
        browser.openURL(page.url)
    }

    protected def click(locator: Locator): IBrowser = {
        browser.click(locator)
    }

    protected def input(locator: Locator, text: String): IBrowser = browser.input(locator, text)

    protected def text(locator: Locator): String = browser.getText(locator)

    protected def isVisible(locator: Locator): Boolean = browser.visible(locator)

    protected def isAvailable(locator: Locator): Boolean = browser.available(locator)

    protected def refresh(): IBrowser = browser.refresh()

    protected def cookie(name: String): String = browser.getCookie(name).getValue
}


