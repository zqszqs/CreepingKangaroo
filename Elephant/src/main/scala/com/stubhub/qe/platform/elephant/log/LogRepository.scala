package com.stubhub.qe.platform.elephant.log

import scala.collection.JavaConverters._
import scala.util.Random

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Aug. 19 2014
 */
object LogRepository {
    private var archives = List[LogDirectory]()
    private val activeDirectory = new ThreadLocal[LogDirectory]()

    def newDirectory(name: String): String = {
        activeDirectory.set(LogDirectory(System.currentTimeMillis() + Random.nextString(5), name))
        activeDirectory.get().id
    }

    def closeCurrent(): Unit = {
        archives = activeDirectory.get() :: archives
        activeDirectory.remove()
    }

    def current: LogDirectory = activeDirectory.get()

    def all: java.util.List[LogDirectory] = archives.reverse.asJava

    def getDirectory(id: String): LogDirectory = archives.find((d) => d.id.equals(id)).orNull
}
