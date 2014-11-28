package com.stubhub.qe.platform.elephant.protocol

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Aug. 15 2014
 */
class Identifier

/** for web testing **/
case class ID(id: String) extends Identifier
case class CLASS_NAME(className: String) extends Identifier
case class LINK_TEXT(linkText: String) extends Identifier
case class PARTIAL_LINK_TEXT(partialLinkText: String) extends Identifier
case class CSS_SELECTOR(cssSelector: String) extends Identifier
case class XPATH(xpath: String) extends Identifier

/** for native app testing **/
case class TEXT(text: String) extends Identifier
case class FULL_TEXT(text: String) extends Identifier
case class XPATH_INDEX(index: Int) extends Identifier
case class FIND(value: String) extends Identifier
case class IosUI(using: String) extends Identifier
case class AndroidUI(using: String) extends Identifier

/** for both web testing and app testing **/
case class TAG_NAME(tagName: String) extends Identifier
case class NAME(name: String) extends Identifier