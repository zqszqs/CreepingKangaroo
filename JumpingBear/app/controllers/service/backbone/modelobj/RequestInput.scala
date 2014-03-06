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
case class RequestInput(
                           method: String,
                           endpoint: String,
                           queryParams: Option[List[Header]],
                           headers: Option[List[Header]],
                           cookies: Option[List[Header]],
                           body: Option[String]) {
}

object RequestInput {
    implicit object RequestInputWriter extends BSONDocumentWriter[RequestInput] {
        def write(input: RequestInput): BSONDocument = BSONDocument(
            "method" -> input.method,
            "endpoint" -> input.endpoint,
            "queryParams" -> input.queryParams,
            "headers" -> input.headers,
            "cookies" -> input.cookies,
            "body" -> input.body
        )
    }

    implicit object RequestInputReader extends BSONDocumentReader[RequestInput] {
        def read(doc: BSONDocument): RequestInput = RequestInput(
            doc.getAs[String]("method").get,
            doc.getAs[String]("endpoint").get,
            doc.getAs[List[Header]]("queryParams"),
            doc.getAs[List[Header]]("headers"),
            doc.getAs[List[Header]]("cookies"),
            doc.getAs[String]("body")
        )
    }

    implicit val inputReader = (
        (__ \ 'method).read[String] and
            (__ \ 'endpoint).read[String] and
            (__ \ 'queryParams).readNullable[List[Header]] and
            (__ \ 'headers).readNullable[List[Header]] and
            (__ \ 'cookies).readNullable[List[Header]] and
            (__ \ 'body).readNullable[String]
        )(RequestInput.apply _)

    implicit val inputWriter = (
        (__ \ 'method).write[String] and
            (__ \ 'endpoint).write[String] and
            (__ \ 'queryParams).writeNullable[List[Header]] and
            (__ \ 'headers).writeNullable[List[Header]] and
            (__ \ 'cookies).writeNullable[List[Header]] and
            (__ \ 'body).writeNullable[String]
        )(unlift(RequestInput.unapply))
}
