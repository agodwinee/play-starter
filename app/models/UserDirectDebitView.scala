package models

case class DirectDebitView(
  period:String,
  depository:String,
  accountNumber:String,
  sortCode:String,
  reference:String
)

case class UserDirectDebitView(
  user:UserView,
  directDebit:Option[DirectDebitView]
)
