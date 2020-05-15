import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.libs.json.{ JsResult,JsValue, Json }
import play.api.mvc.{ RequestHeader, Result }
import play.api.test._
import play.api.test.Helpers._
import play.api.test.CSRFTokenHelper._


import scala.concurrent.Future

class TextParserRouterSpec extends PlaySpec with GuiceOneAppPerTest {

  "TextParserRouter" should {
	  
	val plaintext: String = "English%20is%20a%20West%20Germanic%20language%20that%20was%20first%20spoken%20in%20early%20medieval%20England%20and%20eventually%20became%20a%20global%20lingua%20franca.%20It%20is%20named%20after%20the%20Angles,%20one%20of%20the%20Germanic%20tribes%20that%20migrated%20to%20the%20area%20of%20Great%20Britain%20that%20later%20took%20their%20name,%20England.%20Both%20names%20derive%20from%20Anglia,%20a%20peninsula%20on%20the%20Baltic%20Sea.%20English%20is%20most%20closely%20related%20to%20Frisian%20and%20Low%20Saxon,%20while%20its%20vocabulary%20has%20been%20significantly%20influenced%20by%20other%20Germanic%20languages,%20particularly%20Norse%20(a%20North%20Germanic%20language),%20as%20well%20as%20Latin%20and%20French.%20John%20is%20good%20man."

    "extract list of nouns" in {
      val request = FakeRequest(GET, "/textparser/api/nouns/" + plaintext).withHeaders(HOST -> "localhost:9000").withCSRFToken
      val home:Future[Result] = route(app, request).get
	  
	  val expectedJson: JsValue = Json.parse("""
		{"nouns":["English","West","Germanic","language","England","lingua","franca","Angles","Germanic","tribes","area","Britain","name","England","names","Anglia","peninsula","Baltic","Sea","English","Frisian","Low","Saxon","vocabulary","languages","Norse","North","language","Latin","French","John","man"]}
          """)

      val results: JsValue = contentAsJson(home)
      results mustBe expectedJson
    } 
	
	"extract the list of verbs" in {
    val request = FakeRequest(GET, "/textparser/api/verbs/" + plaintext).withHeaders(HOST -> "localhost:9000").withCSRFToken
    val home:Future[Result] = route(app, request).get

	  val expectedJson: JsValue = Json.parse("""
		{"verbs":["is","was","spoken","became","is","named","migrated","took","derive","is","related","has","been","influenced","is"]}
          """)
    val results: JsValue = contentAsJson(home)
    results mustBe expectedJson
    }
	
    "extract list of unique nouns" in {
      val request = FakeRequest(GET, "/textparser/api/unique/nouns/" + plaintext).withHeaders(HOST -> "localhost:9000").withCSRFToken
      val home:Future[Result] = route(app, request).get
	  
	  val expectedJson: JsValue = Json.parse("""
		{"nouns":["english","west","germanic","language","england","lingua","franca","angles","tribes","area","britain","name","names","anglia","peninsula","baltic","sea","frisian","low","saxon","vocabulary","languages","norse","north","latin","french","john","man"]}
          """)

      val results: JsValue = contentAsJson(home)
      results mustBe expectedJson
    } 
	
	"extract the list of unique verbs" in {
    val request = FakeRequest(GET, "/textparser/api/unique/verbs/" + plaintext).withHeaders(HOST -> "localhost:9000").withCSRFToken
    val home:Future[Result] = route(app, request).get

	  val expectedJson: JsValue = Json.parse("""
		{"verbs":["is","was","spoken","became","named","migrated","took","derive","related","has","been","influenced"]}
          """)
    val results: JsValue = contentAsJson(home)
    results mustBe expectedJson
    }
	
	"extract the list of names and locations" in {
    val request = FakeRequest(GET, "/textparser/api/nameandlocation/" + plaintext).withHeaders(HOST -> "localhost:9000").withCSRFToken
    val home:Future[Result] = route(app, request).get

	  val expectedJson: JsValue = Json.parse("""
		{"persons":["John"],"Locations":["England","Baltic Sea"]}
          """)
    val results: JsValue = contentAsJson(home)
    results mustBe expectedJson
    }

	"analyze the given text" in {
    val request = FakeRequest(GET, "/textparser/api/analyze/" + plaintext).withHeaders(HOST -> "localhost:9000").withCSRFToken
    val home:Future[Result] = route(app, request).get

	  val expectedJson: JsValue = Json.parse("""
		{"tokenCount":{"total":107,"nouns":32,"verbs":15,"stopWords":23,"specialCharacters":13},"extractAll":{"nouns":["English","West","Germanic","language","England","lingua","franca","Angles","Germanic","tribes","area","Britain","name","England","names","Anglia","peninsula","Baltic","Sea","English","Frisian","Low","Saxon","vocabulary","languages","Norse","North","language","Latin","French","John","man"],"verbs":["is","was","spoken","became","is","named","migrated","took","derive","is","related","has","been","influenced","is"],"stopWords":["in","is","it","a","as","has","that","to","most","was","been","on","after","by","while","their","from","both","its","other","of","and","the"]},"extractUnique":{"nouns":["english","west","germanic","language","england","lingua","franca","angles","tribes","area","britain","name","names","anglia","peninsula","baltic","sea","frisian","low","saxon","vocabulary","languages","norse","north","latin","french","john","man"],"verbs":["is","was","spoken","became","named","migrated","took","derive","related","has","been","influenced"],"stopWords":["in","is","it","a","as","has","that","to","most","was","been","on","after","by","while","their","from","both","its","other","of","and","the"],"specialCharacters":[".",",","(",")"]}}
          """)
    val results: JsValue = contentAsJson(home)
    results mustBe expectedJson
    }	
	
  }

}