package controllers.service.facetime

import play.api.mvc.{Action, Controller}

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 06 2014
 */
object ServiceSuiteTemplateController extends Controller {

    def getSuite = Action {
        Ok(views.html.service.suite.suite())
    }

    def getSuiteList = Action {
        Ok(views.html.service.suite.suitelist())
    }

    def getNewSuite = Action {
        Ok(views.html.service.suite.newsuite())
    }

    def getSuiteEdit = Action {
        Ok(views.html.service.suite.suiteedit())
    }

    def getSuiteDelete = Action {
        Ok(views.html.service.suite.suitedelete())
    }

}
