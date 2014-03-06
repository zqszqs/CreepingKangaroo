package controllers.backservice

import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.MongoController
import play.api.libs.json._
import reactivemongo.bson.BSONObjectID
import utils.db.MongoJsonUtils._
import play.api.libs.functional.syntax._
import play.api.libs.json.JsArray
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.json.JsObject
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.Future

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Dec. 11 2013
 */
object TestCaseApplication extends Controller with MongoController {
    def testcase = db.collection[JSONCollection]("testcase")

    val testCaseCreate = (
        (__ \ 'name).read[String] and
            (__ \ 'testId).read[String] and
            (__ \ 'status).read[String] and
            (__ \ 'startTime).read[String] and
            (__ \ 'method).read[String] and
            (__ \ 'arguments).read[JsObject] and
            (__ \ 'groups).readNullable[JsArray] and
            (__ \ 'endTime).readNullable[String] and
            (__ \ 'description).readNullable[String] and
            (__ \ 'stackTrace).readNullable[String] and
            (__ \ 'methodSpec).readNullable[JsObject]
        ).tupled

    val testCaseUpdate = (
        (__ \ 'name).readNullable[String] and
            (__ \ 'testId).readNullable[String] and
            (__ \ 'status).readNullable[String] and
            (__ \ 'startTime).readNullable[String] and
            (__ \ 'method).readNullable[String] and
            (__ \ 'arguments).readNullable[JsObject] and
            (__ \ 'groups).readNullable[JsArray] and
            (__ \ 'endTime).readNullable[String] and
            (__ \ 'description).readNullable[String] and
            (__ \ 'stackTrace).readNullable[String] and
            (__ \ 'methodSpec).readNullable[JsObject]
        ).tupled

    def create = Action.async(parse.json) {
        implicit request => request.body.validate(testCaseCreate).map {
            case (name, testId, status, startTime, method, arguments, groups, endTime, description, stackTrace, methodSpec) => {
                val id = BSONObjectID.generate.stringify
                testcase.insert(
                    Json.obj(
                        "_id" -> Json.obj("$oid" -> id),
                        "name" -> name,
                        "testId" -> testId,
                        "status" -> status,
                        "startTime" -> startTime,
                        "endTime" -> endTime,
                        "description" -> description,
                        "method" -> method,
                        "arguments" -> arguments,
                        "groups" -> groups,
                        "stackTrace" -> stackTrace,
                        "methodSpec" -> methodSpec
                    )
                ).map(
                    lastError => if (lastError.ok) Created(Json.obj("id" -> id))
                    else UnprocessableEntity("entity is not processible" + lastError)
                )
            }
        }.recoverTotal(e => Future(BadRequest(JsError.toFlatJson(e))))
    }

    def update(id: String) = Action.async(parse.json) {
        implicit request => request.body.validate(testCaseUpdate).map {
            case (name, testId, status, startTime, method, arguments, groups, endTime, description, stackTrace, methodSpec) => {
                val partialModifier = buildJsonFromList(
                    "name" -> name,
                    "testId" -> testId,
                    "status" -> status,
                    "startTime" -> startTime,
                    "endTime" -> endTime,
                    "description" -> description,
                    "method" -> method,
                    "stackTrace" -> stackTrace
                )

                val modifier = partialModifier ++ Json.obj("arguments" -> arguments) ++ Json.obj("groups" -> groups) ++ Json.obj("methodSpec" -> methodSpec)

                testcase.update(
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
        testcase.find(
            jsonObjectId(id)
        ).one[JsObject].map {
            doc => Ok(Json.stringify(doc.getOrElse(Json.obj())))
        }.recoverWith {
            case e: Throwable => Future(BadRequest(e.toString))
        }
    }

    def delete(id: String) = Action.async(parse.empty) {
        implicit request => {
            testcase.remove(
                jsonObjectId(id)
            ).map(
                lastError => if (lastError.ok) NoContent
                else BadRequest("Error when delete run document [%s]: %s".format(id, lastError))
            )
        }
    }

}
