package controllers.service.backbone.model

import scala.concurrent.{Await, Future}
import play.api.mvc.Controller
import play.modules.reactivemongo.MongoController
import controllers.service.backbone.modelobj.ServiceTest
import play.api.libs.concurrent.Execution.Implicits._
import reactivemongo.bson.{BSONObjectID, BSONDocument}
import reactivemongo.api.collections.default.BSONCollection
import scala.util.{Failure, Success}
import controllers.service.backbone.modelobj.execute.{ServiceExecute, ExecuteRequest}
import reactivemongo.core.commands.LastError
import controllers.service.backbone.model.BSONUtils._
import scala.concurrent.duration.Duration

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 09 2014
 */
object ExecutingOperate extends Controller with MongoController {
    val executing = db.collection[BSONCollection]("service_executing")

    def addNewExecute(testId: String, requests: List[ExecuteRequest]): (Future[LastError], ServiceExecute) = {
        val doc = ServiceExecute(BSONObjectID.generate, testId, requests)
        (executing.insert(doc), doc)
    }

    def createNew(testId: String, requests: List[ExecuteRequest]) = {
        query(testId).onComplete {
            case Success(optDoc) => {
                optDoc match {
                    case None => addNewExecute(testId, requests)
                    case Some(doc) => update(doc, requests)
                }
            }
            case _ => {}
        }
    }

    def update(execute: ServiceExecute): Future[LastError] = {
        executing.update(select(execute.id.stringify), execute)
    }

    def update(testId: String, requests: List[ExecuteRequest]): Future[LastError] = {
        query(testId).map(
            optExe => optExe.map(
                exe => Await.result(update(exe, requests), Duration.Inf)
            ).getOrElse(LastError(ok = false, None, None, None, None, 0, updatedExisting = false))
        )
    }

    def update(execute: ServiceExecute, requests: List[ExecuteRequest]): Future[LastError] = {
        val updateIds = requests.map(_.id)
        val same = execute.requests.filter(r => !updateIds.contains(r.id))
        val doc = execute.copy(requests = requests ::: same)
        update(doc)
    }

    def deleteExecuteRequest(testId: String, requestId: String) = {
        executing.update(
            BSONDocument("testId" -> testId),
            pull(BSONDocument("requests" -> BSONDocument("id" -> requestId)))
        )
    }

    def delete(testId: String) = {
        executing.remove(BSONDocument("testId" -> testId))
    }

    def query(testId: String): Future[Option[ServiceExecute]] = executing.find(BSONDocument("testId" -> testId)).one[ServiceExecute]

    def queryRequest(testId: String, requestId: String): Future[Option[ExecuteRequest]] = query(testId).map {
        case None => None
        case Some(doc) => doc.requests.find(d => d.id.equals(requestId))
    }
}
