package utils.compatible.json.transform

import play.api.libs.json._
import JsonValueType._
import play.api.libs.json.JsString
import play.api.libs.json.JsBoolean
import play.api.libs.json.JsNumber


/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 21 2014
 */
case class JsonFieldBuilder(currentPath: String, value: JsValue) {

    def _parseField(src: String): JsonField = {
        val list = src.replace("{%", "").replace("%}", "").split("&&").toList.map(
            s => {
                val sArr = s.split("=", 2)
                (sArr(0), sArr(1))
            }
        )
        JsonField(currentPath, list.map(l => JsonValidator.maps(l._1, l._2)), list.map(l => JsonFunction.maps(l._1, l._2)))
    }

    def asField = value match {
        case JsString(str) => {
            if (str.matches("\\{%.*%\\}"))
                _parseField(str)
            else
                JsonField(currentPath, List(TypeValidator(JSON_STRING), ValueValidator(str)), Nil)

        }
        case JsNumber(num) => JsonField(currentPath, List(TypeValidator(JSON_NUMBER), ValueValidator(num.toString())), Nil)
        case JsBoolean(bool) => JsonField(currentPath, List(TypeValidator(JSON_BOOLEAN), ValueValidator(bool.toString)), Nil)
        case JsNull => JsonField(currentPath, List(TypeValidator(JSON_NULL), ValueValidator("NULL")), Nil)
    }
}

