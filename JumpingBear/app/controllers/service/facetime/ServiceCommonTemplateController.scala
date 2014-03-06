package controllers.service.facetime

import play.api.mvc.{Action, Controller}

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 06 2014
 */
object ServiceCommonTemplateController extends Controller {
    def getOpButtons = Action {
        Ok(views.html.service.buttons())
    }

    def getLabels = Action {
        Ok(views.html.service.labels())
    }

    def getHome = Action {
        Ok(views.html.service.service())
    }
}
