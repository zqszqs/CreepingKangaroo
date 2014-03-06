package controllers.service.backbone.modelobj.execute

import reactivemongo.bson.{BSONDocumentReader, BSONDocument, BSONDocumentWriter, BSONObjectID}
import controllers.service.backbone.modelobj.ServiceAPI
import controllers.service.backbone.modelobj.execute.ExecuteRequest.ExecuteRequestReader
import controllers.service.backbone.modelobj.execute.ExecuteRequest.ExecuteRequestWriter

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 16 2014
 */
case class ServiceExecute(id: BSONObjectID, testId: String, requests: List[ExecuteRequest]) {

}

object ServiceExecute {
    implicit object ServiceExecuteWriter extends BSONDocumentWriter[ServiceExecute] {
        def write(execute: ServiceExecute): BSONDocument = BSONDocument(
            "_id" -> execute.id,
            "testId" -> execute.testId,
            "requests" -> execute.requests
        )
    }

    implicit object ServiceExecuteReader extends BSONDocumentReader[ServiceExecute] {
        def read(doc: BSONDocument): ServiceExecute = ServiceExecute(
            doc.getAs[BSONObjectID]("_id").get,
            doc.getAs[String]("testId").get,
            doc.getAs[List[ExecuteRequest]]("requests").get
        )
    }
}
