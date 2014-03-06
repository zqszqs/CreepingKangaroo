package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.functional.syntax._
import reactivemongo.api._
import play.modules.reactivemongo._
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.Play.current
import utils.compatible.json.transform.JsonValue
import utils.compatible.json.JsonCompatibleUtil

object Application extends Controller with MongoController {

    implicit val rds = (
        (__ \ 'name).read[String] and
            (__ \ 'age).readNullable[Long]
        ).tupled

    def sayHello = Action(parse.json) {
        request => rds.reads(request.body).map {
            case (name, age) => Ok("Hello " + name + ", you're " + age)
        }.recoverTotal(e => BadRequest("Detected error:" + JsError.toFlatJson(e)))
    }


    def collection: JSONCollection = db.collection[JSONCollection]("persons")

    def create = Action.async(parse.json) {
        request => {

            val json = request.body.validate(rds).map{
                case (name, age) => Json.obj(
                    "name" -> name,
                    "age" -> age
                )
            }
            collection.insert(json.get).map(lastError => Ok("Mongo LastError: %s".format(lastError)))
        }
    }

    def debug = Action(parse.json) {
        request => {
            val body = request.body.as[JsObject]
            val temp = body.value("temp").as[JsObject]
            val target = body.value("target").as[JsObject]

            val (errors, results) = JsonCompatibleUtil.compare(JsonValue.from("root", temp), target)

            Ok(Json.stringify(errors.asJsValue))
        }
    }
}