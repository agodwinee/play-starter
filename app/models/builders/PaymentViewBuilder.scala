package models.builders

import play.api.libs.json._
import play.api.libs.functional.syntax._
import models.{PaymentEvent, PaymentView}

class PaymentViewBuilder {

  def paymentView(json: JsValue) : PaymentView = {
    implicit val paymentReads = (
      (__ \ "amount").read[BigDecimal] and
      (__ \ "dueTimestamp").read[Long] and
      (__ \ "paymentType").read[String]
    )(PaymentEvent.apply _)

//    implicit val paymentReads = Json.reads[PaymentEvent]

    val paymentEvents = for {
      payment <- (json \ "payments").as[List[JsObject]]
      paymentEvent = payment.validate[PaymentEvent].get
    } yield paymentEvent

    PaymentView(paymentEvents, (json \ "period").as[String], (json \ "createdDate").as[Long], (json \ "amountOwed").as[BigDecimal])
  }
}
