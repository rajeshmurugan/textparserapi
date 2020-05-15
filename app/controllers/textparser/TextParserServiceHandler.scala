package controllers.textparser

import javax.inject.{Inject, Provider}

import play.api.MarkerContext

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._

/**
  * Controls access to the backend data, returning [[JsValue]]
  */
class TextParserServiceHandler @Inject()(
    routerProvider: Provider[TextParserRouter],
    textParserService: TextParserService)(implicit ec: ExecutionContext) {

  def getNouns(text: String)(
      implicit mc: MarkerContext): Future[JsValue] = {
    val textparserFuture = textParserService.getNouns(text)
	textparserFuture.map { textparserData =>
        Json.toJson(textparserData)
      }
  }

  def getVerbs(text: String)(
      implicit mc: MarkerContext): Future[JsValue] = {
    val textparserFuture = textParserService.getVerbs(text)
	textparserFuture.map { textparserData =>
        Json.toJson(textparserData)
      }
  }
  def getUniqueNouns(text: String)(
      implicit mc: MarkerContext): Future[JsValue] = {
    val textparserFuture = textParserService.getNouns(text, true)
	textparserFuture.map { textparserData =>
        Json.toJson(textparserData)
      }
  }

  def getUniqueVerbs(text: String)(
      implicit mc: MarkerContext): Future[JsValue] = {
    val textparserFuture = textParserService.getVerbs(text,true)
	textparserFuture.map { textparserData =>
        Json.toJson(textparserData)
      }
  }
  
  def analyze(text: String)(
      implicit mc: MarkerContext): Future[JsValue] = {
    val textparserFuture = textParserService.analyze(text)
	textparserFuture.map { textparserData =>
        Json.toJson(textparserData)
      }
  }
}
