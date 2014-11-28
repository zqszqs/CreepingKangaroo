package com.stubhub.qe.platform.elephant.data

import java.io.{File, FileReader}

import org.apache.commons.csv.{CSVFormat, CSVRecord}
import play.api.libs.json.{JsObject, JsValue, Json}

import scala.collection.JavaConverters._

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Sep. 03 2014
 */
class CSVDataLoader(file: File) extends DataLoader {
    override def dataList: DataNodeList = {
        val dataPairs = readFile(file)
        val nodes = dataPairs.map((pairs) => DataNode(transform(pairs)))
        DataNodeList(nodes, List.empty)
    }

    private def readFile(file: File): List[List[(String, String)]] = {
        val records = CSVFormat.EXCEL.parse(new FileReader(file)).getRecords.asScala.toList
        val title = records.head.asScala.toList
        parseRecords(title, records.tail)
    }

    private def parseRecords(title: List[String], records: List[CSVRecord]): List[List[(String, String)]] = {
        if (records.isEmpty) List.empty
        else title.zip(records.head.iterator().asScala.toList) :: parseRecords(title, records.tail)
    }

    private def transform(pairs: List[(String, String)]): JsValue = {
        pairs.foldLeft(Json.obj())((result, current) => {
            val newJson = current._1.split("\\.").foldRight(Json.toJson(current._2))((cur, re) => {
                Json.obj(cur -> re)
            })
            result.deepMerge(newJson.as[JsObject])
        })
    }
}

object CSVDataLoader {
    def apply(file: File): CSVDataLoader = new CSVDataLoader(file)
}
