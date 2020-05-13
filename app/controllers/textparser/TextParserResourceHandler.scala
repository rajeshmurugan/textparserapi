package controllers.textparser

import javax.inject.{Inject, Provider}

import play.api.MarkerContext

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._

/**
  * Controls access to the backend data, returning [[JsValue]]
  */
class TextParserResourceHandler @Inject()(
    routerProvider: Provider[TextParserRouter],
    textRepository: TextParserRepositoryImpl)(implicit ec: ExecutionContext) {

  def getNouns(text: String)(
      implicit mc: MarkerContext): Future[JsValue] = {
    val postFuture = textRepository.getNouns(text)
	postFuture.map { postData =>
        Json.toJson(postData)
      }
  }

  def getVerbs(text: String)(
      implicit mc: MarkerContext): Future[JsValue] = {
    val postFuture = textRepository.getVerbs(text)
	postFuture.map { postData =>
        Json.toJson(postData)
      }
  }
  def getUniqueNouns(text: String)(
      implicit mc: MarkerContext): Future[JsValue] = {
    val postFuture = textRepository.getUniqueNouns(text)
	postFuture.map { postData =>
        Json.toJson(postData)
      }
  }

  def getUniqueVerbs(text: String)(
      implicit mc: MarkerContext): Future[JsValue] = {
    val postFuture = textRepository.getUniqueVerbs(text)
	postFuture.map { postData =>
        Json.toJson(postData)
      }
  }
  
  def analyze(text: String)(
      implicit mc: MarkerContext): Future[JsValue] = {
    val postFuture = textRepository.analyze(text)
	postFuture.map { postData =>
        Json.toJson(postData)
      }
  }
}
