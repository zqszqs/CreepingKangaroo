package com.stubhub.qe.platform.elephant.client.web

import java.util

import com.stubhub.qe.platform.elephant.assertion.Assertion
import com.stubhub.qe.platform.elephant.client.web.browser.IBrowser

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Aug. 22 2014
 */
abstract class Flow extends WebStepChain {
    private var defaultBrowser: IBrowser = null
    private val values: util.List[AnyRef] = new util.ArrayList[AnyRef]()

    def start(browser: IBrowser, args: Array[AnyRef]): (Assertion, util.List[AnyRef]) = {
        defaultBrowser = browser
        execute(args)
        (assertion, values)
    }

    @throws[Exception]
    def execute(args: Array[AnyRef])

    protected def addToReturn(obj: AnyRef): Unit = values.add(obj)

    override protected def browser: IBrowser = defaultBrowser
}
