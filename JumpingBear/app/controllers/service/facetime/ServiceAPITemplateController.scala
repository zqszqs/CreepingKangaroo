package controllers.service.facetime

import play.api.mvc.{Action, Controller}

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 06 2014
 */
object ServiceAPITemplateController extends Controller {
    def getAPI = Action {
        Ok(views.html.service.api.api())
    }

    def getAPIList = Action {
        Ok(views.html.service.api.apilist())
    }

    def getCreateAPI = Action {
        Ok(views.html.service.api.createapi())
    }
}
