package controllers.textparser

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}
import play.api.libs.json._

import scala.concurrent.Future

case class NounsData(nouns: List[String])
case class VerbsData(verbs: List[String])
case class TokenCountData(total: Int, nouns: Int, verbs: Int, stopWords: Int, specialCharacters: Int)
case class ExtractAllData(nouns: List[String], verbs: List[String], stopWords: List[String])
case class ExtractUniqueData(nouns: List[String], verbs: List[String], stopWords: List[String],specialCharacters: List[String])
case class OverAllData(tokenCount: TokenCountData, extractAll: ExtractAllData, extractUnique: ExtractUniqueData)

object NounsData {
  /**
    * Mapping to read/write a NounsData out as a JSON value.
    */
    implicit val format: Format[NounsData] = Json.format
}

object VerbsData {
  /**
    * Mapping to read/write a VerbsData out as a JSON value.
    */
    implicit val format: Format[VerbsData] = Json.format
}

object TokenCountData {
  /**
    * Mapping to read/write a TokenCountData out as a JSON value.
    */
    implicit val format: Format[TokenCountData] = Json.format
}

object ExtractAllData {
  /**
    * Mapping to read/write a ExtractAllData out as a JSON value.
    */
    implicit val format: Format[ExtractAllData] = Json.format
}

object ExtractUniqueData {
  /**
    * Mapping to read/write a ExtractUniqueData out as a JSON value.
    */
    implicit val format: Format[ExtractUniqueData] = Json.format
}

object OverAllData {
  /**
    * Mapping to read/write a OverAllData out as a JSON value.
    */
    implicit val format: Format[OverAllData] = Json.format
}

class TextParserExecutionContext @Inject()(actorSystem: ActorSystem)
    extends CustomExecutionContext(actorSystem, "repository.dispatcher")

/**
  * A pure non-blocking interface for the PostRepository.
  */
trait TextParserRepository {
  def getNouns(text: String)(implicit mc: MarkerContext): Future[NounsData]
  def getVerbs(text: String)(implicit mc: MarkerContext): Future[VerbsData]
  def getUniqueNouns(text: String)(implicit mc: MarkerContext): Future[NounsData]
  def getUniqueVerbs(text: String)(implicit mc: MarkerContext): Future[VerbsData]
  def analyze(text: String)(implicit mc: MarkerContext): Future[OverAllData]
}

/**
  * A trivial implementation for the Post Repository.
  *
  * A custom execution context is used here to establish that blocking operations should be
  * executed in a different thread than Play's ExecutionContext, which is used for CPU bound tasks
  * such as rendering.
  */
@Singleton
class TextParserRepositoryImpl @Inject()()(implicit ec: TextParserExecutionContext)
    extends TextParserRepository {

  private val logger = Logger(this.getClass)

  private val nounList = NounsData( List(
   "Nouns 1",
   "Nouns 2",
   "Nouns 3",
   "Nouns 4",
   "Nouns 5"
   ))
   private val verbList = VerbsData( List(
   "Verbs 1",
   "Verbs 2",
   "Verbs 3",
   "Verbs 4",
   "Verbs 5"
   ))

   private val tokenCountData = TokenCountData( 1,2,3,4,5)
   private val extractAllData = ExtractAllData( List("Nouns 1"),List("Verbs 1"),List("SW 1"))
   private val extractUniqueData = ExtractUniqueData( List("Nouns 1"),List("Verbs 1"),List("SW 1"),List("Spl 1"))
   private val overAllData = OverAllData( tokenCountData,extractAllData,extractUniqueData)

  override def getNouns(text: String)(
      implicit mc: MarkerContext): Future[NounsData] = {
    Future {
      logger.trace("getNouns")
      nounList
    }
  }

  override def getVerbs(text: String)(
      implicit mc: MarkerContext): Future[VerbsData] = {
    Future {
      logger.trace("getVerbs")
      verbList
    }
  }
  
  override def getUniqueVerbs(text: String)(
      implicit mc: MarkerContext): Future[VerbsData] = {
    Future {
      logger.trace("getUniqueVerbs")
      verbList
    }
  }
  
  override def getUniqueNouns(text: String)(
      implicit mc: MarkerContext): Future[NounsData] = {
    Future {
      logger.trace("getUniqueNouns")
      nounList
    }
  }
  
  override def analyze(text: String)(
      implicit mc: MarkerContext): Future[OverAllData] = {
    Future {
      logger.trace("analyze")
      overAllData
    }
  }

}
