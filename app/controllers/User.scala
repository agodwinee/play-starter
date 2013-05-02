package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits._
import models.builders.UserViewBuilder
import views.html.user
import services.UserServiceClient

object User extends Controller {

  val userViewBuilder = new UserViewBuilder

  val userServiceClient = new UserServiceClient("localhost", 8500)

  def getUser(pid: String) = Action {
    Async {
      userServiceClient.getUser(pid).map { response => {
        Ok(user(userViewBuilder.userView(Json.parse(response.body))))
      }}
    }
  }
}

