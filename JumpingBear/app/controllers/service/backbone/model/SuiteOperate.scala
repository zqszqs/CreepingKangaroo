package controllers.service.backbone.model

import play.api.mvc.Controller
import play.modules.reactivemongo.MongoController
import scala.concurrent.{Await, Future}
import controllers.service.backbone.modelobj.ServiceSuite
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import reactivemongo.core.commands.LastError
import BSONUtils._
import scala.concurrent.duration.Duration
import reactivemongo.api.collections.default.BSONCollection
import controllers.service.backbone.modelobj.ServiceSuite.ServiceSuiteReader
import controllers.service.backbone.modelobj.ServiceSuite.ServiceSuiteWriter
import play.api.libs.concurrent.Execution.Implicits._

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 03 2014
 */
object SuiteOperate extends Controller with MongoController {
    val suite = db.collection[BSONCollection]("service_suite")

    def createNew(name: String): (Future[LastError], ServiceSuite) = {
        val doc = ServiceSuite(BSONObjectID.generate, name)
        (suite.insert(doc), doc)
    }

    def update(serSuite: ServiceSuite): Future[LastError] = {
        suite.update(select(serSuite.id.stringify), serSuite)
    }

    def update(id: String, name: Option[String]): Future[LastError] = {
        query(id).map {
            case None => LastError(ok = false, None, None, None, None, 0, updatedExisting = false)
            case Some(doc) => {
                val updateDoc = ServiceSuite(BSONObjectID(id), name.getOrElse(doc.name))
                Await.result(update(updateDoc), Duration.Inf)
            }
        }
    }

    def delete(id: String) = {
        TestOperate.deleteBySuiteId(id)
        suite.remove(select(id))
    }

    def delete(serSuite: ServiceSuite) = {
        TestOperate.deleteBySuiteId(serSuite.id.stringify)
        suite.remove(serSuite)
    }

    def query(id: String): Future[Option[ServiceSuite]] = suite.find(select(id)).one[ServiceSuite]

    def all(): Future[List[ServiceSuite]] = suite.find(BSONDocument()).cursor[ServiceSuite].collect[List]()
}
