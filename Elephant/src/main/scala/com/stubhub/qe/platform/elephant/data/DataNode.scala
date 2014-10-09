package com.stubhub.qe.platform.elephant.data

import com.google.gson.Gson
import com.stubhub.qe.platform.elephant.exception.NoSuchFieldException
import play.api.libs.json._


/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Sep. 02 2014
 */
class DataNode(val json: JsValue) {
    private val gson = new Gson()

    def value: String = json.as[String]

    def value(path: String): String = node(path.split("\\.")).value

    def node(path: String): DataNode = node(path.split("\\."))

    def castTo[T](clazz: Class[T]): T = gson.fromJson(Json.stringify(json), clazz)

    private def node(path: Array[String]): DataNode = {
        if (path.isEmpty) this
        else {
            strip(path, json) match {
                case undefined: JsUndefined => throw new NoSuchFieldException("Cannot find field: " + path.mkString("."))
                case v: JsValue => DataNode(v)
            }
        }
    }

    private def strip(path: Array[String], json: JsValue): JsValue = {
        if (path.isEmpty) json
        else strip(path.tail, json \ path.head)
    }

    override def toString: String = Json.stringify(json)
}

object DataNode {
    def apply(json: JsValue): DataNode = new DataNode(json)
}
