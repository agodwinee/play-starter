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
      async(userServiceClient.getUser(pid),
        responseToUser,
        userServiceClient.getPaymentSchedule(pid),
        responseToPayment,
        userPaymentToResult
      )
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
               extractor:Response => A,
               aggregator:Future[A] => Future[Result]) : Future[Result] = {
    aggregator(response.map (extractor))
  }

  def async[A,B](responseA:Future[Response], extractorA:Response => A,
                 responseB:Future[Response], extractorB:Response => B,
                 aggregator:(Future[A],Future[B]) => Future[Result]) : Future[Result] = {
    aggregator(responseA.map (extractorA), responseB.map (extractorB))
  }

  def async[A,B,C](responseA:Future[Response], extractorA:Response => A,
                   responseB:Future[Response], extractorB:Response => B,
                   responseC:Future[Response], extractorC:Response => C,
                   aggregator:(Future[A],Future[B],Future[C]) => Future[Result]) : Future[Result] = {
    aggregator(responseA.map (extractorA), responseB.map (extractorB), responseC.map (extractorC))
  }
}
