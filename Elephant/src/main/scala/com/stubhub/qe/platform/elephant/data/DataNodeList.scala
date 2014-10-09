package com.stubhub.qe.platform.elephant.data

import java.util

import scala.collection.JavaConverters._

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Sep. 02 2014
 */
class DataNodeList(dataNodes: List[DataNode], mappings: List[(String, Class[_])]) {

    private def field(name: String, value: String, nodes: List[DataNode]): List[DataNode] = {
        dataNodes.filter(
            node => node.value(name).equals(value)
        )
    }

    def forMethod(name: String): DataNodeList = DataNodeList(field("method", name, dataNodes), mappings)

    def withId(id: String): DataNodeList = DataNodeList(field("id", id, dataNodes), mappings)

    def forSite(site: String): DataNodeList = DataNodeList(field("site", site, dataNodes), mappings)

    def fieldEqual(name: String, value: String): DataNodeList = DataNodeList(field(name, value, dataNodes), mappings)

    def cast(name: String, clazz: Class[_]): DataNodeList = DataNodeList(dataNodes, (name, clazz) :: mappings)

    def toIterator: util.Iterator[Array[AnyRef]] = mappings match {
        case Nil => dataNodes.map((n) => Array(n.asInstanceOf[AnyRef])).asJava.iterator()
        case l: List[(String, Class[_])] => dataNodes.map((n) => castToObject(n, l).reverse.toArray).asJava.iterator()
    }

    private def castToObject(node: DataNode, mappings: List[(String, Class[_])]): List[AnyRef] = mappings match {
        case Nil => List.empty
        case head :: tail => node.node(head._1).castTo(head._2).asInstanceOf[AnyRef] :: castToObject(node, tail)
    }
}

object DataNodeList {
    def apply(dataNodes: List[DataNode], mappings: List[(String, Class[_])]): DataNodeList = new DataNodeList(dataNodes, mappings)
}
