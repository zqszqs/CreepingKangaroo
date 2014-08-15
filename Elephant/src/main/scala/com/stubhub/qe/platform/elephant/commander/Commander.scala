package com.stubhub.qe.platform.elephant.commander

import com.stubhub.qe.platform.elephant.agency.driverinfo.DriverInfo
import com.stubhub.qe.platform.elephant.agency.manage.DriverAgency
import com.stubhub.qe.platform.elephant.protocal.Contract

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Aug. 14 2014
 */
object Commander {
    def newWindow(className: String, methodName: String, driverInfo: DriverInfo): Contract =
        DriverAgency.recruit(className, methodName, driverInfo)

    def executeCommand(command: Command) = command match {
        case ClickCommand(locator, contract) => DriverAgency.agentWithContract(contract).map(_.click(locator))
    }

    def terminateContracts(contracts: List[Contract]) = contracts.foreach(
        (contract) => DriverAgency.agentWithContract(contract).map(_.retire())
    )

}
