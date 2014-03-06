package controllers.service.backbone.model

import play.api.mvc.Controller
import play.modules.reactivemongo.MongoController
import scala.concurrent.{Await, Future}
import scala.Some
import reactivemongo.core.commands.LastError
import controllers.service.backbone.modelobj.{ServiceRequest, RequestOutput, Header, RequestInput}
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import reactivemongo.api.collections.default.BSONCollection
import scala.concurrent.duration.Duration
import BSONUtils._
import controllers.service.backbone.modelobj.ServiceRequest.ServiceRequestReader
import controllers.service.backbone.modelobj.ServiceRequest.ServiceRequestWriter
import play.api.libs.concurrent.Execution.Implicits._

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 03 2014
 */
object RequestOperate extends Controller with MongoController {
    val request = db.collection[BSONCollection]("service_request")


    def createNew(name: String, testId: String, description: String, position: Int, input: RequestInput, output: RequestOutput): (Future[LastError], ServiceRequest) = {
        val doc = ServiceRequest(BSONObjectID.generate, name, testId, description, position, input, output, None)

        (request.insert(doc), doc)
    }

    def update(serRequest: ServiceRequest): Future[LastError] = {
        request.update(select(serRequest.id.stringify), serRequest)
    }

    def update(id: String, testId: Option[String], name: Option[String], position: Option[Int],
               description: Option[String], input: Option[RequestInput], output: Option[RequestOutput]): Future[LastError] = {
        query(id).map {
            case None => LastError(ok = false, None, None, None, None, 0, updatedExisting = false)
            case Some(doc) => {
                val updateDoc = ServiceRequest(
                    BSONObjectID(id),
                    name.getOrElse(doc.name),
                    testId.getOrElse(doc.testId),
                    description.getOrElse(doc.description),
                    position.getOrElse(doc.position),
                    input.getOrElse(doc.input),
                    output.getOrElse(doc.output),
                    None
                )
                Await.result(update(updateDoc), Duration.Inf)
            }
        }
    }

    def delete(id: String): Future[LastError] = {
        Await.result(query(id).map {
            case Some(req) => {
                ExecutingOperate.deleteExecuteRequest(req.testId, req.id.stringify)
            }
        }, Duration.Inf)
        request.remove(select(id))
    }

    def delete(serRequest: ServiceRequest): Future[LastError] = {
        delete(serRequest.id.stringify)
    }

    def deleteByTestId(testId: String) = {
        request.remove(BSONDocument("testId" -> testId))
    }

    def query(id: String): Future[Option[ServiceRequest]] = request.find(select(id)).one[ServiceRequest]

    def queryByTest(testId: String): Future[List[ServiceRequest]] = {
        request.find(BSONDocument("testId" -> testId)).sort(BSONDocument("position" -> 1)).cursor[ServiceRequest].collect[List]()
    }

    def all(): Future[List[ServiceRequest]] = request.find(BSONDocument()).cursor[ServiceRequest].collect[List]()
}
