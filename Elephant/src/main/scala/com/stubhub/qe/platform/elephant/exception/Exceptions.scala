package com.stubhub.qe.platform.elephant.exception

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Sep. 04 2014
 */
class Exceptions {

}

class NoSuchFieldException(message: String) extends RuntimeException(message)

class AppFileMissingException(message: String) extends RuntimeException(message)

class IncompatibleException(message: String) extends RuntimeException(message)
