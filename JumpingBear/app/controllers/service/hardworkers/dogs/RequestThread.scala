package controllers.service.hardworkers.dogs

import scala.concurrent.Future
import controllers.service.backbone.modelobj.execute.ExecuteRequest
import controllers.service.backbone.modelobj.ServiceRequest

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 09 2014
 */

abstract class RequestThread(rid: String) {
    def id = rid
}

case class SingleRequestThread(tid: String, request: ServiceRequest) extends RequestThread(tid)
case class MultipleRequestThread(tid: String, requests: List[ServiceRequest]) extends RequestThread(tid)