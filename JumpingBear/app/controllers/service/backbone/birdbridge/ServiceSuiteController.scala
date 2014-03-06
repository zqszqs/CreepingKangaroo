package controllers.service.backbone.birdbridge

import play.api.libs.json._
import play.api.libs.json.JsArray
import play.api.libs.functional.syntax._
import play.api.mvc.{Controller, Action}
import play.api.mvc.BodyParsers.parse
import reactivemongo.bson.BSONObjectID
import scala.concurrent.Future
import utils.db.MongoJsonUtils._
import play.api.libs.json.JsArray
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import scala.Some
import controllers.service.backbone.model.SuiteOperate
import play.api.libs.concurrent.Execution.Implicits._
import play.modules.reactivemongo.MongoController
import controllers.service.backbone.modelobj.ServiceSuite

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 06 2014
 */
object ServiceSuiteController extends Controller with MongoController {
    val suiteCreate = (__ \ 'name).read[String]

    val suiteUpdate = (__ \ 'name).readNullable[String]

    def _json(serSuite: ServiceSuite) = Json.obj(
        "id" -> serSuite.id.stringify,
        "name" -> serSuite.name
    )
    
    def create = Action.async(parse.json) {
        implicit request => {
            request.body.validate(suiteCreate).map {
                case (name) => {
                    SuiteOperate.createNew(name) match {
                        case (result, doc) => {
                            result.map(
                                re => if (re.ok) Created(_json(doc))
                                else UnprocessableEntity("entity is not processible:" + re)
                            )
                        }
                    }
                }
            }.recoverTotal(e => Future(BadRequest(JsError.toFlatJson(e))))
        }
    }

    def update(id: String) = Action.async(parse.json) {
        implicit request => {
            request.body.validate(suiteUpdate).map {
                case (name) => {
                    SuiteOperate.update(id, name).map {
                        result => if (result.ok) NoContent
                        else BadRequest("Error when update document [%s]: %s".format(id, result))
                    }
                }
            }.recoverTotal(e => Future(BadRequest(JsError.toFlatJson(e))))
        }
    }

    def delete(id: String) = Action.async(parse.empty) {
        implicit request => {
            SuiteOperate.delete(id).map(
                result => if (result.ok) NoContent
                else BadRequest("Error when delete run document [%s]: %s".format(id, result))
            )
        }
    }

    def get(id: String) = Action.async {
        if (id equals "0")
            SuiteOperate.all().map {
                doc => Ok(JsArray(doc.map(_json)))
            }
        else
            SuiteOperate.query(id).map {
                doc => Ok(doc.map(_json).getOrElse(Json.obj()))
            }.recoverWith {
                case e: Throwable => Future(BadRequest(e.toString))
            }
    }
}
