package com.stubhub.qe.platform.elephant.agency.workshop

import java.io.File

import com.stubhub.qe.platform.elephant.agency.manage.FirefoxUXDriverAgent
import com.stubhub.qe.platform.elephant.context.ContextTree
import com.stubhub.qe.platform.elephant.log.LogRepository
import org.openqa.selenium.Platform
import org.openqa.selenium.firefox.{FirefoxDriver, FirefoxProfile}
import org.openqa.selenium.remote.{CapabilityType, DesiredCapabilities}
/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Aug. 13 2014
 */
object FirefoxDriverWorkshop {
    def produceAgent(context: ContextTree): FirefoxUXDriverAgent = FirefoxUXDriverAgent(
        new FirefoxDriver(buildCapabilities(context)), context
    )

    def buildCapabilities(context: ContextTree): DesiredCapabilities = {
        val capabilities = DesiredCapabilities.firefox()

        capabilities.setCapability(FirefoxDriver.PROFILE, buildFirefoxProfile(context.getContextTree("firefox")))

        val logBlock = LogRepository.current.newBlock
        logBlock.addLog(WorkshopLog("Created Firefox capability"))
        logBlock.addLog(WorkshopLog("Added Firefox profile"))

        context.getOptValue("firefox.enableJavascript") match {
            case None => capabilities.setJavascriptEnabled(true)
            case Some(decision) => capabilities.setJavascriptEnabled(decision.toBoolean)
        }

        capabilities.setCapability(CapabilityType.TAKES_SCREENSHOT, true)
        capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true)
        logBlock.addLog(WorkshopLog("Set accept ssl certs to true"))

        context.getOptValue("firefox.browserVersion").map((v) => {
            capabilities.setVersion(v)
            logBlock.addLog(WorkshopLog("Set browser version to " + v))
        })

        context.getOptValue("firefox.platform").map((platform) => capabilities setPlatform Platform.valueOf(platform))
        context.getOptValue("firefox.apiProxyHost").map(capabilities.setCapability(CapabilityType.PROXY, _))

        context.getOptValue("firefox.injector") match {
            case None => capabilities
            case Some(injector) =>
                if (classOf[CapabilityInjector] isAssignableFrom Class.forName(injector))
                    Class.forName(injector).newInstance().asInstanceOf[CapabilityInjector].inject(capabilities)
                else
                    capabilities
        }
    }

    def buildFirefoxProfile(context: ContextTree): FirefoxProfile = {
        val logBlock = LogRepository.current.newBlock
        logBlock.addLog(WorkshopLog("Build firefox profile"))

        val profile = context.getOptValue("profilePath") match {
            case None => new FirefoxProfile()
            case Some(path) => new FirefoxProfile(new File(path))
        }

        context.getOptValue("acceptUntrustedCertificates").map((setCert) => {
            profile setAcceptUntrustedCertificates setCert.toBoolean
            logBlock.addLog(WorkshopLog("Set accept untrusted certificates to " + setCert))
        })

        context.getOptValue("assumeUntrustedCertificateIssuer").map((assumeIssuer) => {
            profile setAssumeUntrustedCertificateIssuer assumeIssuer.toBoolean
            logBlock.addLog(WorkshopLog("Set assume untrusted certificate issuers to " + assumeIssuer))
        })

        context.getOptValue("binaryPath").map((path) => {
            System.setProperty("webdriver.firefox.bin", path)
            logBlock.addLog(WorkshopLog("Set webdriver.firefox.bin to " + path))
        })

        context.getOptValue("userAgent").map((userAgent) => {
            profile.setPreference("general.useragent.override", userAgent)
            logBlock.addLog(WorkshopLog("Set general.useragent.orverride to " + userAgent))
        })

        context.getOptValue("ntlmAuthTrustedUris").map(profile.setPreference("network.automatic-ntlm-auth.trusted-uris", _))

        context.getOptValue("downloadDir").map((dir) => {
            profile.setPreference("browser.download.dir", dir)
            profile.setPreference("browser.download.folderList", 2)
            profile.setPreference("browser.download.manager.showWhenStarting", false)
            profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/octet-stream,text/plain,application/pdf,application/zip,text/csv,text/html")
        })

        context.getOptValue("enableJavascript") match {
            case Some("false") =>
                profile.setPreference("javascript.enabled", false)
                logBlock.addLog(WorkshopLog("Set javascript.enable to false"))
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

        context.getOptValue("injector") match {
            case None => profile
            case Some(injector) =>
                if (classOf[FirefoxProfileInjector] isAssignableFrom Class.forName(injector))
                    Class.forName(injector).newInstance().asInstanceOf[FirefoxProfileInjector].inject(profile)
                else
                    profile
        }
    }
}
