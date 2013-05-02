package controllers

import play.api.mvc.{Result, Action, Controller}
import play.api.libs.concurrent.Execution.Implicits._
import services.UserServiceClient
import views.html.{user, payments, userpayment}
import play.api.libs.json.Json
import models.builders.{UserPaymentResultBuilder, DirectDebitViewBuilder, UserViewBuilder, PaymentViewBuilder}
import java.util.concurrent.TimeoutException
import models.{DirectDebitView, PaymentView, UserView, UserDirectDebitView, UserPaymentView}
import scala.concurrent.Future
import play.api.libs.ws.{WS, Response}
import org.apache.commons.lang3.builder.ReflectionToStringBuilder

object Payment extends Controller {

  val userServiceClient = new UserServiceClient("localhost", 8400)

  val paymentViewBuilder = new PaymentViewBuilder

  val directDebitViewBuilder = new DirectDebitViewBuilder

  val userViewBuilder = new UserViewBuilder

  val userPaymentResultBuilder = new UserPaymentResultBuilder

  def getPaymentSchedule(pid:String) = Action {
    Async {
        userServiceClient.getPaymentSchedule(pid) map { response => {
          response.status match {
            case OK => {
              Ok(payments(paymentViewBuilder.paymentView(Json.parse(response.body))))
            }
            case _ => {
              NoContent
            }
          }
        }} recover {
          case t: TimeoutException =>
            RequestTimeout(t.getMessage)
          case e =>
            ServiceUnavailable(e.getMessage)
        }
    }
  }

//  def getUserPayment(pid:String) = Action {
//    Async {
//      val views = getUserPaymentFromService(pid)
//      for {
//        user <- views._1
//        payment <- views._2
//      } yield (
//        Ok(userpayment(UserPaymentView(user, payment)))
//      )
//    }
//  }

  def getUserPayment(pid:String) = Action {
    userPaymentResultBuilder.build((userServiceClient.getUser(pid), userServiceClient.getPaymentSchedule(pid)))
  }

  def getUserPaymentFromService(pid:String) : (Future[UserView], Future[Option[PaymentView]]) = {
    val responses = getUserPaymentResult(pid)

    println(ReflectionToStringBuilder.toString(WS.client.getConfig))

      val userFuture = responses._1.map { response => {
        response.status match {
          case OK => userViewBuilder.userView(Json.parse(response.body))
          case _ => throw new IllegalStateException
        }
      }}
    val paymentFuture = responses._2.map { response => {
      response.status match {
        case OK => Some(paymentViewBuilder.paymentView(Json.parse(response.body)))
        case _ => None
      }
    }}

    (userFuture, paymentFuture)
  }

  def getUserDirectDebitFromService(pid:String) : (Future[UserView], Future[Option[DirectDebitView]]) = {
    val responses = getUserPaymentResult(pid)

    val userFuture = responses._1.map { response => {
      response.status match {
        case OK => userViewBuilder.userView(Json.parse(response.body))
        case _ => throw new IllegalStateException
      }
    }}
    val directDebitFuture = responses._2.map { response => {
      response.status match {
        case OK => directDebitViewBuilder.directDebitView(Json.parse(response.body))
        case _ => None
      }
    }}

    (userFuture, directDebitFuture)
  }

  @inline def getUserPaymentResult(pid:String) : (Future[Response], Future[Response]) =
    (userServiceClient.getUser(pid), userServiceClient.getPaymentSchedule(pid))
}

