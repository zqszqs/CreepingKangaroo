package com.stubhub.qe.platform.elephant.agency.manage

import com.stubhub.qe.platform.elephant.context.ContextTree
import io.appium.java_client.AppiumDriver

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Sep. 11 2014
 */
class AndroidDriverAgent(driver: AppiumDriver, context: ContextTree) extends APPDriverAgent(driver, context)

object AndroidDriverAgent {
    def apply(driver: AppiumDriver, context: ContextTree): AndroidDriverAgent = new AndroidDriverAgent(driver, context)
}
