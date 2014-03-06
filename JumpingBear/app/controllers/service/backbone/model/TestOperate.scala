package controllers.service.backbone.model

import play.api.mvc.Controller
import play.modules.reactivemongo.MongoController
import scala.concurrent.{Await, Future}
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.core.commands.LastError
import controllers.service.backbone.modelobj.ServiceTest
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import scala.concurrent.duration.Duration
import controllers.service.backbone.modelobj.ServiceTest.ServiceTestReader
import controllers.service.backbone.modelobj.ServiceTest.ServiceTestWriter
import play.api.libs.concurrent.Execution.Implicits._
import BSONUtils._

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 03 2014
 */
object TestOperate extends Controller with MongoController {
    val test = db.collection[BSONCollection]("service_test")

    def createNew(name: String, suiteId: String, forAPIId: String): (Future[LastError], ServiceTest) = {
        val doc = ServiceTest(BSONObjectID.generate, name, suiteId, forAPIId)
        (test.insert(doc), doc)
    }

    def update(serTest: ServiceTest): Future[LastError] = {
        test.update(select(serTest.id.stringify), serTest)
    }

    def update(id: String, suiteId: Option[String], name: Option[String], forAPIId: Option[String]): Future[LastError] = {
        query(id).map {
            case None => LastError(ok = false, None, None, None, None, 0, updatedExisting = false)
            case Some(doc) => {
                val updateDoc = ServiceTest(BSONObjectID(id), name.getOrElse(doc.name), suiteId.getOrElse(doc.suiteId), forAPIId.getOrElse(doc.forAPI))
                Await.result(update(updateDoc), Duration.Inf)
            }
        }
    }

    def delete(id: String) = {
        RequestOperate.deleteByTestId(id)
        test.remove(select(id))
    }

    def delete(serTest: ServiceTest) = {
        RequestOperate.deleteByTestId(serTest.id.stringify)
        test.remove(serTest)
    }

    def deleteBySuiteId(id: String) = test.remove(BSONDocument("suiteId" -> id))

    def query(id: String): Future[Option[ServiceTest]] = test.find(select(id)).one[ServiceTest]

    def queryBySuite(suiteId: String) = test.find(BSONDocument("suiteId" -> suiteId)).cursor[ServiceTest].collect[List]()

    def all(): Future[List[ServiceTest]] = test.find(BSONDocument()).cursor[ServiceTest].collect[List]()
}
