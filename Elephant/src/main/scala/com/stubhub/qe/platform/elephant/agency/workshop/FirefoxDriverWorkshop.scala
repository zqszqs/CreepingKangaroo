package com.stubhub.qe.platform.elephant.agency.workshop

import java.io.File

import com.stubhub.qe.platform.elephant.agency.driverinfo.FirefoxDriverInfo
import com.stubhub.qe.platform.elephant.agency.manage.FirefoxUXDriverAgent
import com.stubhub.qe.platform.elephant.agency.workshop.DriverParameters._
import org.openqa.selenium.Platform
import org.openqa.selenium.firefox.{FirefoxDriver, FirefoxProfile}
import org.openqa.selenium.remote.{CapabilityType, DesiredCapabilities}

import scala.collection.immutable.HashMap

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Aug. 13 2014
 */
object FirefoxDriverWorkshop {
    def produceAgent(driverInfo: FirefoxDriverInfo): FirefoxUXDriverAgent = FirefoxUXDriverAgent(
        new FirefoxDriver(buildCapabilities(driverInfo.keys, driverInfo.configs))
    )

    def buildCapabilities(keys: Set[String], config: Map[String, String]): DesiredCapabilities = {
        val capabilities = DesiredCapabilities.firefox()

        capabilities.setCapability(FirefoxDriver.PROFILE, buildFirefoxProfile(keys, config))

        config.get(ENABLE_JAVASCRIPT) match {
            case None => capabilities.setJavascriptEnabled(true)
            case Some(decision) => capabilities.setJavascriptEnabled(decision.asInstanceOf[Boolean])
        }

        capabilities.setCapability(CapabilityType.TAKES_SCREENSHOT, true)
        capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true)

        config.get(WEB_RUN_BROWSER_VERSION).map(capabilities.setVersion)
        config.get(WEB_RUN_PLATFORM).map((platform) => capabilities setPlatform Platform.valueOf(platform))
        config.get(API_PROXY_HOST).map(capabilities.setCapability(CapabilityType.PROXY, _))

        capabilities
    }

    def buildFirefoxProfile(keys: Set[String], config: Map[String, String]): FirefoxProfile = {
        val profile = config.get(FIREFOX_USER_PROFILE_PATH) match {
            case None => new FirefoxProfile()
            case Some(path) => new FirefoxProfile(new File(path))
        }

        config.get(SET_ACCEPT_UNTRUSTED_CERTIFICATES).map((setCert) => profile setAcceptUntrustedCertificates setCert.asInstanceOf[Boolean])

        config.get(SET_ASSUME_UNTRUSTED_CERTIFICATE_ISSUERS).map((assumeIssuer) => profile setAssumeUntrustedCertificateIssuer assumeIssuer.asInstanceOf[Boolean])

        config.get(FIREFOX_BINARY_PATH).map(System.setProperty("webdriver.firefox.bin", _))

        config.get(USER_AGENT).map(profile.setPreference("general.useragent.override", _))

        config.get(NTLM_AUTH_TRUSTED_URIS).map(profile.setPreference("network.automatic-ntlm-auth.trusted-uris", _))

        config.get(BROWSER_DOWNLOAD_DIR).map((dir) => {
            profile.setPreference("browser.download.dir", dir)
            profile.setPreference("browser.download.folderList", 2)
            profile.setPreference("browser.download.manager.showWhenStarting", false)
            profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/octet-stream,text/plain,application/pdf,application/zip,text/csv,text/html")
        })

        config.get(ENABLE_JAVASCRIPT) match {
            case Some("false") | None => profile.setPreference("javascript.enabled", false)
            case _ => None
        }

        profile.setPreference("capability.policy.default.Window.QueryInterface", "allAccess")
        profile.setPreference("capability.policy.default.Window.frameElement.get", "allAccess")
        profile.setPreference("capability.policy.default.HTMLDocument.compatMode.get", "allAccess")
        profile.setPreference("capability.policy.default.Document.compatMode.get", "allAccess")
        profile.setEnableNativeEvents(false)
        // Fix unresponsive script problem jirap#MAUI-124
        profile.setPreference("dom.max_chrome_script_run_time", 0)
        profile.setPreference("dom.max_script_run_time", 0)
        // Fix plugin container crash problem
        profile.setPreference("dom.ipc.plugins.enabled", false)
        profile.setPreference("dom.ipc.plugins.flash.subprocess.crashreporter.enabled", false)

        profile
    }
}
