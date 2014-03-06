package controllers.service.backbone.birdbridge

import play.api.mvc.{WebSocket, SimpleResult, Action, Controller}
import controllers.service.backbone.model.{ExecutingOperate, TestOperate, RequestOperate}
import scala.util.{Failure, Success}
import play.api.libs.json.{JsString, JsValue, JsArray, JsObject}
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.concurrent.Akka
import akka.actor.Props
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.JsObject
import scala.concurrent.{Await, Future}
import controllers.service.hardworkers.dogs._
import controllers.service.backbone.model.RequestUtils._
import controllers.service.backbone.modelobj.{FunctionError, ServiceTest}
import controllers.service.backbone.modelobj.execute.ExecuteRequest
import controllers.service.hardworkers.dogs.MultipleRequestThread
import scala.util.Success
import scala.util.Failure
import scala.Some
import controllers.service.hardworkers.dogs.SingleRequestThread
import play.api.libs.iteratee.{Enumeratee, Concurrent, Enumerator, Iteratee}
import play.api.Logger
import scala.concurrent.duration.Duration

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 09 2014
 */
object ServiceExecuteController extends Controller {

    val requestExecutorActor = Akka.system.actorOf(Props[RequestExeActor], name = "RequestExecutorActor")

    def executeRequest(id: String) = Action {
        request => {
            RequestOperate.query(id).onComplete {
                case Success(data) => data match {
                    case None => BadRequest("No such request: " + id)
                    case Some(req) => {
                        requestExecutorActor !(SingleRequestThread(req.testId, req), EmptyRuntimeParams)
                    }
                }
                case _ => {}
            }
            NoContent
        }
    }

    def _executeTest(test: ServiceTest): Unit = _executeTest(test.id.stringify)

    def _executeTest(id: String): Unit = {
        RequestOperate.queryByTest(id).onComplete {
            case Success(requests) => {
                requestExecutorActor !(MultipleRequestThread(id, requests), EmptyRuntimeParams)
            }
            case _ => {}
        }
    }

    def executeTest(id: String) = Action {
        request => {
            _executeTest(id)
            NoContent
        }
    }

    def executeSuite(id: String) = Action {
        request => {
            TestOperate.queryBySuite(id).onComplete {
                case Success(tests) => {
                    tests.foreach(_executeTest)
                }
                case Failure(_) => {}
            }
            NoContent
        }
    }


    def getRequestStatus(tid: String, rid: String): Future[JsObject] = {
        def _getStatus(req: ExecuteRequest) = Json.obj(
            "forItem" -> "REQUEST",
            "testId" -> tid,
            "requestId" -> rid,
            "status" -> req.status,
            "input" -> req.input,
            "output" -> req.output
        )
        ExecutingOperate.queryRequest(tid, rid).map(optRequest => optRequest.map(_getStatus) match {
            case None => Json.obj(
                "forItem" -> "REQUEST",
                "testId" -> tid,
                "requestId" -> rid,
                "status" -> "NONE"
            )
            case Some(json) => json
        })
    }

    def getTestStatus(id: String, requestDetail: Boolean = false): Future[JsObject] = {
        ExecutingOperate.query(id).map {
            case None => Json.obj(
                "forItem" -> "TEST",
                "testId" -> id,
                "status" -> "NONE"
            )
            case Some(test) => {
                val status = test.requests.foldLeft("SUCCESS")(
                    (last, current) => (last, current.status) match {
                        case (_, "EXECUTING") => "EXECUTING"
                        case ("EXECUTING", _) => "EXECUTING"
                        case ("PENDING", _) => "PENDING"
                        case (_, "PENDING") => "PENDING"
                        case ("FAILURE", _) => "FAILURE"
                        case ("SUCCESS", s: String) => s
                        case ("SKIPPED", "SKIPPED") => "SKIPPED"
                        case ("SKIPPED", "FAILURE") => "FAILURE"
                        case ("SKIPPED", "SUCCESS") => "SKIPPED"
                    }
                )
                Json.obj(
                    "forItem" -> "TEST",
                    "testId" -> id,
                    "status" -> status,
                    "requests" -> JsArray(test.requests.map(req => {
                        val basic = Json.obj("id" -> req.id, "status" -> req.status)
                        if (requestDetail)
                            basic ++ Json.obj("input" -> req.input) ++ Json.obj("output" -> req.output)
                        else
                            basic
                    }))
                )
            }
        }
    }

    def _statusCriteriaReader = (
        (__ \ 'forItem).read[String] and
            (__ \ 'testId).read[String] and
            (__ \ 'requestId).readNullable[String]
        ).tupled

    def feedStatus = WebSocket.using[String] {
        request => {
            val (in, inStream) = Concurrent.joined[String]
            val extractData = Enumeratee.map[String] {
                data => {
                    try {
                        Json.parse(data).as(_statusCriteriaReader)
                    } catch {
                        case e: Exception => {
                            ("NO_ITEM", "NO_ITEM_ID", None)
                        }
                    }
                }
            }
            val fetchStatus = Enumeratee.mapM[(String, String, Option[String])] {
                pair => pair._1 match {
                    case "TEST" => getTestStatus(pair._2)
                    case "REQUEST" => pair._3 match {
                        case None => Future(FunctionError("ERROR", "MISSING requestId").asJson)
                        case Some(reqId) => getRequestStatus(pair._2, reqId)
                    }
                    case "ALL_REQUESTS" => getTestStatus(pair._2, true)
                    case "NO_ITEM" => Future(FunctionError("ERROR", "error processing input").asJson)
                    case s: String => Future(FunctionError("ERROR", s + " is not supported yet").asJson)
                }
            }
            val serializeStatus = Enumeratee.map[JsObject] {
                status => {
                    Json.stringify(status)
                }
            }

            val out = inStream through extractData through fetchStatus through serializeStatus

            (in, out)
        }
    }
}