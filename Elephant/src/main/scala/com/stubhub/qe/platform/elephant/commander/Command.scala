package com.stubhub.qe.platform.elephant.commander

import com.stubhub.qe.platform.elephant.protocal.{Contract, Locator}

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Aug. 14 2014
 */
class Command

case class ClickCommand(locator: Locator, contract: Contract) extends Command
