package com.stubhub.qe.platform.elephant.context

import org.testng.ITestContext
import scala.collection.JavaConverters._
import scala.collection.immutable.HashMap

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Aug. 15 2014
 */
object Context {
    private val testAll = new ThreadLocal[Map[String, String]]
    private val testLocal = new ThreadLocal[Map[String, String]]

    def initTestContext(context: ITestContext) = {
        testAll.set(context.getCurrentXmlTest.getAllParameters.asScala.toMap)
        testLocal.set(context.getCurrentXmlTest.getLocalParameters.asScala.toMap)
    }

    def all: Map[String, String] = testAll.get()
    def local: Map[String, String] = testLocal.get()
}

