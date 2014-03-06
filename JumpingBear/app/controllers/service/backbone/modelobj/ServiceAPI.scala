package controllers.service.backbone.modelobj

import reactivemongo.bson.{BSONObjectID, BSONDocumentReader, BSONDocument, BSONDocumentWriter}

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 16 2014
 */
case class ServiceAPI(id: BSONObjectID, name: String, domain: String, endpoint: String) {
}

object ServiceAPI {

    implicit object ServiceAPIWriter extends BSONDocumentWriter[ServiceAPI] {
        def write(api: ServiceAPI): BSONDocument = BSONDocument(
            "_id" -> api.id,
            "name" -> api.name,
            "domain" -> api.domain,
            "endpoint" -> api.endpoint
        )
    }

    implicit object ServiceAPIReader extends BSONDocumentReader[ServiceAPI] {
        def read(doc: BSONDocument): ServiceAPI = ServiceAPI(
            doc.getAs[BSONObjectID]("_id").get,
            doc.getAs[String]("name").get,
            doc.getAs[String]("domain").get,
            doc.getAs[String]("endpoint").get
        )
    }

}
