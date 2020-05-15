package controllers.textparser

import javax.inject.Inject

import play.api.Logger
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

case class TextParserFormInput(plaintext: String)

/**
  * Takes HTTP requests and produces JSON.
  */
class TextParserController @Inject()(cc: TextParserControllerComponents)(
    implicit ec: ExecutionContext)
    extends TextParserBaseController(cc) {

  private val logger = Logger(getClass)

  private val form: Form[TextParserFormInput] = {
    import play.api.data.Forms._

    Form(
      mapping(
        "plaintext" -> text
      )(TextParserFormInput.apply)(TextParserFormInput.unapply)
    )
  }

  def getNouns(text: String): Action[AnyContent] = TextParserAction.async { implicit request =>
    logger.trace("Controller - GetNouns")
    textServiceHandler.getNouns(text).map { result =>
      Ok(result)
    }
  }

  def getVerbs(text: String): Action[AnyContent] = TextParserAction.async {
    implicit request =>
      logger.trace("Controller - GetVerbs")
      textServiceHandler.getVerbs(text).map { result =>
		Ok(result)
      }
  }
  
  def getUniqueNouns(text: String): Action[AnyContent] = TextParserAction.async { implicit request =>
    logger.trace("Controller - GetUniqueNouns")
    textServiceHandler.getUniqueNouns(text).map { result =>
      Ok(result)
    }
  }

  def getUniqueVerbs(text: String): Action[AnyContent] = TextParserAction.async {
    implicit request =>
      logger.trace("Controller - GetUniqueVerbs")
      textServiceHandler.getUniqueVerbs(text).map { result =>
		Ok(result)
      }
  }
  
  def analyze(text: String): Action[AnyContent] = TextParserAction.async { implicit request =>
    logger.trace("Controller - Analyze")
    textServiceHandler.analyze(text).map { result =>
      Ok(result)
    }
  }  
}
