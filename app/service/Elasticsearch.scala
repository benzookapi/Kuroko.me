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

object Elasticsearch {

  private def convertId(id: String) = id.toLowerCase()

  val baseUrl = "http://cherry-4560338.us-east-1.bonsai.io/"

  val user = "ucg9i531"

  val password = "ecn6ftlx3irseki4"

  def createIndex(id: String) = {
    val url = baseUrl + convertId(id)
    WS.url(url).withHeaders("ContentType" -> "application/json").
      withAuth(user, password, WSAuthScheme.BASIC).post("""
{"ok":true,"acknowledged":true}
""").map { res =>
        println(url)
        println(res.body)
        res.json
      }
  }

  def insertDocument(id: String, service: String, key: String, json: JsValue) = {
    val url = baseUrl + convertId(id) + "/" + service + "/" + key
    WS.url(url).withHeaders("ContentType" -> "application/json").
      withAuth(user, password, WSAuthScheme.BASIC).post(json).map { res =>
        println(url)
        println(res.body)
        res.json
      }
  }

  def search(token: String, service: String, query: String) = {

    WS.url("https://api.parse.com/1/users/me").withHeaders(
      "X-Parse-Application-Id" -> "J7dA73X0uR08brr2ebtRa2nMQrMFJaIRMgdfKjrB",
      "X-Parse-REST-API-Key" -> "J0iA0bh6gmSHaLdf93OsgDJgZNtkusPHQRWthKrQ",
      "X-Parse-Session-Token" -> token).get().flatMap { res =>
        val id = (res.json \ "objectId").as[String]
        val url = baseUrl + convertId(id) + "/" + service + "/_search"
        println(url)
        WS.url(url).withQueryString("q" -> query).
          withAuth(user, password, WSAuthScheme.BASIC).get().map { res =>
            println(res.body)
            val str = "[" + (res.json \ "hits" \ "hits").as[List[JsValue]].map { d =>
              val fbId = (d \ "_id").as[String]
              val title = ((d \ "_source" \ "description").asOpt[String] match {
                case Some(s) => s
                case _ => (d \ "_source" \ "message").asOpt[String] match {
                  case Some(s) => s
                  case _ => ""
                }
              }).replaceAll("\"", "").replaceAll("\n", "")
              val img = (d \ "_source" \ "picture").asOpt[String] match {
                case Some(s) => s
                case _ => ""
              }
              "{" +
                " link : \"https://www.facebook.com/" + fbId.replace("_", "/posts/") + "\", " +
                " title : \"" + title + "\", " +
                " img : \"" + img + "\" " +
                "}"
            }.mkString(",") + "]"
            str
          }

      }

  }

}