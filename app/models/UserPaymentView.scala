package models

case class UserPaymentView(
  userView:UserView,
  paymentView:Option[PaymentView]
)
