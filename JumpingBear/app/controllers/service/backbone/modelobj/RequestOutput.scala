package controllers.service.backbone.modelobj

import reactivemongo.bson.{BSONDocumentReader, BSONDocument, BSONDocumentWriter}
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.concurrent.Execution.Implicits._


/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 16 2014
 */
case class RequestOutput(responseCode: String, headers: Option[List[Header]], body: Option[String]) {

}

object RequestOutput {
    implicit object RequestOutputWriter extends BSONDocumentWriter[RequestOutput] {
        def write(output: RequestOutput): BSONDocument = BSONDocument(
            "responseCode" -> output.responseCode,
            "headers" -> output.headers,
            "body" -> output.body
        )
    }

    implicit object RequestOutputReader extends BSONDocumentReader[RequestOutput] {
        def read(doc: BSONDocument): RequestOutput = RequestOutput(
            doc.getAs[String]("responseCode").get,
            doc.getAs[List[Header]]("headers"),
            doc.getAs[String]("body")
        )
    }

    implicit val outputReader = (
        (__ \ 'responseCode).read[String] and
            (__ \ 'headers).readNullable[List[Header]] and
            (__ \ 'body).readNullable[String]
        )(RequestOutput.apply _)

    implicit val outputWriter = (
        (__ \ 'responseCode).write[String] and
            (__ \ 'headers).writeNullable[List[Header]] and
            (__ \ 'body).writeNullable[String]
        )(unlift(RequestOutput.unapply))
}
