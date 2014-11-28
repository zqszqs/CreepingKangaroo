package com.stubhub.qe.platform.elephant.agency.manage

import com.stubhub.qe.platform.elephant.context.ContextTree
import org.openqa.selenium.chrome.ChromeDriver

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Aug. 13 2014
 */

class ChromeUXDriverAgent(driver: ChromeDriver, context: ContextTree) extends UXDriverAgent(driver, context)

object ChromeUXDriverAgent {
    def apply(driver: ChromeDriver, context: ContextTree): ChromeUXDriverAgent = new ChromeUXDriverAgent(driver, context)
}
