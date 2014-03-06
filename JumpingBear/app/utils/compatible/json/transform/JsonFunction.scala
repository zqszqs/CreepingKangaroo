package utils.compatible.json.transform

import play.api.libs.json._
import utils.compatible.json.{Result, Error, Warning}
import utils.compatible.json.Error
import utils.compatible.json.Result
import play.api.libs.json.JsString
import play.api.libs.json.JsBoolean
import utils.compatible.json.transform.GetFunction
import utils.compatible.json.Warning
import play.api.libs.json.JsNumber

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 26 2014
 */
trait JsonFunction {
    def invoke(path: String, arg: JsValue): (JsonOutputValue, List[Result])

    def _result(arg: JsValue, key: String, value: AnyRef) = (JsonOutputValue.get(arg), List(Result(key, value)))
}


object JsonFunction {
    def maps(validator: String, arg: String): JsonFunction = validator match {
        case "get" => GetFunction(arg)
        case _ => EmptyFunction
    }
}

object EmptyFunction extends JsonFunction {
    def invoke(path: String, arg: JsValue) = (JsonOutputValue.get(arg), Nil)
}

case class GetFunction(key: String) extends JsonFunction {
    def invoke(path:String, arg: JsValue) = arg match {
        case JsString(str) => _result(arg, key, str)
        case JsNumber(num) => _result(arg, key, num.toString())
        case JsBoolean(bool) => _result(arg, key, bool.toString)
        case JsNull => _result(arg, "ERROR", "Cannot get value from NULL")
    }

}
