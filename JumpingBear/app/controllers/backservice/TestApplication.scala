package controllers.backservice

import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.json._
import play.api.libs.functional.syntax._
import reactivemongo.bson.{BSONObjectID, BSONDocument}
import play.api.libs.concurrent.Execution.Implicits._
import utils.db.MongoJsonUtils._
import scala.concurrent.Future

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Oct. 10 2013
 */
object TestApplication extends Controller with MongoController {

    def test = db.collection[JSONCollection]("test")

    val testCreate = (
        (__ \ 'name).read[String] and
            (__ \ 'suiteId).read[String] and
            (__ \ 'status).read[String] and
            (__ \ 'startTime).read[String] and
            (__ \ 'endTime).readNullable[String] and
            (__ \ 'description).readNullable[String] and
            (__ \ 'groups \ 'include).readNullable[JsArray] and
            (__ \ 'groups \ 'exclude).readNullable[JsArray] and
            (__ \ 'configuration).read[JsObject] and
            (__ \ 'parameter).read[JsObject]
        ).tupled

    val testUpdate = (
        (__ \ 'name).readNullable[String] and
            (__ \ 'suiteId).readNullable[String] and
            (__ \ 'status).readNullable[String] and
            (__ \ 'startTime).readNullable[String] and
            (__ \ 'endTime).readNullable[String] and
            (__ \ 'description).readNullable[String] and
            (__ \ 'groups \ 'include).readNullable[JsArray].orElse((__ \ 'none).readNullable[JsArray]) and
            (__ \ 'groups \ 'exclude).readNullable[JsArray].orElse((__ \ 'none).readNullable[JsArray]) and
            (__ \ 'configuration).readNullable[JsObject] and
            (__ \ 'parameter).readNullable[JsObject]
        ).tupled

    def create = Action.async(parse.json) {
        implicit request => request.body.validate(testCreate).map {
            case (name, suiteId, status, startTime, endTime, description, groupsInclude, groupsExclude,
            configuration, parameter) => {
                val id = BSONObjectID.generate.stringify

                val groups = buildJsonFromObjList(
                    "include" -> groupsInclude,
                    "exclude" -> groupsExclude
                )

                val partial = Json.obj(
                    "_id" -> Json.obj("$oid" -> id),
                    "name" -> name,
                    "suiteId" -> suiteId,
                    "status" -> status,
                    "startTime" -> startTime,
                    "endTime" -> endTime,
                    "description" -> description,
                    "configuration" -> configuration,
                    "parameter" -> parameter
                )

                test.insert(
                    if (groups.keys.size > 0) partial ++ Json.obj("groups" -> groups) else partial
                ).map(
                    lastError => if (lastError.ok) Created(Json.obj("id" -> id))
                    else UnprocessableEntity("entity is not processible" + lastError)
                )
            }
        }.recoverTotal(e => Future(BadRequest(JsError.toFlatJson(e))))
    }

    def update(id: String) = Action.async(parse.json) {
        implicit request => request.body.validate(testUpdate).map {
            case (name, suiteId, status, startTime, endTime, description, groupsInclude, groupsExclude,
            configuration, parameter) => {

                val groups = buildJsonFromObjList(
                    "include" -> groupsInclude,
                    "exclude" -> groupsExclude
                )

                val partialModifier = buildJsonFromList(
                    "name" -> name,
                    "suiteId" -> suiteId,
                    "status" -> status,
                    "startTime" -> startTime,
                    "endTime" -> endTime,
                    "description" -> description,
                    "configuration" -> configuration.map(_.toString()),
                    "parameter" -> parameter.map(_.toString())
                )

                val modifier = if (groups.keys.size > 0) partialModifier ++ Json.obj("groups" -> groups) else partialModifier
                test.update(
                    jsonObjectId(id),
                    Json.obj("$set" -> modifier)
                ).map(
                    lastError => if (lastError.ok) NoContent
                    else BadRequest("Error when update document [%s]: %s".format(id, lastError))
                )
            }
        }.recoverTotal(e => Future(BadRequest(JsError.toFlatJson(e))))
    }

    def get(id: String) = Action.async {
        test.find(
            jsonObjectId(id)
        ).one[JsObject].map {
            doc => Ok(Json.stringify(doc.getOrElse(Json.obj())))
        }.recoverWith {
            case e: Throwable => Future(BadRequest(e.toString))
        }
    }

    def delete(id: String) = Action.async(parse.empty) {
        implicit request => {
            test.remove(
                jsonObjectId(id)
            ).map(
                lastError => if (lastError.ok) NoContent
                else BadRequest("Error when delete run document [%s]: %s".format(id, lastError))
            )
        }
    }
}


