package com.stubhub.qe.platform.elephant.agency.workshop

import org.openqa.selenium.firefox.FirefoxProfile

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Aug. 29 2014
 */
trait FirefoxProfileInjector {
    def inject(profile: FirefoxProfile): FirefoxProfile
}
