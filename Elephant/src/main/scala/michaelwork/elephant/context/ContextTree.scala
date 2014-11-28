package com.stubhub.qe.platform.elephant.context

import scala.collection.immutable.HashMap

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Aug. 18 2014
 */
class ContextTree (value: Option[String], sub: Map[String, ContextTree]) {
    def getValue: String = value.orNull

    def getOptValue(path: String): Option[String] = get(split(path)).map(_.getValue)

    def getValue(path: String): String = getOptValue(path).orNull

    def getValue(path: String, default: String): String = getOptValue(path).getOrElse(default)

    def getContextTree(path: String): ContextTree = get(split(path)).orNull

    def addParameter(p: List[String], v: String): ContextTree = p match {
        case Nil => ContextTree(Some(v), sub)
        case head :: tail => sub.get(head) match {
            case None => ContextTree(value, sub + (head -> newContext(tail, v)))
            case Some(ctx) => ContextTree(value, sub + (head -> ctx.addParameter(tail, v)))
        }
    }

    private def split(path: String): List[String] = path.split("\\.").toList

    private def get(path: List[String]): Option[ContextTree] = path match {
        case Nil => Some(this)
        case head :: tail => sub.get(head).flatMap(_.get(tail))
    }

    private def newContext(path: List[String], value: String): ContextTree = path match {
        case Nil => ContextTree(Some(value), HashMap.empty[String, ContextTree])
        case head :: tail => ContextTree(None, HashMap(head -> newContext(tail, value)))

    }
}

object ContextTree {
    def apply(value: Option[String], sub: Map[String, ContextTree]) = new ContextTree(value, sub)
}