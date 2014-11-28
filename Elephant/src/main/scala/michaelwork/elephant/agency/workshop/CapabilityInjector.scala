package com.stubhub.qe.platform.elephant.agency.workshop

import org.openqa.selenium.remote.DesiredCapabilities

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Aug. 29 2014
 */
trait CapabilityInjector {
    def inject(capability: DesiredCapabilities): DesiredCapabilities
}
