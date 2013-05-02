package models.builders

import play.api.libs.json.{Json, JsValue, JsObject}
import models.UserView
import java.net.URI
import play.api.libs.ws.Response

class UserViewBuilder {

  def userView(json : JsValue) : UserView = {
    val regimes = for {
      path <- (json \ "regimes").as[JsObject].fields
      uri = URI.create((path._2 \ "user").as[String].replaceAll("\"", ""))
    } yield (path._1, uri)

    UserView(
      (json \ "name" \ "firstName").as[String] + " " + (json \ "name" \ "lastName").as[String],
      regimes.toMap
    )
  }

  def extractUserView(response:Response) : Response => UserView = {
    response => {
      userView(Json.parse(response.body))
    }
  }
}
