package com.stubhub.qe.platform.elephant.protocal

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Aug. 14 2014
 */
case class Contract(className: String, methodName: String, agentId: String) {
    def is(contract: Contract): Boolean = {
        (contract.className equals className) &&
            (contract.methodName equals methodName) &&
            (contract.agentId equals agentId)
    }
}
