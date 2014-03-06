package controllers.service.backbone.modelobj

import reactivemongo.bson.{BSONObjectID, BSONDocumentReader, BSONDocument, BSONDocumentWriter}

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 16 2014
 */
case class ServiceTest(id: BSONObjectID, name: String, suiteId: String, forAPI: String) {

}

object ServiceTest {
    implicit object ServiceTestWriter extends BSONDocumentWriter[ServiceTest] {
        def write(test: ServiceTest): BSONDocument = BSONDocument(
            "_id" -> test.id,
            "name" -> test.name,
            "suiteId" -> test.suiteId,
            "forAPI" -> test.forAPI
        )
    }

    implicit object ServiceTestReader extends BSONDocumentReader[ServiceTest] {
        def read(doc: BSONDocument): ServiceTest = ServiceTest(
            doc.getAs[BSONObjectID]("_id").get,
            doc.getAs[String]("name").get,
            doc.getAs[String]("suiteId").get,
            doc.getAs[String]("forAPI").get
        )
    }
}
