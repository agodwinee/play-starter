package models

import java.net.URI

case class UserView (name:String, regimes:Map[String, URI])

object UserView {}
