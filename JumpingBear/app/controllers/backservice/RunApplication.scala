package controllers.backservice

import _root_.utils.db.MongoJsonUtils._
import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.MongoController
import play.api.libs.json._
import play.modules.reactivemongo.json.collection.{JSONDocumentReaderAsBufferReader, JSONCollection}
import play.api.libs.concurrent.Execution.Implicits._
import reactivemongo.bson._
import play.modules.reactivemongo.json.BSONFormats.BSONDocumentFormat
import play.api.libs.concurrent.Akka
import reactivemongo.api.collections.default.BSONDocumentReaderAsBufferReader
import play.modules.reactivemongo.json.collection.JSONCollection
import play.libs.F.Tuple
import play.api.libs.functional.syntax._
import scala.concurrent.Future

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Oct. 08 2013
 */
object RunApplication extends Controller with MongoController {

    def run: JSONCollection = db.collection[JSONCollection]("run")

    val runCreate = (
        (__ \ 'name).read[String] and
            (__ \ 'status).read[String] and
            (__ \ 'host).read[String] and
            (__ \ 'build).read[String] and
            (__ \ 'startTime).read[String] and
            (__ \ 'endTime).readNullable[String] and
            (__ \ 'description).readNullable[String] and
            (__ \ 'reportURL).readNullable[String]
        ).tupled

    val runUpdate = {
        (__ \ 'name).readNullable[String] and
            (__ \ 'status).readNullable[String] and
            (__ \ 'host).readNullable[String] and
            (__ \ 'build).readNullable[String] and
            (__ \ 'startTime).readNullable[String] and
            (__ \ 'endTime).readNullable[String] and
            (__ \ 'description).readNullable[String] and
            (__ \ 'reportURL).readNullable[String]
    }.tupled

    def create = Action.async(parse.json) {
        implicit request => {
            request.body.validate(runCreate).map {
                case (name, status, host, build, startTime, endTime, description, reportURL) => {
                    val id = BSONObjectID.generate.stringify
                    run.insert(
                        Json.obj(
                            "_id" -> Json.obj("$oid" -> id),
                            "name" -> name,
                            "status" -> status,
                            "host" -> host,
                            "build" -> build,
                            "startTime" -> startTime,
                            "endTime" -> endTime,
                            "description" -> description,
                            "reportURL" -> reportURL
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
            request.body.validate(runUpdate).map {
                case (name, status, host, build, startTime, endTime, description, reportURL) => {
                    val modifier = buildJsonFromList(
                        "name" -> name,
                        "status" -> status,
                        "host" -> host,
                        "build" -> build,
                        "startTime" -> startTime,
                        "endTime" -> endTime,
                        "description" -> description,
                        "reportURL" -> reportURL
                    )

                    run.update(
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
        run.find(
            jsonObjectId(id)
        ).one[JsObject].map {
            doc => Ok(Json.stringify(doc.getOrElse(Json.obj())))
        }.recoverWith {
            case e: Throwable => Future(BadRequest(e.toString))
        }
    }

    def delete(id: String) = Action.async(parse.empty) {
        implicit request => {
            run.remove(
                jsonObjectId(id)
            ).map(
                lastError => if (lastError.ok) NoContent
                else BadRequest("Error when delete run document [%s]: %s".format(id, lastError))
            )
        }
    }
}
