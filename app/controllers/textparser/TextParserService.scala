package controllers.textparser

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}
import play.api.libs.json._

import scala.concurrent.Future
import scala.collection.mutable.ListBuffer
import java.io.FileInputStream
import java.io.InputStream 
import java.io.IOException

import opennlp.tools.postag.POSModel; 
import opennlp.tools.postag.POSSample; 
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.TokenizerME; 
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.namefind.NameFinderME; 
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;

case class NounsData(nouns: List[String])
case class VerbsData(verbs: List[String])
case class NameAndLocation(persons: List[String], Locations: List[String])
case class TokenCountData(total: Int, nouns: Int, verbs: Int, stopWords: Int, specialCharacters: Int)
case class ExtractAllData(nouns: List[String], verbs: List[String], stopWords: Set[String])
case class ExtractUniqueData(nouns: List[String], verbs: List[String], stopWords: Set[String],specialCharacters: List[String])
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

object NameAndLocation {
  /**
    * Mapping to read/write a NameAndLocation out as a JSON value.
    */
    implicit val format: Format[NameAndLocation] = Json.format
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
  * A pure non-blocking interface for the TextParserService.
  */
trait TextParserService {
  def getNouns(text: String, IsUnique: Boolean = false)(implicit mc: MarkerContext): Future[NounsData]
  def getVerbs(text: String, IsUnique: Boolean = false)(implicit mc: MarkerContext): Future[VerbsData]
  def getNameAndLocation(text: String)(implicit mc: MarkerContext): Future[NameAndLocation]
  def analyze(text: String)(implicit mc: MarkerContext): Future[OverAllData]
}

/**
  * A trivial implementation for TextParser Service.
  *
  * A custom execution context is used here to establish that blocking operations should be
  * executed in a different thread than Play's ExecutionContext, which is used for CPU bound tasks
  * such as rendering.
  */
@Singleton
class TextParserServiceImpl @Inject()()(implicit ec: TextParserExecutionContext)
    extends TextParserService {

  private val logger = Logger(this.getClass)
  private var tokenizer : TokenizerME = _
  private var posTagger : POSTaggerME = _
  private var personFinder : NameFinderME = _
  private var locationFinder : NameFinderME = _
  var STOPWORDS : Set[String] = _
  
  def initializeModel() = {
	logger.trace("initializing Model")
	
	// Get Root path
	val currentDirectory = new java.io.File(".").getCanonicalPath
	
	// Tokenize the given text
	val tokenModel = new TokenizerModel(new FileInputStream(currentDirectory + "\\conf\\resource\\en-token.bin"))
	tokenizer = new TokenizerME(tokenModel)
	
	// Parts-Of-Speech Tagging 
	// loading the parts-of-speech model from stream 
	val posModel = new POSModel(new FileInputStream(currentDirectory + "\\conf\\resource\\en-pos-maxent.bin"))
	
	// initializing the parts-of-speech tagger with model 
	posTagger = new POSTaggerME(posModel)
	
	STOPWORDS = scala.io.Source.fromFile(currentDirectory + "\\conf\\resource\\stopwords.txt").getLines.flatMap(_.split("\\W+")).toSet
	
	//Loading the NER-person model    
	val personModel = new TokenNameFinderModel(new FileInputStream(currentDirectory + "\\conf\\resource\\en-ner-person.bin"))
	
	//Instantiating the Person NameFinderME class 
	personFinder = new NameFinderME(personModel);
	
	//Loading the NER-location moodel     
    val locationModel = new TokenNameFinderModel(new FileInputStream(currentDirectory + "\\conf\\resource\\en-ner-location.bin")); 
        
    //Instantiating the Location NameFinderME class 
    locationFinder = new NameFinderME(locationModel);  

  }
  initializeModel()

  override def getNouns(text: String, IsUnique: Boolean = false)(
      implicit mc: MarkerContext): Future[NounsData] = {
    Future {
	  if (!IsUnique)
	  {
		  logger.trace("Service - GetNouns")
		  NounsData(extractFeatures(text, true))
	  }
	  else
	  {
		  logger.trace("Service - GetNouns With Unique")
		  NounsData(extractFeatures(text, true).map(_.toLowerCase()).distinct)
	  }
    }
  }

  override def getVerbs(text: String, IsUnique: Boolean = false)(
      implicit mc: MarkerContext): Future[VerbsData] = {
    Future {
	  if (!IsUnique)
	  {
		  logger.trace("Service - GetVerbs")
		  VerbsData(extractFeatures(text, false))
	  }
	  else
	  {
		  logger.trace("Service - GetVerbs With Unique")
		  VerbsData(extractFeatures(text, false).map(_.toLowerCase()).distinct)
	  }
    }
  }

  override def getNameAndLocation(text: String)(
      implicit mc: MarkerContext): Future[NameAndLocation] = {
    Future {
		logger.trace("Service - Get Name & Location")
		
		// Tokenize the given text
        val tokens = tokenizer.tokenize(text)
	  
		//Finding the names in the sentence 
		val nameSpans: Array[Span] = personFinder.find(tokens)
		
		//Finding the locations in the sentence
		val locationSpans: Array[Span] = locationFinder.find(tokens)
		
		val persons = Span.spansToStrings(nameSpans, tokens)
		
		val locations = Span.spansToStrings(locationSpans, tokens)
		
		NameAndLocation(persons.toList.distinct, locations.toList.distinct)
    }
  }
 
  override def analyze(text: String)(
      implicit mc: MarkerContext): Future[OverAllData] = {
    Future {
      logger.trace("Service - Analyze")
	  var (nouns:ListBuffer[String], verbs:ListBuffer[String], stopWords:Set[String], splChars:ListBuffer[String], tokenCount:Int) = extractFeatures(text)
	  val uniqueNouns = nouns.map(_.toLowerCase()).distinct
	  val uniqueVerbs = verbs.map(_.toLowerCase()).distinct
      OverAllData(TokenCountData(tokenCount,nouns.length,verbs.length,stopWords.size,splChars.length),
	  ExtractAllData(nouns.toList,verbs.toList,stopWords), ExtractUniqueData(uniqueNouns.toList, uniqueVerbs.toList, stopWords, splChars.distinct.toList))
    }
  }

  def extractFeatures(text: String, IsNoun: Boolean): List[String] = {
	  // Tokenize the given text
      val tokens = tokenizer.tokenize(text)
	  // POS tagging the tokens 
	  val tags = posTagger.tag(tokens)
	  
	  var nouns = new ListBuffer[String]()
	  var verbs  = new ListBuffer[String]()

	  // Iterate through each token & POS
	  (tags.toList, tokens.toList).zipped.foreach{ (a,b) => 
	  if (a.startsWith("N"))
	  {
	  	if (b != "_") {
	  		nouns.append(b)
		}
	  }
	  else if (a.startsWith("V"))
	  {
	  	verbs.append(b)
	  }
	}
	  if (IsNoun)
		  nouns.toList
	  else
		  verbs.toList

	}
	def extractFeatures(text: String): (ListBuffer[String],ListBuffer[String],Set[String],ListBuffer[String],Int) = {
      // Tokenize the given text
      val tokens = tokenizer.tokenize(text)
	  // POS tagging the tokens 
	  val tags = posTagger.tag(tokens)
	  var nouns = new ListBuffer[String]()
	  var verbs  = new ListBuffer[String]()
	  var specialCharacters  = new ListBuffer[String]()
	  var stopWords:Set[String] = tokens.map(_.toLowerCase()).toSet.intersect(STOPWORDS)
	  // Iterate through each token & POS
	  val splcharPattern = "^[\\W]".r
	  (tags.toList, tokens.toList).zipped.foreach{ (a,b) => 
	  if (a.startsWith("N"))
	  {
	  	if (b != "_")
	  		nouns.append(b)
	  	else 
	  		specialCharacters.append(b)
	  }
	  else if (a.startsWith("V"))
	  	verbs.append(b)
	  else if (splcharPattern.findFirstIn(a) != None)
	  	specialCharacters.append(b)		
	  }
	  (nouns,verbs,stopWords,specialCharacters,tokens.length)
	}
	
	def dumpPosTagger(data:String): List[String] = {
	var result:List[String] = List("")
	if(data.isEmpty)
	"Data is empty"
	try {
		// tokenize the sentence
		// val projectPath = play.Environment.rootPath().getAbsolutePath()
		val tokens = tokenizer.tokenize(data) 

		// Tagger tagging the tokens 
		val tags = posTagger.tag(tokens) 
		// Getting the probabilities of the tags given to the tokens
		val probs = posTagger.probs 
		// Iterates the token, tag and probability score
		// Flattening to list makes into string
		result =(0 until tokens.length).map(i => tokens(i) + "->" + tags(i) + "->" + probs(i)).toList
	} 
	catch
	{
		case e: IOException => // Model loading failed, handle the error
		e.printStackTrace()
	}
	result
	}
}
