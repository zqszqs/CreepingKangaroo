package utils.compatible.json.transform

import play.api.libs.json._
import play.api.libs.json.JsString
import play.api.libs.json.JsBoolean
import play.api.libs.json.JsNumber
import utils.compatible.json.{Error, Result, Warning}
import utils.compatible.json
import utils.compatible.json.transform.JsonValueType._


/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 26 2014
 */
trait JsonValidator {
    def validate(path: String, target: JsValue): (JsonOutputValue, List[Result])

    def _result(result: Boolean, value: JsValue, message: String) =
        if (!result)
            (JsonOutputField(JsonOutputValue.get(value), List(("ERROR", message))), Nil)
        else (JsonOutputValue.get(value), Nil)

    def _convertMsg(value: JsValue): String = value match {
        case JsNumber(num) => JSON_NUMBER
        case JsString(str) => JSON_STRING
        case JsBoolean(bool) => JSON_BOOLEAN
        case JsArray(arr) => JSON_ARRAY
        case JsObject(fields) => JSON_OBJECT
        case JsNull => JSON_NULL
    }
}

object JsonValidator {
    def maps(validator: String, arg: String): JsonValidator = validator match {
        case "value" => ValueValidator(arg)
        case "type" => TypeValidator(arg)
        case "optional" => OptionalValidator(arg)
        case _ => EmptyValidator
    }
}


case class ValueValidator(value: String) extends JsonValidator {
    def validate(path: String, target: JsValue) = target match {
        case s: JsString =>  _result(s.value.equals(value), s, "expect[" + value + "]")
        case n: JsNumber =>  _result(n.value.toString().equals(value), n, "expect[" + value + "]")
        case b: JsBoolean =>  _result(b.value.toString.equals(value), b, "expect[" + value + "]")
        case JsNull => _result(value.equals("NULL"), JsNull, "expect[" + value + "]")
    }
}

case class TypeValidator(t: String) extends JsonValidator {
    def validate(path: String, target: JsValue) = t match {
        case JSON_STRING => _result(target.isInstanceOf[JsString], target, "expect[" + t + "]")
        case JSON_NUMBER => _result(target.isInstanceOf[JsNumber], target, "expect[" + t + "]")
        case JSON_BOOLEAN => _result(target.isInstanceOf[JsBoolean], target, "expect [" + t + "]")
        case JSON_NULL => _result(target.isInstanceOf[JsNull.type ], target, "expect [" + t + "]")
    }
}

object EmptyValidator extends JsonValidator {
    def validate(path: String, target: JsValue) = (JsonOutputValue.get(target), Nil)
}

case class OptionalValidator(arg: String) extends JsonValidator {
    def validate(path: String, target: JsValue) =
        if (arg.equals("true"))
            (JsonOutputField(JsonOutputValue.get(target), List(("WARNING", "field missing"))), Nil)
        else
            (JsonOutputField(JsonOutputValue.get(target), List(("ERROR", "field missing"))), Nil)
}