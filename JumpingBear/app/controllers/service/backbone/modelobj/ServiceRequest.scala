package controllers.service.backbone.modelobj

import reactivemongo.bson.{BSONObjectID, BSONDocumentReader, BSONDocument, BSONDocumentWriter}

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 16 2014
 */
case class ServiceRequest(
                             id: BSONObjectID,
                             name: String,
                             testId: String,
                             description: String,
                             position: Int,
                             input: RequestInput,
                             output: RequestOutput,
                             producedParams: Option[List[String]]
                             ) {

}

object ServiceRequest {

    implicit object ServiceRequestWriter extends BSONDocumentWriter[ServiceRequest] {
        def write(request: ServiceRequest): BSONDocument = BSONDocument(
            "_id" -> request.id,
            "name" -> request.name,
            "testId" -> request.testId,
            "description" -> request.description,
            "position" -> request.position,
            "input" -> request.input,
            "output" -> request.output,
            "producedParams" -> request.producedParams
        )
    }

    implicit object ServiceRequestReader extends BSONDocumentReader[ServiceRequest] {
        def read(doc: BSONDocument): ServiceRequest = ServiceRequest(
            doc.getAs[BSONObjectID]("_id").get,
            doc.getAs[String]("name").get,
            doc.getAs[String]("testId").get,
            doc.getAs[String]("description").get,
            doc.getAs[Int]("position").get,
            doc.getAs[RequestInput]("input").get,
            doc.getAs[RequestOutput]("output").get,
            doc.getAs[List[String]]("producedParams")
        )
    }

}