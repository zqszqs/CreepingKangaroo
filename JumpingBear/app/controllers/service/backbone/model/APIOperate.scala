package controllers.service.backbone.model

import play.api.mvc.Controller
import play.modules.reactivemongo.MongoController
import play.api.libs.json.Json
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import scala.concurrent.{Await, Future}
import play.api.libs.json.JsObject
import controllers.service.backbone.modelobj.ServiceAPI
import reactivemongo.api.collections.default.BSONCollection
import controllers.service.backbone.modelobj.ServiceAPI.ServiceAPIWriter
import controllers.service.backbone.modelobj.ServiceAPI.ServiceAPIReader
import play.api.libs.concurrent.Execution.Implicits._
import BSONUtils._
import reactivemongo.core.commands.LastError
import scala.concurrent.duration.Duration

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 03 2014
 */

object APIOperate extends Controller with MongoController {

    val api = db.collection[BSONCollection]("service_api")

    def createNew(name: String, domain: String, endpoint: String): (Future[LastError], ServiceAPI) = {
        val serAPI = ServiceAPI(BSONObjectID.generate, name, domain, endpoint)
        (api.insert(serAPI), serAPI)
    }

    def update(serAPI: ServiceAPI) = {
        api.update(select(serAPI.id.stringify), serAPI)
    }

    def update(id: String, name: Option[String], domain: Option[String], endpoint: Option[String]): Future[LastError] = {
        query(id).map {
            case None => LastError(ok = false, None, None, None, None, 0, updatedExisting = false)
            case Some(doc) => {
                val updateDoc = ServiceAPI(BSONObjectID(id), name.getOrElse(doc.name), domain.getOrElse(doc.domain), endpoint.getOrElse(doc.endpoint))
                Await.result(update(updateDoc), Duration.Inf)
            }
        }
    }

    def delete(id: String) = {
        api.remove(select(id))
    }

    def delete(serAPI: ServiceAPI) = {
        api.remove(serAPI)
    }

    def query(id: String): Future[Option[ServiceAPI]] = {
        api.find(select(id)).one[ServiceAPI]
    }

    def all(): Future[List[ServiceAPI]] = {
        api.find(BSONDocument()).cursor[ServiceAPI].collect[List]()
    }
}
