package com.stubhub.qe.platform.elephant.client.web

import com.stubhub.qe.platform.elephant.context.Context
import com.stubhub.qe.platform.elephant.protocal.Contract
import org.testng.ITestContext
import org.testng.annotations.BeforeTest
import org.testng.xml.XmlTest

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Aug. 15 2014
 */
class TestPlan extends WebStepChain {

    private var defaultContract: Contract =

    @BeforeTest
    def initGlobalContext(context: ITestContext, xml: XmlTest) = {
        Context.initTestContext(context)
    }
}
