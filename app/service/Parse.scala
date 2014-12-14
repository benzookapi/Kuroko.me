package service

import play.api.libs.concurrent.Akka
import akka.actor._
import akka.actor.Actor._
import org.slf4j.LoggerFactory
import play.Logger
import play.api.libs.ws._
import play.api.libs.json._
import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global

object Parse {

  val APP_ID = System.getenv("PARSE_APP_ID")

  val REST_KEY = System.getenv("PARSE_REST_KEY")

  def getFbAuth() = {
    WS.url("https://api.parse.com/1/classes/FbAuth").withHeaders(
      "X-Parse-Application-Id" -> APP_ID,
      "X-Parse-REST-API-Key" -> REST_KEY).get()
  }

  def getUserByToken(token: String) = {
    WS.url("https://api.parse.com/1/users/me").withHeaders(
      "X-Parse-Application-Id" -> APP_ID,
      "X-Parse-REST-API-Key" -> REST_KEY,
      "X-Parse-Session-Token" -> token).get()
  }
}