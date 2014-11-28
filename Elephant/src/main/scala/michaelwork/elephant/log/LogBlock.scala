package com.stubhub.qe.platform.elephant.log

import scala.collection.JavaConverters._


/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Aug. 19 2014
 */
case class LogBlock(id: String) {
    private var logs = List[Log]()

    def addLog(log: Log): LogBlock = {
        logs = log :: logs
        this
    }

    def getLogs: java.util.List[Log] = logs.reverse.asJava
}
