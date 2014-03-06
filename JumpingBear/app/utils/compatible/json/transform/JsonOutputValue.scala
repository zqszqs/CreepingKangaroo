package utils.compatible.json.transform

import play.api.libs.json._
import utils.compatible.json.transform.JsonValueType._
import utils.compatible.json.transform.JsonOutputField
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import play.api.libs.json.JsArray

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 27 2014
 */
trait JsonOutputValue {
    def asJsValue: JsValue
}

object JsonOutputValue {
    def get(value: String, typ: String) = JsonOutputField(JsonOutputEmptyField, List(("value", value), ("type", typ)))

    def get(js: JsValue) = {
        def _outputValue(value: String, typ: String) = JsonOutputField(JsonOutputEmptyField, List(("value", value), ("type", typ)))

        js match {
            case JsString(str) => _outputValue(str, JSON_STRING)
            case JsNumber(num) => _outputValue(num.toString(), JSON_NUMBER)
            case JsBoolean(bool) => _outputValue(bool.toString, JSON_BOOLEAN)
            case JsNull => _outputValue("NULL", JSON_NULL)
            case JsArray(arr) => _outputValue("ARRAY", JSON_ARRAY)
            case JsObject(fields) => _outputValue("OBJECT", JSON_OBJECT)
        }
    }
}

case class JsonOutputObject(fields: List[(String, JsonOutputValue)]) extends JsonOutputValue{
    def asJsValue = JsObject(fields.map(p => (p._1, p._2.asJsValue)))
}

case class JsonOutputArray(elements: List[JsonOutputValue]) extends JsonOutputValue{
    def asJsValue = JsArray(elements.map(_.asJsValue))
}

case class JsonOutputField(json: JsonOutputValue, extra: List[(String, String)]) extends JsonOutputValue {
    def asJsValue = {
        val extraString = extra.map(p => p._1 + "=" + p._2).mkString("&&")
        json match {
            case JsonOutputEmptyField => JsString(extraString)
            case js: JsonOutputField => JsString(js.asJsValue.as[JsString].value + "&&" + extraString)
        }
    }
}

object JsonOutputEmptyField extends JsonOutputValue {
    def asJsValue = JsString("")
}