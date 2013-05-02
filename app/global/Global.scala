package global

import play.api.GlobalSettings

object Global extends GlobalSettings {

  override def getControllerInstance[A](controllerClass: Class[A]): A = {
    super.getControllerInstance(controllerClass)
  }
}
