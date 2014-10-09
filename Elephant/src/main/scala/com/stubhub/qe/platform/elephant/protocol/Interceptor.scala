package com.stubhub.qe.platform.elephant.protocol

import com.stubhub.qe.platform.elephant.agency.manage.UXDriverAgent

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Aug. 26 2014
 */
trait Interceptor {
    def handle(locator: Locator, agent: UXDriverAgent)
}
