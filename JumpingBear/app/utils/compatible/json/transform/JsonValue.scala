package utils.compatible.json.transform

import play.api.libs.json._
import play.api.libs.json.JsObject
import play.api.libs.json.JsArray

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 21 2014
 */
abstract class JsonValue {
    def path: String

    def validations: List[JsonValidator]

    def functions: List[JsonFunction]
}

object JsonValue {
    def from(currentPath: String, json: JsValue): JsonValue = json match {
        case JsObject(fields) => JsonObject(
            currentPath,
            fields.map(f => (f._1, from(currentPath + "." + f._1, f._2))).toList,
            Nil,
            Nil
        )
        case JsArray(elements) => JsonArray(currentPath, elements.map(e => from(currentPath, e)).toList, Nil, Nil)
        case v: JsValue => JsonFieldBuilder(currentPath, v).asField
    }
}

case class JsonField(path:String, validations: List[JsonValidator], functions: List[JsonFunction]) extends JsonValue

case class JsonObject(path:String, fields: List[(String, JsonValue)], validations: List[JsonValidator],
                      functions: List[JsonFunction]) extends JsonValue

case class JsonArray(path: String, elements: List[JsonValue], validations: List[JsonValidator],
                     functions: List[JsonFunction]) extends JsonValue
