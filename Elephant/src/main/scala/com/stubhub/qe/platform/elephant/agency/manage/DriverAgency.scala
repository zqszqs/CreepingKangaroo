package com.stubhub.qe.platform.elephant.agency.manage

import com.stubhub.qe.platform.elephant.agency.driverinfo.DriverInfo
import com.stubhub.qe.platform.elephant.agency.workshop.DriverWorkshop
import com.stubhub.qe.platform.elephant.protocal.Contract

import scala.util.Random


/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Aug. 14 2014
 */
object DriverAgency {
    var seats = List.empty[DriverCubic]

    def recruit(className: String, methodName: String, driverInfo: DriverInfo): Contract = {
        val newAgent = DriverWorkshop.produceAgent(driverInfo)
        val contract = Contract(className, methodName, Random.nextString(10))
        seats = DriverCubic(contract, newAgent) :: seats

        contract
    }

    def agentWithContract(contract: Contract): Option[UXDriverAgent] = seats.find(_.contract.is(contract)).map(_.agent)
}


case class DriverCubic(contract: Contract, agent: UXDriverAgent)
