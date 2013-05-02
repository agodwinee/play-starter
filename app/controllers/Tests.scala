package controllers

import play.api.mvc.{Result, Results, Action, Controller}
import play.api.libs.concurrent.Execution.Implicits._
import services.UserServiceClient
import scala.concurrent.Future
import play.api.libs.ws.Response
import play.api.libs.json.Json
import models.builders.{UserViewBuilder, PaymentViewBuilder}
import models.{UserPaymentView, UserView, PaymentView}
import views.html.user
import views.html.userpayment

object Tests extends Controller {

  val userServiceClient = new UserServiceClient("localhost", 8500)

  val paymentViewBuilder = new PaymentViewBuilder

  val userViewBuilder = new UserViewBuilder

  def getUser(pid:String) = Action {
    Async {
      async(userServiceClient.getUser(pid), responseToUser, userToResult)
    }
  }

  def getUserPayment(pid:String) = Action {
    Async {
      async((userServiceClient.getUser(pid), userServiceClient.getPaymentSchedule(pid)),
        responseToUser,
        responseToPayment,
        userPaymentToResult)
    }
  }

  def responseToUser(response: Response) : UserView = {
    response.status match {
      case OK => userViewBuilder.userView(Json.parse(response.body))
      case _ => throw new IllegalStateException
    }
  }

  def responseToPayment(response: Response) : Option[PaymentView] = {
    response.status match {
      case OK => Some(paymentViewBuilder.paymentView(Json.parse(response.body)))
      case _ => None
    }
  }

  def userToResult(future : Future[UserView]) : Future[Result] = {
    for {
      userView <- future
    } yield (
      Ok(user(userView))
    )
  }

  def userPaymentToResult(futureUser:Future[UserView], futurePayment:Future[Option[PaymentView]]) : Future[Result] = {
    for {
      userView <- futureUser
      paymentView <- futurePayment
    } yield (
      Ok(userpayment(UserPaymentView(userView,paymentView)))
    )
  }

  def async() : Future[Result] = {
    Future(Ok)
  }

  def async[A](response:Future[Response],
               f:Response => A,
               r:Future[A] => Future[Result]) : Future[Result] = {
    r(response.map (f))
  }

  def async[A,B](responses:(Future[Response],Future[Response]),
                 f1:Response => A, f2:Response => B,
                 r:(Future[A],Future[B]) => Future[Result]) : Future[Result] = {
    r(responses._1.map (f1), responses._2.map (f2))
  }

  def async[A,B,C](responses:(Future[Response],Future[Response],Future[Response]),
                   f1:Response => A, f2:Response => B, f3:Response => C,
                   r:(Future[A],Future[B],Future[C]) => Future[Result]) : Future[Result] = {
    r(responses._1.map (f1), responses._2.map (f2), responses._3.map (f3))
  }
}
