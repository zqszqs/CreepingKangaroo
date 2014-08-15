package com.stubhub.qe.platform.elephant.protocal

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Aug. 15 2014
 */
class Identifier

case class ID(id: String) extends Identifier
case class NAME(name: String) extends Identifier
case class CLASS_NAME(className: String) extends Identifier
case class LINK_TEXT(linkText: String) extends Identifier
case class PARTIAL_LINK_TEXT(partialLinkText: String) extends Identifier
case class CSS_SELECTOR(cssSelector: String) extends Identifier
case class TAG_NAME(tagName: String) extends Identifier
case class XPATH(xpath: String) extends Identifier
