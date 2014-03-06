package controllers.service.backbone.modelobj.execute

import controllers.service.backbone.modelobj.{RequestOutput, Header}
import reactivemongo.bson.{BSONDocumentReader, BSONDocument, BSONDocumentWriter}
import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 16 2014
 */
case class ExecuteOutput(responseCode: String, headers: Option[List[Header]], body: Option[String]) {

}

object ExecuteOutput {
    implicit object ExecuteOutputWriter extends BSONDocumentWriter[ExecuteOutput] {
        def write(output: ExecuteOutput): BSONDocument = BSONDocument(
            "responseCode" -> output.responseCode,
            "headers" -> output.headers,
            "body" -> output.body
        )
    }

    implicit object RequestOutputReader extends BSONDocumentReader[ExecuteOutput] {
        def read(doc: BSONDocument): ExecuteOutput = ExecuteOutput(
            doc.getAs[String]("responseCode").get,
            doc.getAs[List[Header]]("headers"),
            doc.getAs[String]("body")
        )
    }

    implicit val outputReader = (
        (__ \ 'responseCode).read[String] and
            (__ \ 'headers).readNullable[List[Header]] and
            (__ \ 'body).readNullable[String]
        )(ExecuteOutput.apply _)

    implicit val outputWriter = (
        (__ \ 'responseCode).write[String] and
            (__ \ 'headers).writeNullable[List[Header]] and
            (__ \ 'body).writeNullable[String]
        )(unlift(ExecuteOutput.unapply))
}
