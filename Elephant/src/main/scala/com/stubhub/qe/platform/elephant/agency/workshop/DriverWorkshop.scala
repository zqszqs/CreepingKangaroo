package com.stubhub.qe.platform.elephant.agency.workshop

import com.stubhub.qe.platform.elephant.agency.driverinfo.{FirefoxDriverInfo, ChromeDriverInfo, DriverInfo}
import com.stubhub.qe.platform.elephant.agency.manage.UXDriverAgent

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Aug. 13 2014
 */
class DriverWorkshop


object DriverWorkshop {
    def produceAgent(driverInfo: DriverInfo): UXDriverAgent = driverInfo match {
        case chrome: ChromeDriverInfo => ChromeDriverWorkshop.produceAgent(chrome)
        case firefox: FirefoxDriverInfo => FirefoxDriverWorkshop.produceAgent(firefox)
        case _ => ???
    }
}