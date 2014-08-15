package com.stubhub.qe.platform.elephant.protocal

import org.openqa.selenium.By

import scala.collection.immutable.HashMap

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Aug. 15 2014
 */
class Locator(val name: String, val identifier: Identifier, val config: Map[String, AnyRef])
