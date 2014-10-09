package com.stubhub.qe.platform.elephant.client.web

import com.stubhub.qe.platform.elephant.agency.manage.{UXDriverAgent, DriverAgency}
import com.stubhub.qe.platform.elephant.client.web.browser.{Browser, IBrowser}
import com.stubhub.qe.platform.elephant.context.Contexts
import com.stubhub.qe.platform.elephant.log.{LogRepository, Logger}
import org.testng.ITestContext
import org.testng.annotations.{AfterMethod, BeforeMethod}
import org.testng.xml.XmlTest

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Aug. 15 2014
 */
class WebTestPlan extends WebStepChain {
    private val defaultBrowser = new ThreadLocal[IBrowser]()
    private val agents = new ThreadLocal[List[String]]() {{
        set(List.empty[String])
    }}

    protected def browser: IBrowser = {
        Option(defaultBrowser.get()) match {
            case Some(b) => b
            case None =>
                defaultBrowser.set(newBrowser)
                defaultBrowser.get()
        }
    }

    protected def recruitedAgents: List[String] = agents.get()

    protected def newBrowser: Browser = {
        val (agentId, agent) = DriverAgency.recruit(Contexts.all.getContextTree("browser"))
        agents.set(agentId :: agents.get())
        Browser(agentId, agent.asInstanceOf[UXDriverAgent])
    }

    protected def doFlow(flow: Flow, args: Array[AnyRef]): java.util.List[AnyRef] = doFlow(browser, flow, args)

    protected def doFlow(brsr: IBrowser, flow: Flow, args: Array[AnyRef]): java.util.List[AnyRef] = {
        try {
            val (as, values) = flow.start(brsr, args)
            assertion.add(as.all)
            values
        } catch {
            case e: Exception =>
                Logger.log("Exception occurs when executing flow " + flow.getClass.getSimpleName,
                           "With argument : " + args)
                throw e
        }
    }

    @BeforeMethod
    def initGlobalContext(context: ITestContext, xml: XmlTest) = {
        Contexts.initTestContext(context)
        val id = LogRepository.newDirectory(context.getName)
        context.setAttribute("LogDirectoryId", id)
    }


    @AfterMethod
    def teardownAgent(context: ITestContext, xml: XmlTest): Unit = {
        recruitedAgents.foreach(DriverAgency.retireAgent)
        LogRepository.closeCurrent()
    }
}
