package com.stubhub.qe.platform.elephant.agency.workshop

import java.io.{FileWriter, File}

import com.stubhub.qe.platform.elephant.agency.manage.ChromeUXDriverAgent
import com.stubhub.qe.platform.elephant.context.ContextTree
import com.stubhub.qe.platform.elephant.log.LogRepository
import org.apache.commons.io.{IOUtils, FileUtils}
import org.openqa.selenium.Platform
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.openqa.selenium.remote.{CapabilityType, DesiredCapabilities}
import org.testng.reporters.Files

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Aug. 13 2014
 */
object ChromeDriverWorkshop {
    val CHROME_DRIVER_PATH_KEY = "webdriver.chrome.driver"

    def produceAgent(context: ContextTree): ChromeUXDriverAgent = ChromeUXDriverAgent(
        new ChromeDriver(buildCapabilities(context)), context
    )

    def buildCapabilities(context: ContextTree): DesiredCapabilities = {
        val logBlock = LogRepository.current.newBlock

        val capabilities = DesiredCapabilities.chrome()
        logBlock.addLog(WorkshopLog("Created chrome capability"))

        val option = new ChromeOptions()
        context.getOptValue("chrome.userAgent") match {
            case None => capabilities.setCapability(ChromeOptions.CAPABILITY, new ChromeOptions)
            case Some(agent) =>
                option.addArguments("--user-agent=" + agent)
                capabilities.setCapability(ChromeOptions.CAPABILITY, option)
                logBlock.addLog(WorkshopLog("Set user agent to " + agent))
        }

        capabilities.setCapability(CapabilityType.TAKES_SCREENSHOT, true)
        capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true)
        logBlock.addLog(WorkshopLog("Set accept ssl certs to true"))

        context.getOptValue("chrome.enableJavascript") match {
            case None =>
                capabilities.setJavascriptEnabled(true)
                logBlock.addLog(WorkshopLog("Enabled Javascript"))
            case Some(b) => capabilities.setJavascriptEnabled(b.toBoolean)
        }

        context.getOptValue("chrome.browserVersion").map((v) => {
            capabilities.setVersion(v)
            logBlock.addLog(WorkshopLog("Set browser version to: " + v))
        })

        context.getOptValue("chrome.platform").map((platform) => capabilities setPlatform Platform.valueOf(platform))

        (context.getOptValue("webRunMode"), context.getOptValue("chrome.driverPath")) match {
            case (Some("LocalMachine"), Some(driverPath)) =>
                System.setProperty(CHROME_DRIVER_PATH_KEY, driverPath)
                logBlock.addLog(WorkshopLog("Using Chrome driver path: " + driverPath))
            case (Some("LocalMachine"), None) | (None, None) =>
                Option(System.getenv(CHROME_DRIVER_PATH_KEY)) match {
                    case Some(path) =>
                        System.setProperty(CHROME_DRIVER_PATH_KEY, path)
                        logBlock.addLog(WorkshopLog("Using Chrome driver path: " + path))
                    case None => System.setProperty(CHROME_DRIVER_PATH_KEY, builtInChrome)
                }
            case _ =>
        }

        context.getOptValue("chrome.injector") match {
            case None => capabilities
            case Some(injector) =>
                if (classOf[CapabilityInjector] isAssignableFrom Class.forName(injector))
                    Class.forName(injector).newInstance().asInstanceOf[CapabilityInjector].inject(capabilities)
                else
                    capabilities
        }
    }

    private def builtInChrome: String = System.getProperty("os.name") match {
        case windows if windows.startsWith("Win") =>
            copyDriver("driver/windows/chromedriver.exe").getAbsolutePath
        case macos if macos.startsWith("Mac") =>
            copyDriver("driver/macos/chromedriver").getAbsolutePath
        case _ => throw new Error("System not supported")
    }

    private def copyDriver(path: String): File = {
        val targetFile = new File(path)
        val target = new FileWriter(targetFile)
        IOUtils.copy(this.getClass.getClassLoader.getResourceAsStream(path), target)
        targetFile
    }
 }
