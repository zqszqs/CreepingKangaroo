package com.stubhub.qe.platform.elephant.data

import java.io.File

import jxl.{Cell, Sheet, Workbook}
import play.api.libs.json.{JsObject, JsValue, Json}

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Sep. 03 2014
 */
class XLSDataLoader(file: File) extends DataLoader {
    override def dataList: DataNodeList = {
        val dataPairs = readXLS(file)
        val nodes = dataPairs.map((pairs) => DataNode(transform(pairs)))
        DataNodeList(nodes, List.empty)
    }

    private def row(r: Array[Cell]): List[String] = r.map(_.getContents).toList

    private def readXLS(file: File): List[List[(String, String)]] = {
        val sheet = Workbook.getWorkbook(file).getSheet(0)
        val title = row(sheet.getRow(0))
        val contents = readExcel(sheet, 1)

        contents.map(title.zip(_))
    }

    private def readExcel(sheet: Sheet, sheetNo: Int): List[List[String]] = {
        if (sheetNo >= sheet.getRows) List.empty
        else row(sheet.getRow(sheetNo)) :: readExcel(sheet, sheetNo + 1)
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

object XLSDataLoader {
    def apply(file: File): XLSDataLoader = new XLSDataLoader(file)
}
