package services

import java.net.URI
import scala.concurrent.Future
import play.api.libs.ws.{WS, Response}
import play.api.libs.json.{Json, JsValue}

class ServiceClient(host:String, port:Int) {

  def get(uri: URI) : Future[Response] = WS.url(formatUrl(uri.toString)).get

  def post(uri: URI, json: JsValue) : Future[Response] = WS.url(formatUrl(uri.toString)).post(json)

  def post(uri: URI, form: Map[String, Seq[String]]) : Future[Response] = WS.url(formatUrl(uri.toString)).post(form)

  private def formatUrl(path:String) : String = "http://" + host + ":" + port + path

}

class UserServiceClient(host:String, port:Int) extends ServiceClient(host, port) {

  def getUser(pid: String) : Future[Response] = get(URI.create("/user/pid/" + pid))

  def getPaymentSchedule(pid: String) : Future[Response] = get(URI.create("/payment/schedule/user/pid/" + pid))
}
