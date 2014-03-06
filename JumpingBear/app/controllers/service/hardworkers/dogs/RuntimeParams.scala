package controllers.service.hardworkers.dogs

import scala.collection.immutable.HashMap

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 17 2014
 */
trait RuntimeParams {
    def getParams(key: String): Option[String]
}

object EmptyRuntimeParams extends RuntimeParams {
    def getParams(key: String) = None
}

case class ServiceRuntimeParams(map: RuntimeParams, extra: List[(String, String)]) extends RuntimeParams {
    val paramMap = HashMap(extra:_*)

    def getParams(key: String) = paramMap.get(key).orElse(map.getParams(key))
}