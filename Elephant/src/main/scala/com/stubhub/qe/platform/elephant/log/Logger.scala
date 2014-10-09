package com.stubhub.qe.platform.elephant.log

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Aug. 19 2014
 */
object Logger {
    def log(text: String): Unit = {
        addLog(TextLog(text))
    }

    def log(text1: String, text2: String): Unit = {
        addLog(TextLog(text1)).addLog(TextLog(text2))
    }

    def important(text: String): Unit = {
        addLog(ImportantLog(text))
    }

    def important(text1: String, text2: String): Unit = {
        addLog(ImportantLog(text1)).addLog(ImportantLog(text2))
    }

    def warning(text: String): Unit = {
        addLog(WarningLog(text))
    }

    def warning(text1: String, text2: String): Unit = {
        addLog(WarningLog(text1)).addLog(WarningLog(text2))
    }

    def addLog(log: Log): LogBlock = LogRepository.current.newBlock.addLog(log)
}
