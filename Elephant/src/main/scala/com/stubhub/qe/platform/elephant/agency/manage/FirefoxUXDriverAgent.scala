package com.stubhub.qe.platform.elephant.agency.manage

import com.stubhub.qe.platform.elephant.context.ContextTree
import org.openqa.selenium.firefox.FirefoxDriver

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Aug. 22 2014
 */
class FirefoxUXDriverAgent(driver: FirefoxDriver, context: ContextTree) extends UXDriverAgent(driver, context)

object FirefoxUXDriverAgent {
    def apply(driver: FirefoxDriver, context: ContextTree): FirefoxUXDriverAgent = new FirefoxUXDriverAgent(driver, context)
}
