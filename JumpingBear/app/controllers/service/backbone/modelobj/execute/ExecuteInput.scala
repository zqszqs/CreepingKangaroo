package controllers.service.backbone.modelobj.execute

import reactivemongo.bson.{BSONDocumentReader, BSONDocument, BSONDocumentWriter, BSONObjectID}
import controllers.service.backbone.modelobj.{RequestInput, Header}
import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 16 2014
 */
case class ExecuteInput(method: String, url: String, headers: Option[List[Header]],
                        cookies: Option[List[Header]], body: Option[String]) {

}

object ExecuteInput {

    implicit object ExecuteInputWriter extends BSONDocumentWriter[ExecuteInput] {
        def write(input: ExecuteInput): BSONDocument = BSONDocument(
            "method" -> input.method,
            "url" -> input.url,
            "headers" -> input.headers,
            "cookies" -> input.cookies,
            "body" -> input.body
        )
    }

    implicit object ExecuteInputReader extends BSONDocumentReader[ExecuteInput] {
        def read(doc: BSONDocument): ExecuteInput = ExecuteInput(
            doc.getAs[String]("method").get,
            doc.getAs[String]("url").get,
            doc.getAs[List[Header]]("headers"),
            doc.getAs[List[Header]]("cookies"),
            doc.getAs[String]("body")
        )
    }

    implicit val inputReader = (
        (__ \ 'method).read[String] and
            (__ \ 'url).read[String] and
            (__ \ 'headers).readNullable[List[Header]] and
            (__ \ 'cookies).readNullable[List[Header]] and
            (__ \ 'body).readNullable[String]
        )(ExecuteInput.apply _)

    implicit val inputWriter = (
        (__ \ 'method).write[String] and
            (__ \ 'url).write[String] and
            (__ \ 'headers).writeNullable[List[Header]] and
            (__ \ 'cookies).writeNullable[List[Header]] and
            (__ \ 'body).writeNullable[String]
        )(unlift(ExecuteInput.unapply))
}
