import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.libs.json.{ JsResult, Json }
import play.api.mvc.{ RequestHeader, Result }
import play.api.test._
import play.api.test.Helpers._
import play.api.test.CSRFTokenHelper._


import scala.concurrent.Future

class TextParserRouterSpec extends PlaySpec with GuiceOneAppPerTest {

  "TextParserRouter" should {

    "render the list of nouns" in {
      val request = FakeRequest(GET, "/textparser/api").withHeaders(HOST -> "localhost:9000").withCSRFToken
      val home:Future[Result] = route(app, request).get

      val results: JsValue = Json.fromJson[JsValue](contentAsJson(home)).get
      results.head mustBe (("1","/v1/posts/1", "title 1", "blog post 1" ))
    } 
  }

}