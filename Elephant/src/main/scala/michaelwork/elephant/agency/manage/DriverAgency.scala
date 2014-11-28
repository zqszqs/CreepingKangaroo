package com.stubhub.qe.platform.elephant.agency.manage

import com.stubhub.qe.platform.elephant.agency.workshop.DriverWorkshop
import com.stubhub.qe.platform.elephant.context.ContextTree
import com.stubhub.qe.platform.elephant.log.{Log, LogRepository, Logger}

import scala.util.Random


/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Aug. 14 2014
 */
object DriverAgency {
    var seats = List.empty[DriverCubic]

    def recruit(context: ContextTree): (String, DriverAgent) = {
        val newAgent = DriverWorkshop.produceAgent(context)
        val cubic = DriverCubic(System.currentTimeMillis() + Random.nextString(5), newAgent)

        seats = cubic :: seats

        val logBlock = LogRepository.current.newBlock
        logBlock.addLog(AgencyLog("Recruit a new agent of " + newAgent.getClass.getSimpleName + " and assigned with id " + cubic.id))

        (cubic.id, cubic.agent)
    }

    def getAgent(id: String): Option[DriverAgent] = seats.find((cubic) => cubic.id.equals(id)).map(_.agent)

    def retireAgent(id: String): Unit = {
        Logger.addLog(AgencyLog("Retire agent with id " + id))
        getAgent(id).map(_.retire())
    }
}


case class DriverCubic(id: String, agent: DriverAgent)

class AgencyLog(content: String) extends Log("DRIVER_AGENCY", content)

object AgencyLog {
    def apply(content: String): AgencyLog = new AgencyLog(content)
}