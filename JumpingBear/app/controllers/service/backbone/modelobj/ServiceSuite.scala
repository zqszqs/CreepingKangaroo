package controllers.service.backbone.modelobj

import reactivemongo.bson.{BSONObjectID, BSONDocumentReader, BSONDocument, BSONDocumentWriter}

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 16 2014
 */
case class ServiceSuite(id: BSONObjectID, name: String) {

}

object ServiceSuite {
    implicit object ServiceSuiteWriter extends BSONDocumentWriter[ServiceSuite] {
        def write(suite: ServiceSuite) = BSONDocument(
            "_id" -> suite.id,
            "name" -> suite.name
        )
    }

    implicit object ServiceSuiteReader extends BSONDocumentReader[ServiceSuite] {
        def read(doc: BSONDocument): ServiceSuite = ServiceSuite(
            doc.getAs[BSONObjectID]("_id").get,
            doc.getAs[String]("name").get
        )
    }
}
