package com.stubhub.qe.platform.elephant.agency.workshop

import com.stubhub.qe.platform.elephant.agency.driverinfo.ChromeDriverInfo
import com.stubhub.qe.platform.elephant.agency.manage.ChromeUXDriverAgent
import com.stubhub.qe.platform.elephant.agency.workshop.DriverParameters._
import org.openqa.selenium.Platform
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.openqa.selenium.remote.{CapabilityType, DesiredCapabilities}

import scala.collection.immutable.HashMap

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Aug. 13 2014
 */
object ChromeDriverWorkshop {
    val CHROME_DRIVER_PATH_KEY = "webdriver.chrome.driver"

    def produceAgent(driverInfo: ChromeDriverInfo): ChromeUXDriverAgent = ChromeUXDriverAgent(
        new ChromeDriver(buildCapabilities(driverInfo.keys, driverInfo.configs))
    )

    def buildCapabilities(keys: Set[String], config: Map[String, String]): DesiredCapabilities = {
        val capabilities = DesiredCapabilities.chrome()

        config.get(USER_AGENT).map((agent) => {
            val option = new ChromeOptions()
            option.addArguments("--user-agent=" + agent)
            capabilities.setCapability(ChromeOptions.CAPABILITY, option)
        })

        capabilities.setCapability(CapabilityType.TAKES_SCREENSHOT, true)
        capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true)

        config.get(ENABLE_JAVASCRIPT) match {
            case None => capabilities.setJavascriptEnabled(true)
            case Some(b) => capabilities.setJavascriptEnabled(b.asInstanceOf[Boolean])
        }

        config.get(WEB_RUN_BROWSER_VERSION).map(capabilities.setVersion)

        config.get(WEB_RUN_PLATFORM).map((platform) => capabilities setPlatform Platform.valueOf(platform))

        (config.get(WEB_RUN_MODE), config.get(CHROME_DRIVER_PATH)) match {
            case (Some(LOCAL_MACHINE), Some(driverPath)) => System.setProperty(CHROME_DRIVER_PATH_KEY, driverPath)
            case (Some(LOCAL_MACHINE), None) =>
                Option(System.getenv(CHROME_DRIVER_PATH_KEY)).map(System.setProperty(CHROME_DRIVER_PATH_KEY, _))
            case _ => None
        }

        capabilities
    }


 }
