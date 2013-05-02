import java.net.URI
import org.apache.commons.io.IOUtils
import org.specs2.mutable.Specification
import play.api.libs.json.{JsObject, JsValue, Json}

class JsonSpec extends Specification {

  "Deserialiser" should {

    "retrieve Fred's name" in {
      val jsonString = IOUtils.toString(getClass.getClassLoader.getResourceAsStream("test.json"), "UTF-8")
      System.out.println(jsonString)
      val deserialiser : Deserialiser = new Deserialiser
      val thingy = deserialiser.deserialise(jsonString)
      thingy.data.get("name") mustEqual(Some("Fred"))
      thingy.data.get("age") mustEqual(None)
    }
  }
}

case class Thingy(data : Map[String, String])

class Deserialiser {

  def deserialise(jsonString : String) : Thingy = {
    val json : JsValue = Json.parse(jsonString)
    val address : JsObject = (json \ "address").as[JsObject]
    val foo : Map[String, JsValue] = address.fields.toMap

    val paths : JsObject = (json \ "regimes").as[JsObject]
    val kippers = for {
      path <- paths.fields
      uri = URI.create((path._2 \ "user").as[String].replaceAll("\"", ""))
    } yield (path._1, uri)

    val kippersMap = kippers.toMap

    System.out.println("Kippers:" + kippersMap)

    address.fields.foreach( key => {
        key match {
          case ("line1", t) =>
            System.out.println("First line " + t)
          case _ =>
            System.out.println("Not the first line")
        }
      }
    )
    Thingy(Map("name" -> (json \ "name").as[String]))
  }
}
