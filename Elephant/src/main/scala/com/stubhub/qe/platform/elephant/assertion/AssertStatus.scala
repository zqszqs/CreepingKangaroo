package com.stubhub.qe.platform.elephant.assertion

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Aug. 25 2014
 */
trait AssertStatus {
    def stopOnFail(): Unit

    def isSuccess: Boolean

    def isFailure: Boolean = !isSuccess
}

case class AssertSuccess(message: String, hintMessage: String) extends AssertStatus {
    override def stopOnFail(): Unit = {}

    override def isSuccess: Boolean = true
}

case class AssertFailure(message: String, hintMessage: String) extends AssertStatus {
    override def stopOnFail(): Unit = throw new AssertionError(message + ":" + hintMessage)

    override def isSuccess: Boolean = false
}
