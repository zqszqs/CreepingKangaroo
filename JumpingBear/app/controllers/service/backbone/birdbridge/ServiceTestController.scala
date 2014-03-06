package controllers.service.backbone.birdbridge

import play.api.mvc.{Action, Controller}
import play.api.libs.json._
import play.api.libs.functional.syntax._
import reactivemongo.bson.BSONObjectID
import scala.concurrent.Future
import utils.db.MongoJsonUtils._
import play.api.libs.json.JsArray
import play.api.libs.json.JsObject
import controllers.service.backbone.model.TestOperate
import play.api.libs.concurrent.Execution.Implicits._
import play.modules.reactivemongo.MongoController
import controllers.service.backbone.modelobj.ServiceTest

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 06 2014
 */
object ServiceTestController extends Controller with MongoController {
    val testCreate = (
        (__ \ 'name).read[String] and
            (__ \ 'suiteId).read[String] and
            (__ \ 'forAPI).read[String]
        ).tupled

    val testUpdate = {
        (__ \ 'name).readNullable[String] and
            (__ \ 'suiteId).readNullable[String] and
            (__ \ 'forAPI).readNullable[String]
    }.tupled

    def _json(serTest: ServiceTest): JsObject = Json.obj(
        "id" -> serTest.id.stringify,
        "name" -> serTest.name,
        "suiteId" -> serTest.suiteId,
        "forAPI" -> serTest.forAPI
    )

    def create = Action.async(parse.json) {
        implicit request => {
            request.body.validate(testCreate).map {
                case (name, suiteId, forAPIId) => {
                    TestOperate.createNew(name, suiteId, forAPIId) match {
                        case (result, doc) => {
                            result.map(re => if (re.ok) Created(_json(doc))
                            else UnprocessableEntity("entity is not processible:" + result)
                            )
                        }
                    }
                }
            }
        }.recoverTotal(e => Future(BadRequest(JsError.toFlatJson(e))))
    }

    def update(id: String) = Action.async(parse.json) {
        implicit request => {
            request.body.validate(testUpdate).map {
                case (name, suiteId, forAPIId) => {
                    TestOperate.update(id, suiteId, name, forAPIId).map(
                        result => if (result.ok) NoContent
                        else BadRequest("Error when update document [%s]: %s".format(id, result))
                    )
                }
            }.recoverTotal(e => Future(BadRequest(JsError.toFlatJson(e))))
        }
    }


    def delete(id: String) = Action.async(parse.empty) {
        implicit request => {
            TestOperate.delete(id).map(
                result => if (result.ok) NoContent
                else BadRequest("Error when delete run document [%s]: %s".format(id, result))
            )
        }
    }

    def get(id: String) = Action.async {
        if (id equals "0")
            TestOperate.all().map {
                doc => Ok(JsArray(doc.map(_json)))
            }
        else
            TestOperate.query(id).map {
                doc => Ok(doc.map(_json).getOrElse(Json.obj()))
            }.recoverWith {
                case e: Throwable => Future(BadRequest(e.toString))
            }
    }

    def getBySuite(id: String) = Action.async {
        TestOperate.queryBySuite(id).map {
            doc => Ok(JsArray(doc.map(_json)))
        }
    }
}
