package controllers.service.backbone.modelobj.execute

import reactivemongo.bson.{BSONDocumentReader, BSONDocument, BSONDocumentWriter, BSONObjectID}
import controllers.service.backbone.modelobj.{Header, ServiceRequest}
import controllers.service.backbone.modelobj.execute.ExecuteInput.ExecuteInputWriter
import controllers.service.backbone.modelobj.execute.ExecuteInput.ExecuteInputReader


/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 16 2014
 */
case class ExecuteRequest(
                             id: String,
                             status: String,
                             input: ExecuteInput,
                             output: ExecuteOutput,
                             producedParams: Option[List[Header]]
                             ) {

}

object ExecuteRequest {

    implicit object ExecuteRequestWriter extends BSONDocumentWriter[ExecuteRequest] {
        def write(request: ExecuteRequest): BSONDocument = BSONDocument(
            "id" -> request.id,
            "status" -> request.status,
            "input" -> request.input,
            "output" -> request.output,
            "producedParams" -> request.producedParams
        )
    }

    implicit object ExecuteRequestReader extends BSONDocumentReader[ExecuteRequest] {
        def read(doc: BSONDocument): ExecuteRequest = ExecuteRequest(
            doc.getAs[String]("id").get,
            doc.getAs[String]("status").get,
            doc.getAs[ExecuteInput]("input").get,
            doc.getAs[ExecuteOutput]("output").get,
            doc.getAs[List[Header]]("producedParams")
        )
    }
}