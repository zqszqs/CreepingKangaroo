package com.stubhub.qe.platform.elephant.assertion

import java.util

import com.stubhub.qe.platform.elephant.log.{Log, Logger}

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Aug. 25 2014
 */
class Assertion {
    private val assertions: util.List[AssertStatus] = new util.ArrayList[AssertStatus]()

    def assertTrue(bool: Boolean, message: String): AssertStatus = assertResult(bool, message, "Expect true but found " + bool)

    def assertFalse(bool: Boolean, message: String): AssertStatus = assertResult(!bool, message, "Expected false but found " + bool)

    def assertNull(obj: Object, message: String): AssertStatus = assertResult(obj == null, message, "Expected null but obj is not null")

    def assertNotNull(obj: Object, message: String): AssertStatus = assertResult(obj != null, message, "Expect obj not being null")

    def assertEquals(actual: Byte, expected: Byte, message: String): AssertStatus = assertPrimitives(actual, expected, message)

    def assertEquals(actual: Boolean, expected: Boolean, message: String): AssertStatus = assertPrimitives(actual, expected, message)

    def assertEquals(actual: Char, expected: Char, message: String): AssertStatus = assertPrimitives(actual, expected, message)

    def assertEquals(actual: Float, expected: Float, message: String): AssertStatus = assertPrimitives(actual, expected, message)

    def assertEquals(actual: Double, expected: Double, message: String): AssertStatus = assertPrimitives(actual, expected, message)

    def assertEquals(actual: Long, expected: Long, message: String): AssertStatus = assertPrimitives(actual, expected, message)

    def assertEquals(actual: Short, expected: Short, message: String): AssertStatus = assertPrimitives(actual, expected, message)

    def assertEquals(actual: Int, expected: Int, message: String): AssertStatus = assertPrimitives(actual, expected, message)

    def assertEquals(actual: String, expected: String, message: String): AssertStatus =
        assertEquals(actual.asInstanceOf[AnyRef], expected.asInstanceOf[AnyRef], message)

    def assertEquals(actual: AnyRef, expected: AnyRef, message: String): AssertStatus =
        if ((expected == null) && (actual == null))
            AssertSuccess(message, "actual and expected value are both null")
        else
            assertResult(expected.equals(actual), message, "Expect [%s] but found [%s]".format(expected, actual))

    private def assertPrimitives(actual: AnyVal, expected: AnyVal, message: String): AssertStatus =
        assertResult(actual == expected, message, "Expect [%s] but found [%s]".format(expected, actual))

    private def assertResult(result: Boolean, message: String, hintMessage: String): AssertStatus = result match {
        case false =>
            Logger.addLog(AssertionLog(message + ":" + hintMessage))
            val af = AssertFailure(message, hintMessage)
            assertions.add(af)
            af
        case true =>
            val as = AssertSuccess(message, hintMessage)
            assertions.add(as)
            as
    }

    def all: java.util.List[AssertStatus] = assertions

    def add(status: AssertStatus): Unit = assertions.add(status)

    def add(statuses: util.List[AssertStatus]) = assertions.addAll(statuses)
}

object Assertion {
    def apply(): Assertion = new Assertion
}


class AssertionLog(context: String) extends Log("ASSERTION", context)

object AssertionLog {
    def apply(context: String): AssertionLog = new AssertionLog(context)
}
