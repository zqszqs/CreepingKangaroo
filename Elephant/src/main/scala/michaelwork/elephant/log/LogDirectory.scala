package com.stubhub.qe.platform.elephant.log

import scala.collection.JavaConverters._
import scala.collection.immutable.SortedMap
import scala.util.Random

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Aug. 19 2014
 */
case class LogDirectory(id: String, name: String) {
    private var blocks = SortedMap.empty[String, LogBlock]

    def newBlock: LogBlock = {
        val bid = System.currentTimeMillis() + Random.nextString(4)
        blocks = blocks + (bid -> LogBlock(bid))
        blocks.get(bid).orNull
    }

    def getBlock(bid: String): LogBlock = blocks.get(bid).orNull

    def getBlocks: java.util.List[LogBlock] = blocks.values.toList.asJava
}
