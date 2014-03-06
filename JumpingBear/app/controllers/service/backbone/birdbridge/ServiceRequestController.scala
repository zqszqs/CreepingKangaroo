package controllers.service.backbone.birdbridge

import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.MongoController
import controllers.service.backbone.model.{ExecutingOperate, TestOperate, RequestOperate}
import scala.concurrent.Future
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.concurrent.Execution.Implicits._
import controllers.service.backbone.modelobj.{RequestOutput, RequestInput, ServiceRequest}
import controllers.service.backbone.modelobj.RequestInput.inputReader
import controllers.service.backbone.modelobj.RequestInput.inputWriter
import controllers.service.backbone.modelobj.RequestOutput.outputReader
import controllers.service.backbone.modelobj.RequestOutput.outputWriter


/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 06 2014
 */
object ServiceRequestController extends Controller with MongoController {


    val requestCreate = (
        (__ \ 'name).read[String] and
            (__ \ 'testId).read[String] and
            (__ \ 'description).read[String] and
            (__ \ 'position).read[Int] and
            (__ \ 'input).read[RequestInput] and
            (__ \ 'output).read[RequestOutput]
        ).tupled

    val requestUpdate = {
        (__ \ 'name).readNullable[String] and
            (__ \ 'testId).readNullable[String] and
            (__ \ 'description).readNullable[String] and
            (__ \ 'position).readNullable[Int] and
            (__ \ 'input).readNullable[RequestInput] and
            (__ \ 'output).readNullable[RequestOutput]
    }.tupled

    def _json(serRequest: ServiceRequest) = Json.obj(
        "id" -> serRequest.id.stringify,
        "name" -> serRequest.name,
        "testId" -> serRequest.testId,
        "description" -> serRequest.description,
        "position" -> serRequest.position,
        "input" -> serRequest.input,
        "output" -> serRequest.output,
        "producedParams" -> serRequest.producedParams
    )

    def create = Action.async(parse.json) {
        implicit request => {
            request.body.validate(requestCreate).map {
                case (name, testId, description, position, input, output) => {
                    RequestOperate.createNew(name, testId, description, position, input, output) match {
                        case (result, doc) => {
                            result.map(
                                re => if (re.ok) Created(_json(doc))
                                else UnprocessableEntity("entity is not processible:" + result)
                            )
                        }

                    }
                }
            }
        }.recoverTotal(e => Future(BadRequest(JsError.toFlatJson(e))))
    }

    def update(id: String) = Action.async(parse.json) {
        implicit request => {
            request.body.validate(requestUpdate).map {
                case (name, testId, description, position, input, output) => {
                    RequestOperate.update(id, testId, name, position, description, input, output).map(
                        result => if (result.ok) NoContent
                        else BadRequest("Error when update document [%s]: %s".format(id, result))
                    )
                }
            }.recoverTotal(e => Future(BadRequest(JsError.toFlatJson(e))))
        }
    }

    def delete(id: String) = Action.async(parse.empty) {
        implicit request => {
            RequestOperate.delete(id).map(
                result => if (result.ok) NoContent
                else BadRequest("Error when delete run document [%s]: %s".format(id, result))
            )
        }
    }

    def get(id: String) = Action.async {
        if (id equals "0")
            RequestOperate.all().map {
                doc => Ok(JsArray(doc.map(_json)))
            }
        else
            RequestOperate.query(id).map {
                doc => Ok(doc.map(_json).getOrElse(Json.obj()))
            }.recoverWith {
                case e: Throwable => Future(BadRequest(e.toString))
            }
    }

    def getByTest(id: String) = Action.async {
        RequestOperate.queryByTest(id).map {
            doc => Ok(JsArray(doc.map(_json)))
        }
    }
}
