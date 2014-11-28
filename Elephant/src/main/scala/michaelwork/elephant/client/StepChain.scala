package com.stubhub.qe.platform.elephant.client

import java.io.File

import com.stubhub.qe.platform.elephant.assertion.{AssertStatus, Assertion}
import com.stubhub.qe.platform.elephant.context.Contexts
import com.stubhub.qe.platform.elephant.data.{CSVDataLoader, DataNodeList, XLSDataLoader}
import com.stubhub.qe.platform.elephant.log._

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Aug. 15 2014
 */
class StepChain {
    protected val assertion = Assertion()

    protected def assertText(actual: String, expected: String, message: String): AssertStatus = assertion.assertEquals(actual, expected, message)

    protected def assertTrue(actual: Boolean, message: String): AssertStatus = assertion.assertTrue(actual, message)

    protected def assertFalse(actual: Boolean, message: String): AssertStatus = assertion.assertFalse(actual, message)

    protected def assertNumber(actual: Int, expected: Int, message: String): AssertStatus = assertion.assertEquals(actual, expected, message)

    protected def assertNumber(actual: Float, expected: Float, message: String): AssertStatus = assertion.assertEquals(actual, expected, message)

    protected def assertNumber(actual: Double, expected: Double, message: String): AssertStatus = assertion.assertEquals(actual, expected, message)

    protected def warning(message: String): Unit = Logger.warning(message)

    protected def important(message: String): Unit = Logger.important(message)

    protected def log(message: String): Unit = Logger.log(message)

    protected def context(key: String): String = Contexts.all.getValue(key)

    protected def loadData(filename: String): DataNodeList = {
        val file = new File(this.getClass.getResource(filename).toURI)

        if (file.exists()) {
            file.getName match {
                case csvFile if csvFile.endsWith(".csv") => CSVDataLoader(file).dataList
                case xlsFile if xlsFile.endsWith(".xls") => XLSDataLoader(file).dataList
            }
        } else DataNodeList(List.empty, List.empty)
    }
}
