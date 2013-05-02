package models

import play.api.libs.json.Json

abstract class TaxEvent(a:BigDecimal,d:Long,e:String) {
  def amount:BigDecimal = a
  def due:Long = d
  def eventType:String = e
}

case class PaymentEvent(amount:BigDecimal,dueTimestamp:Long,paymentType:String)
//  extends TaxEvent(amount,dueTimestamp,paymentType)

//object PaymentEvent{
//  implicit val paymentFormat = Json.format[PaymentEvent]
//}

case class FilingEvent(amount:BigDecimal,dueTimestamp:Long,filingType:String)
//  extends TaxEvent(amount,dueTimestamp,filingType)

//object FilingEvent{
//  implicit val filingFormat = Json.format[FilingEvent]
//}

case class PaymentView(
  payments:Seq[PaymentEvent],
  period:String,
  created:Long,
  amountOwed:BigDecimal
)


