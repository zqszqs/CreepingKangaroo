package utils.db

import play.api.libs.json.{JsValue, JsString, Json, JsObject}

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 03 2014
 */
object JsonUtils {
    def JObject(fields: (String, String)*): JsObject =
        fields.foldLeft[JsObject](Json.obj())((js, next) => js + (next._1, JsString(next._2)))

    def JObject(fields: List[(String, String)]): JsObject =
        fields.foldLeft[JsObject](Json.obj())((js, next) => js + (next._1, JsString(next._2)))

    def JosObject(fields: (String, Option[String])*) = fields.foldLeft[JsObject](Json.obj())(
        (js, next) =>
            if (next._2.isEmpty) js
            else js + (next._1, JsString(next._2.get))
    )

    def JvObject(fields: (String, JsValue)*): JsObject =
        fields.foldLeft[JsObject](Json.obj())((js, next) => js + next)

    def JArray(list: List[JsValue]) = Json.arr(list)

    def JObject(list: Option[List[(String, String)]]): JsObject =
        if (list.isDefined)
            JObject(list.get)
        else Json.obj()


    def refer(id: String, name: String) = JObject("id" -> id, "name" -> name)
    def refer(id: Option[String], name: Option[String]) = JosObject("id" -> id, "name" -> name)
}
