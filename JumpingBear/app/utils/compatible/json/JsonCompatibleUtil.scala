package utils.compatible.json

import utils.compatible.json.transform._
import play.api.libs.json._
import utils.compatible.json.transform.JsonField
import utils.compatible.json.transform.JsonObject
import utils.compatible.json.transform.JsonArray
import utils.compatible.json.transform.JsonValueType._
import play.api.libs.json.JsString
import play.api.libs.json.JsArray
import play.api.libs.json.JsBoolean
import play.api.libs.json.JsNumber
import play.Logger

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 21 2014
 */
object JsonCompatibleUtil {
    def compare(template: String, target: String): (String, List[Result]) = {
        try {
            val temp = JsonValue.from("root", Json.parse(template))
            val tar = Json.parse(target)
            val re = compare(temp, tar)
            (Json.stringify(re._1.asJsValue), re._2)
        } catch {
            case e: Exception => {
                Logger.debug("T", e)
                (target, Nil)
            }
        }
    }

    def compare(template: JsonValue, target: JsValue): (JsonOutputValue, List[Result]) = (template, target) match {
        case (JsonArray(path, tempElements, validates, functions), JsArray(tarElements)) => _compareArray(path, tempElements, validates, functions, tarElements.toList)
        case (JsonObject(path, tempFields, validates, functions), JsObject(tarFields)) => _compareObject(path, tempFields, validates, functions, tarFields.toList)
        case (JsonField(path, validates, functions), js: JsString) => _compareField(path, validates, functions, js)
        case (JsonField(path, validates, functions), jn: JsNumber) => _compareField(path, validates, functions, jn)
        case (JsonField(path, validates, functions), jb: JsBoolean) => _compareField(path, validates, functions, jb)
        case (JsonField(path, validates, functions), JsNull) => _compareField(path, validates, functions, JsNull)
        case (JsonArray(path, tempElements, validates, functions), _) => (JsonOutputField(JsonOutputEmptyField, List(("ERROR", "expect[Json Array]"))), Nil)
        case (JsonObject(path, tempFields, validates, functions), _) => (JsonOutputField(JsonOutputEmptyField, List(("ERROR", "expect[Json Object]"))), Nil)
        case (JsonField(path, validates, functions), _) => (JsonOutputField(JsonOutputEmptyField, List(("ERROR", "expect[Json Field]"))), Nil)
        case _ => throw new Exception("Structure error: expect [" + template.getClass + "] but found [" + _convertMsg(target) + "]")
    }

    def _convertMsg(value: JsValue): String = value match {
        case JsNumber(num) => JSON_NUMBER
        case JsString(str) => JSON_STRING
        case JsBoolean(bool) => JSON_BOOLEAN
        case JsArray(arr) => JSON_ARRAY
        case JsObject(fields) => JSON_OBJECT
        case JsNull => JSON_NULL
    }

    def _compareArray(path: String, tempElements: List[JsonValue], validates: List[JsonValidator], functions: List[JsonFunction], tarElements: List[JsValue]) = {
        val tempNode = if (tempElements.isEmpty) JsonObject("", Nil, Nil, Nil) else tempElements.head

        tarElements.map(e => compare(tempNode, e)).foldLeft((JsonOutputArray(List()), List.empty[Result]))(
            (result, current) => (JsonOutputArray(current._1 :: result._1.elements), result._2 ++ current._2)
        )
    }

    def _compareObject(path: String, tempFields: List[(String, JsonValue)], validates: List[JsonValidator], functions: List[JsonFunction], tarFields: List[(String, JsValue)]): (JsonOutputObject, List[Result]) = {
        if (tempFields.isEmpty && tarFields.isEmpty) {
            (JsonOutputObject(List()), List.empty[Result])
        }
        else if (tempFields.isEmpty) {
            val fieldsWithError = tarFields.map(f => (f._1, JsonOutputField(JsonOutputValue.get(f._2), List(("ERROR", "Additional Field")))))
            Logger.debug(fieldsWithError.toString())
            (JsonOutputObject(fieldsWithError), List.empty[Result])
        }
        else if (tarFields.isEmpty) {
            val fieldsWithError = tempFields.map(f => (f._1, JsonOutputField(JsonOutputEmptyField, List(("ERROR", "Field missing")))))
            (JsonOutputObject(fieldsWithError), List.empty[Result])
        }
        else {
            val tempHead = tempFields.head
            val (tarHeadOpt, tarFieldsRemain) = tarFields.partition(p => p._1.equals(tempHead._1))

            if (tarHeadOpt.isEmpty) {
                val result = tempHead._2.validations.find(_.isInstanceOf[OptionalValidator]).getOrElse(OptionalValidator(arg = "false")).validate(path, JsNull)
                val subResult = _compareObject(path, tempFields.tail, validates, functions, tarFieldsRemain)
                (JsonOutputObject((tempHead._1, result._1) :: subResult._1.fields), result._2 ++ subResult._2)
            }
            else {
                val result = compare(tempHead._2, tarHeadOpt(0)._2)
                val subResult = _compareObject(path, tempFields.tail, validates, functions, tarFieldsRemain)
                (JsonOutputObject((tempHead._1, result._1) :: subResult._1.fields), result._2 ++ subResult._2)
            }
        }
    }

    def _compareField(path: String, validates: List[JsonValidator], functions: List[JsonFunction], v: JsValue) = {

        def _combine(left: JsonOutputField, right: JsonOutputField) = JsonOutputField(left.json, (left.extra ++ right.extra).distinct)

        val validateResult = validates.foldLeft((JsonOutputValue.get(v), List.empty[Result]))(
            (result, current) => {
                val re = current.validate(path, v)
                (_combine(result._1, re._1.asInstanceOf[JsonOutputField]), result._2 ++ re._2)
            }
        )

        functions.foldLeft(validateResult)(
            (result, current) => {
                val re = current.invoke(path, v)
                (_combine(result._1, re._1.asInstanceOf[JsonOutputField]), result._2 ++ re._2)
            }
        )
    }
}