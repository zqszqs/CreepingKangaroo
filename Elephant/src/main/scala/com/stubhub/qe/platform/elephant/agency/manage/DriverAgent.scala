package com.stubhub.qe.platform.elephant.agency.manage

import com.stubhub.qe.platform.elephant.context.ContextTree
import org.openqa.selenium.WebDriver

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Sep. 11 2014
 */
class DriverAgent(driver: WebDriver, context: ContextTree) {
    def retire(): Unit = driver.quit()
}
