package models.builders

import play.api.libs.ws.Response
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.Future
import play.api.mvc.{AsyncResult, Results, Result}
import play.api.libs.json.Json
import play.api.http.Status
import views.html.userpayment
import models.UserPaymentView

class UserPaymentResultBuilder {

  val userViewBuilder = new UserViewBuilder

  val paymentViewBuilder = new PaymentViewBuilder

  def build(responses:(Future[Response],Future[Response])) : Result = {
    AsyncResult {
      for {
        user <- responses._1.map { response => {
          response.status match {
            case Status.OK => userViewBuilder.userView(Json.parse(response.body))
            case _ => throw new IllegalStateException
          }
        }}
        payment <- responses._2.map { response => {
          response.status match {
            case Status.OK => Some(paymentViewBuilder.paymentView(Json.parse(response.body)))
            case _ => None
          }
        }}
      } yield (
        if (payment.isDefined) {
          Results.Ok(userpayment(UserPaymentView(user, payment)))
        } else {
          Results.Ok(userpayment(UserPaymentView(user, None)))
        }
      )
    }
  }
}
