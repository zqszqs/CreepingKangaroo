package com.stubhub.qe.platform.elephant.agency.driverinfo

import scala.collection.immutable.HashMap

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Aug. 13 2014
 */
class  DriverInfo

case class ChromeDriverInfo(keys: Set[String], configs: Map[String, String]) extends DriverInfo

case class FirefoxDriverInfo(keys: Set[String], configs: Map[String, String]) extends DriverInfo