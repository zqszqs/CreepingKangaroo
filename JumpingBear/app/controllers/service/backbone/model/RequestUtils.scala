package controllers.service.backbone.model

import controllers.service.backbone.modelobj.{RequestOutput, RequestInput, Header, ServiceRequest}
import controllers.service.backbone.modelobj.execute.{ExecuteOutput, ExecuteInput, ExecuteRequest}
import controllers.service.hardworkers.dogs.RuntimeParams

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 16 2014
 */
object RequestUtils {

    def requestInputToExecuteInput(input: RequestInput, params: RuntimeParams): ExecuteInput = {
        def _buildUrl(endpoint: String, queryParams: Option[List[Header]]) =
            endpoint + queryParams.map(_.map(h => h.name + "=" + h.value) mkString "&").map("?" + _).getOrElse("")

        ExecuteInput(
            input.method,
            _buildUrl(input.endpoint, input.queryParams),
            input.headers,
            input.cookies,
            input.body
        )
    }

    def requestOutputToExecuteOutput(output: RequestOutput, params: RuntimeParams): ExecuteOutput = {
        ExecuteOutput(
            output.responseCode,
            output.headers,
            output.body
        )
    }

    def serviceRequestToExecuteRequest(serRequest: ServiceRequest, params: RuntimeParams): ExecuteRequest = {
        ExecuteRequest(
            serRequest.id.stringify,
            "PENDING",
            requestInputToExecuteInput(serRequest.input, params),
            requestOutputToExecuteOutput(serRequest.output, params),
            serRequest.producedParams.map(_.map(Header(_, "")))
        )
    }

}
