package controllers.service.backbone.modelobj

import play.api.libs.json.Json

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 20 2014
 */
case class FunctionError(status: String, message: String) {
    override def toString = Json.stringify(asJson)

    def asJson = Json.obj("status" -> status, "message" -> message)
}
