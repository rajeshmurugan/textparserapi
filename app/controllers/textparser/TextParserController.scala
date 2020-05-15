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
  final private val inputEmptyMessage  = "The input text is either an empty or invalid. Please provide the input text..."

  def validateInput(text: String) : Boolean = {text.trim == ""}

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
	if (validateInput(text)) {
		Future(Ok(Json.toJson(inputEmptyMessage)))
	}
	else {
    textServiceHandler.getNouns(text).map { result =>
      Ok(result)
    }
	}
  }

  def getVerbs(text: String): Action[AnyContent] = TextParserAction.async {
    implicit request =>
      logger.trace("Controller - GetVerbs")
	  if (validateInput(text)) {
		Future(Ok(Json.toJson(inputEmptyMessage)))
	}
	else {
      textServiceHandler.getVerbs(text).map { result =>
		Ok(result)
      }
	}
  }
  
  def getNameAndLocation(text: String): Action[AnyContent] = TextParserAction.async {
    implicit request =>
      logger.trace("Controller - GetNameAndLocation")
	  if (validateInput(text)) {
		Future(Ok(Json.toJson(inputEmptyMessage)))
	}
	else {
      textServiceHandler.getNameAndLocation(text).map { result =>
		Ok(result)
      }
	}
  }
  
  def getUniqueNouns(text: String): Action[AnyContent] = TextParserAction.async { implicit request =>
    logger.trace("Controller - GetUniqueNouns")
	if (validateInput(text)) {
		Future(Ok(Json.toJson(inputEmptyMessage)))
	}
	else {
    textServiceHandler.getUniqueNouns(text).map { result =>
      Ok(result)
    }
	}
  }

  def getUniqueVerbs(text: String): Action[AnyContent] = TextParserAction.async {
    implicit request =>
      logger.trace("Controller - GetUniqueVerbs")
	  if (validateInput(text)) {
		Future(Ok(Json.toJson(inputEmptyMessage)))
	}
	else {
      textServiceHandler.getUniqueVerbs(text).map { result =>
		Ok(result)
      }
	}
  }
  
  def analyze(text: String): Action[AnyContent] = TextParserAction.async { implicit request =>
    logger.trace("Controller - Analyze")
	if (validateInput(text)) {
		Future(Ok(Json.toJson(inputEmptyMessage)))
	}
	else {
		textServiceHandler.analyze(text).map { result =>
		Ok(result)
		}
	}
  }  
}
