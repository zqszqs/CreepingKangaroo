package com.stubhub.qe.platform.elephant.context

import org.testng.ITestContext

import scala.collection.JavaConverters._
import scala.collection.immutable.HashMap

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Aug. 15 2014
 */
object Contexts {
    private val allTree = new ThreadLocal[ContextTree]
    private val localTree = new ThreadLocal[ContextTree]
//    private val rootTree = new ThreadLocal[ContextTree]

    def initTestContext(context: ITestContext) = {
        allTree.set(buildContext(context.getCurrentXmlTest.getAllParameters.asScala.toMap))
        localTree.set(buildContext(context.getCurrentXmlTest.getLocalParameters.asScala.toMap))
//        rootTree.set(initialRoot())
    }

    private def buildContext(map: Map[String, String]): ContextTree =
        map.toList.foldLeft(ContextTree(None, HashMap.empty[String, ContextTree]))((ctx, entry) => {
            ctx.addParameter(entry._1.split("\\.").toList, entry._2)
        })

//    private def initialRoot(): ContextTree = ???

    def all: ContextTree = allTree.get()
    def local: ContextTree = localTree.get()
//    private def root: ContextTree = rootTree.get()
}

