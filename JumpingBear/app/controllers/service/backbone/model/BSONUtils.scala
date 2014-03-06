package controllers.service.backbone.model

import reactivemongo.bson.{BSONDocument, BSONObjectID}

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 16 2014
 */
object BSONUtils {
    def select(id: String): BSONDocument = BSONDocument("_id" -> BSONObjectID(id))

    def set(doc: BSONDocument): BSONDocument = BSONDocument("$set" -> doc)

    def pull(doc: BSONDocument): BSONDocument = BSONDocument("$pull" -> doc)

    def push(doc: BSONDocument): BSONDocument = BSONDocument("$push" -> doc)
}
