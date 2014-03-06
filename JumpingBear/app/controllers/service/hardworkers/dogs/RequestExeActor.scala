package controllers.service.hardworkers.dogs

import play.api.libs.concurrent.Akka
import akka.actor.Actor
import controllers.service.backbone.model.{RequestOperate, ExecutingOperate}
import play.api.libs.json.{JsArray, Json, JsPath, JsObject}
import play.api.libs.functional.syntax._
import akka.actor.Status.Success
import controllers.service.backbone.modelobj.Header
import controllers.service.hardworkers.conveyor.Executor
import play.api.libs.json.JsObject
import controllers.service.hardworkers.dogs.MultipleRequestThread
import play.api.libs.json.JsArray
import controllers.service.hardworkers.dogs.SingleRequestThread
import controllers.service.backbone.modelobj.execute.{ExecuteOutput, ExecuteRequest}
import controllers.service.backbone.model.RequestUtils._
import play.api.libs.iteratee.Enumerator
import scala.concurrent.{Await, Channel}
import play.Logger
import scala.concurrent.duration.Duration
import utils.compatible.json.JsonCompatibleUtil
import utils.compatible.json.transform.JsonValue


/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 08 2014
 */
class RequestExeActor extends Actor {

    def _executeRequest(testId: String, request: ExecuteRequest): ExecuteRequest = {
        val executingReq = request.copy(status = "EXECUTING")
        try {
            val (exeStatus, exeOutput) = Executor.execute(executingReq.input)

            try {
                val bodyJs = Json.parse(exeOutput.body.get)
                val tempBodyJs = JsonValue.from("root", Json.parse(request.output.body.get))
                executingReq.copy(status = exeStatus, output = exeOutput)
            } catch {
                case e: Exception => {
                    executingReq.copy(status = exeStatus, output = exeOutput)
                }
            }
        } catch {
            case e: Exception => {
                executingReq.copy(status = "FAILURE")
            }
        }
    }

    def _compareResult(template: String, target: String): (String, List[(String, String)]) = {
        val result = JsonCompatibleUtil.compare(template, target)
        (result._1, result._2.map(r => (r.key, r.value.toString)))
    }

    def receive = {
        case (SingleRequestThread(tid, request), params: RuntimeParams) => {
            val exeReq = serviceRequestToExecuteRequest(request, params)
            ExecutingOperate.createNew(tid, List(exeReq))
            val executedReq = _executeRequest(tid, exeReq)
            val compareResult = _compareResult(request.output.body.getOrElse(""), executedReq.output.body.getOrElse(""))
            //TODO parameter passing, start here

            Await.result(ExecutingOperate.update(tid, List(executedReq.copy(output = executedReq.output.copy(body = Some(compareResult._1))))), Duration.Inf)
        }
        case (MultipleRequestThread(tid, requests), params: RuntimeParams) => {
            ExecutingOperate.createNew(tid, requests.map(req => serviceRequestToExecuteRequest(req, EmptyRuntimeParams)))
            requests.foldLeft(params)(
                (pre, cur) => {
                    try {
                        val executedReq = _executeRequest(tid, serviceRequestToExecuteRequest(cur, pre))
                        val compareResult = _compareResult(cur.output.body.getOrElse(""), executedReq.output.body.getOrElse(""))
                        //TODO parameter passing, start here

                        val comparedOutput = executedReq.output.copy(body = Some(compareResult._1))
                        Await.result(ExecutingOperate.update(tid, List(executedReq.copy(output = comparedOutput))), Duration.Inf)
                        ServiceRuntimeParams(pre, compareResult._2)
                    } catch {
                        case e: Exception => {
                            pre
                        }
                    }
                }
            )
        }
        case out: Channel[String] => {
            out.write("In hte acator")
        }
    }
}
