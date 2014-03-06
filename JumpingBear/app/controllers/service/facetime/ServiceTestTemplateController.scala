package controllers.service.facetime

import play.api.mvc.{Action, Controller}

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 06 2014
 */
object ServiceTestTemplateController extends Controller {


    def getTestEdit = Action {
        Ok(views.html.service.test.testedit())
    }

    def getTest = Action {
        Ok(views.html.service.test.testlist())
    }

    def getTestDelete = Action {
        Ok(views.html.service.test.testdelete())
    }

    def getNewTest = Action {
        Ok(views.html.service.test.newtest())
    }

}
