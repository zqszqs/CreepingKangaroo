package com.stubhub.qe.platform.elephant.log

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Aug. 18 2014
 */
class Log(val logType: String, val content: String)

class TextLog(content: String) extends Log("TEXT", content)
object TextLog {
    def apply(content: String): TextLog = new TextLog(content)
}

class ImportantLog(content: String) extends Log("IMPORTANT", content)
object ImportantLog {
    def apply(content: String): ImportantLog = new ImportantLog(content)
}

class WarningLog(content: String) extends Log("WARNING", content)
object WarningLog {
    def apply(content: String): WarningLog = new WarningLog(content)
}

