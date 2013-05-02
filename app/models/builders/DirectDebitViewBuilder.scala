package models.builders

import play.api.libs.json.JsValue
import models.DirectDebitView
import org.json4s.jackson.JsonMethods._
import org.json4s.{CustomSerializer, DefaultFormats}
import org.json4s.JsonAST._

class DirectDebitViewBuilder {

  private implicit val formats = DefaultFormats

  def directDebitView(json: JsValue) : Option[DirectDebitView] = {
    val js = parse(json.toString())
    val directDebit = js \ "directDebit"
    if (directDebit.isInstanceOf[JObject]) {
        Some(DirectDebitView(
          (js \\ "period").extract[String],
          ((directDebit \ "depository" \ "name").extract[String] + ", " +
            (directDebit \ "depository" \ "address" \\ classOf[JString]).mkString(", ")),
          (directDebit \ "accountNumber").extract[String],
          (directDebit \ "sortCode").extract[String],
          (directDebit \ "referenceNumber").extract[String]
        ))
    } else {
      None
    }
  }
}

case class Address(
  lineOne:String,
  lineTwo:String,
  lineThree:String,
  lineFour:String,
  lineFive:String,
  postcode:String,
  country:String
)

class AddressSerializer extends CustomSerializer[Address] ( format => (
  {
    case JObject(JField("lineOne", JString(one)) :: JField("lineTwo", JString(two)) :: JField("lineThree", JString(three)) ::
      JField("lineFour", JString(four)) :: JField("lineFive", JString(five)) :: JField("postcode", JString(pc)) ::
      JField("country", JString(co)) :: Nil) =>
        new Address(one, two, three, four, five, pc, co)
  },
  {
    case x: Address =>
      JObject()
  }
))
