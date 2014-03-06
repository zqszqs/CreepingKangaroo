package utils.db

import play.api.libs.json.{JsValue, JsNull, Json, JsObject}
import play.api.libs.json.Json.JsValueWrapper

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Oct. 10 2013
 */
object MongoJsonUtils {
    def jsonObjectId(id: String): JsObject = Json.obj("_id" -> Json.obj("$oid" -> id))

    def buildJsonFromList(fields: (String, Option[String])*): JsObject = {

        def addToJson(json: JsObject, ele: (String, Option[String])): JsObject = {
            json ++ Json.obj(ele._1 -> ele._2.get)
        }

        fields.filter(pair => pair._2.isDefined)
              .foldLeft(Json.obj())(addToJson)
    }

    def buildJsonFromObjList(fields: (String, Option[JsValue])*): JsObject = {

        def addToJson(json: JsObject, ele: (String, Option[JsValue])): JsObject = {
            json ++ Json.obj(ele._1 -> ele._2.get)
        }

        fields.filter(pair => pair._2.isDefined)
            .foldLeft(Json.obj())(addToJson)
    }
}
