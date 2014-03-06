package controllers.service.backbone.modelobj

import reactivemongo.bson.{BSONDocumentReader, BSONDocument, BSONDocumentWriter}
import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 16 2014
 */
case class Header(name: String, value: String) {

}

object Header {

    implicit object HeaderWriter extends BSONDocumentWriter[Header] {
        def write(header: Header): BSONDocument = BSONDocument(
            "name" -> header.name,
            "value" -> header.value
        )
    }

    implicit object HeaderReader extends BSONDocumentReader[Header] {
        def read(doc: BSONDocument): Header = Header(
            doc.getAs[String]("name").get,
            doc.getAs[String]("value").get
        )
    }

    implicit val headerRead = (
        (__ \ 'name).read[String] and
            (__ \ 'value).read[String]
        )(Header.apply _)

    implicit val headerWrite = (
        (__ \ 'name).write[String] and
            (__ \ 'value).write[String]
        )(unlift(Header.unapply))
}
