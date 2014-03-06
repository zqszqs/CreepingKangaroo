package controllers.service.facetime

import play.api.mvc.{Action, Controller}

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 06 2014
 */
object ServiceRequestTemplateController extends Controller {

    def getRequest = Action {
        Ok(views.html.service.request.requestlist())
    }

    def getRequestCreate = Action {
        Ok(views.html.service.request.createrequest())
    }

    def getRequestDelete = Action {
        Ok(views.html.service.request.deleterequest())
    }

    def getRequestResult = Action {
        Ok(views.html.service.request.requestresult())
    }
}
