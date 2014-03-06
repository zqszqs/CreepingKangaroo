package controllers.backservice

import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.MongoController
import play.api.libs.json._
import play.api.libs.functional.syntax._
import reactivemongo.bson.BSONObjectID
import utils.db.MongoJsonUtils._
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.Future

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Oct. 10 2013
 */
object SuiteApplication extends Controller with MongoController {

    def suite = db.collection[JSONCollection]("suite")

    val suiteCreate = (
        (__ \ 'name).read[String] and
            (__ \ 'status).read[String] and
            (__ \ 'startTime).read[String] and
            (__ \ 'endTime).readNullable[String] and
            (__ \ 'description).readNullable[String] and
            (__ \ 'runId).read[String]
        ).tupled

    val suiteUpdate = {
        (__ \ 'name).readNullable[String] and
            (__ \ 'status).readNullable[String] and
            (__ \ 'startTime).readNullable[String] and
            (__ \ 'endTime).readNullable[String] and
            (__ \ 'description).readNullable[String] and
            (__ \ 'runId).readNullable[String]
    }.tupled

    def create = Action.async(parse.json) {
        implicit request => {
            request.body.validate(suiteCreate).map {
                case (name, status, startTime, endTime, description, runId) => {
                    val id = BSONObjectID.generate.stringify
                    suite.insert(
                        Json.obj(
                            "_id" -> Json.obj("$oid" -> id),
                            "name" -> name,
                            "status" -> status,
                            "startTime" -> startTime,
                            "endTime" -> endTime,
                            "description" -> description,
                            "runId" -> runId
                        )
                    ).map(
                        lastError => if (lastError.ok) Created(Json.obj("id" -> id))
                        else UnprocessableEntity("entity is not processible" + lastError)
                    )
                }
            }.recoverTotal(e => Future(BadRequest(JsError.toFlatJson(e))))
        }
    }

    def update(id: String) = Action.async(parse.json) {
        implicit request => {
            request.body.validate(suiteUpdate).map {
                case (name, status, startTime, endTime, description, runId) => {
                    val modifier = buildJsonFromList(
                        "name" -> name,
                        "status" -> status,
                        "startTime" -> startTime,
                        "endTime" -> endTime,
                        "description" -> description,
                        "runId" -> runId
                    )

                    suite.update(
                        jsonObjectId(id),
                        Json.obj("$set" -> modifier)
                    ).map(
                        lastError => if (lastError.ok) NoContent
                        else BadRequest("Error when update document [%s]: %s".format(id, lastError))
                    )
                }
            }.recoverTotal(e => Future(BadRequest(JsError.toFlatJson(e))))
        }
    }

    def get(id: String) = Action.async {
        suite.find(
            jsonObjectId(id)
        ).one[JsObject].map {
            doc => Ok(Json.stringify(doc.getOrElse(Json.obj())))
        }.recoverWith {
            case e: Throwable => Future(BadRequest(e.toString))
        }
    }

    def delete(id: String) = Action.async(parse.empty) {
        implicit request => {
            suite.remove(
                jsonObjectId(id)
            ).map(
                lastError => if (lastError.ok) NoContent
                else BadRequest("Error when delete run document [%s]: %s".format(id, lastError))
            )
        }
    }

}
