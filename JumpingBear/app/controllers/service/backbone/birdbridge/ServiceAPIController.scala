package controllers.service.backbone.birdbridge

import play.api.mvc.{SimpleResult, Action, Controller}
import play.api.libs.json._
import play.api.libs.functional.syntax._
import controllers.service.backbone.model.APIOperate
import utils.db.MongoJsonUtils._
import play.api.libs.concurrent.Execution.Implicits._
import play.modules.reactivemongo.MongoController
import scala.util.{Success, Failure}
import controllers.service.backbone.modelobj.ServiceAPI
import reactivemongo.bson.BSONObjectID
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 06 2014
 */
object ServiceAPIController extends Controller with MongoController {

    val apisCreate = (
        (__ \ 'name).read[String] and
            (__ \ 'domain).read[String] and
            (__ \ 'endpoint).read[String]
        ).tupled

    val apisUpdate = {
        (__ \ 'name).readNullable[String] and
            (__ \ 'domain).readNullable[String] and
            (__ \ 'endpoint).readNullable[String]
    }.tupled

    def create = Action.async(parse.json) {
        implicit request => {
            request.body.validate(apisCreate).map {
                case (name, domain, endpoint) => {
                    APIOperate.createNew(name, domain, endpoint) match {
                        case (result, doc) =>
                            result.map(
                                re => if (re.ok) Created(_json(doc))
                                else UnprocessableEntity("entity is not processable:" + result)
                            )
                    }
                }
            }
        }.recoverTotal(e => Future(BadRequest(JsError.toFlatJson(e))))
    }

    def update(id: String) = Action.async(parse.json) {
        implicit request => {
            request.body.validate(apisUpdate).map {
                case (name, domain, endpoint) => {
                    APIOperate.update(id, name, domain, endpoint) map {
                        lastError => if (lastError.ok) NoContent
                        else BadRequest("Error when update document [%s]: %s".format(id, lastError))
                    }
                }
            }
        }.recoverTotal((e) => Future(BadRequest(JsError.toFlatJson(e))))
    }

    def delete(id: String) = Action.async(parse.empty) {
        implicit request => {
            APIOperate.delete(id).map(
                result => if (result.ok) NoContent
                else BadRequest("Error when delete document [%s]: %s".format(id, result))
            )
        }
    }

    def _json(api: ServiceAPI): JsObject = Json.obj(
        "id" -> api.id.stringify,
        "domain" -> api.domain,
        "endpoint" -> api.endpoint,
        "name" -> api.name
    )

    def _jsonPair(api: ServiceAPI): (String, JsValue) = (api.id.stringify, JsString(api.name))

    def get(id: String, action: Option[String]) = Action.async {
        def _toJson(doc: List[ServiceAPI]): JsValue = action match {
            case None => JsArray(doc.map(_json))
            case Some(act) => act match {
                case "brief" => JsArray(doc.map(_json))
                case "brief_map" =>  JsObject(doc.map(_jsonPair))
            }
        }

        if (id equals "0")
            APIOperate.all() map {
                doc => Ok(_toJson(doc))
            }
        else
            APIOperate.query(id).map {
                doc => Ok(doc.map(_json).getOrElse(Json.obj()))
            }.recoverWith {
                case e: Throwable => Future(BadRequest(e.toString))
            }
    }
}
