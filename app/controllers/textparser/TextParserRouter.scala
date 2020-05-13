package controllers.textparser

import javax.inject.Inject

import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

/**
  * Routes and URLs to the TextParseRouter controller.
  */
class TextParserRouter @Inject()(controller: TextParserController) extends SimpleRouter {

  override def routes: Routes = {

	case GET(p"/nouns/$text") =>
      controller.getNouns(text)
	  
    case GET(p"/verbs/$text") =>
      controller.getVerbs(text)
	  
	case GET(p"/unique/nouns/$text") =>
      controller.getUniqueNouns(text)
	  
    case GET(p"/unique/verbs/$text") =>
      controller.getUniqueVerbs(text)	
	  
    case GET(p"/analyze/$text") =>
      controller.analyze(text)
  }

}
