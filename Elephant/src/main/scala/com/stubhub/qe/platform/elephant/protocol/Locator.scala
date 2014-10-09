package com.stubhub.qe.platform.elephant.protocol

import scala.collection.JavaConverters._
import scala.collection.immutable.HashMap


/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Aug. 15 2014
 */
abstract class Locator(val name: String, val identifier: Identifier, val config: Map[String, Interceptor]) {

}

class Label(name: String, identifier: Identifier, config: java.util.Map[String, Interceptor]) extends Locator(name, identifier, Locator.toScalaMap(config))
class Link(name: String, identifier: Identifier, config: java.util.Map[String, Interceptor]) extends Locator(name, identifier, Locator.toScalaMap(config))
class Button(name: String, identifier: Identifier, config: java.util.Map[String, Interceptor]) extends Locator(name, identifier, Locator.toScalaMap(config))
class CheckBox(name: String, identifier: Identifier, config: java.util.Map[String, Interceptor]) extends Locator(name, identifier, Locator.toScalaMap(config))
class Image(name: String, identifier: Identifier, config: java.util.Map[String, Interceptor]) extends Locator(name, identifier, Locator.toScalaMap(config))
class RadioButton(name: String, identifier: Identifier, config: java.util.Map[String, Interceptor]) extends Locator(name, identifier, Locator.toScalaMap(config))
class SelectList(name: String, identifier: Identifier, config: java.util.Map[String, Interceptor]) extends Locator(name, identifier, Locator.toScalaMap(config))
class TextField(name: String, identifier: Identifier, config: java.util.Map[String, Interceptor]) extends Locator(name, identifier, Locator.toScalaMap(config))
class Table(name: String, identifier: Identifier, config: java.util.Map[String, Interceptor]) extends Locator(name, identifier, Locator.toScalaMap(config))

object Locator {
    def toScalaMap(m: java.util.Map[String, Interceptor]): Map[String, Interceptor] =
        if (m == null) HashMap.empty[String, Interceptor]
        else m.asScala.toMap
}