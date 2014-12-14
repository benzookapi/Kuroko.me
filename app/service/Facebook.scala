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

class Facebook extends Actor {

  def receive = {
    case msg: String => {
      Logger.debug(msg)
      getTimeLine()
    }
    case _ => {
      Logger.error("ERROR")
    }
  }

  def getTimeLine() {
    WS.url("https://api.parse.com/1/classes/FbAuth").withHeaders(
      "X-Parse-Application-Id" -> "J7dA73X0uR08brr2ebtRa2nMQrMFJaIRMgdfKjrB",
      "X-Parse-REST-API-Key" -> "J0iA0bh6gmSHaLdf93OsgDJgZNtkusPHQRWthKrQ").get().map { res =>
        (res.json \ "results").as[List[JsValue]].map { r =>
          val id = (r \ "userId").as[String]
          val token = (r \ "auth").as[String]
          WS.url("https://graph.facebook.com/me/home").withQueryString(
            "access_token" -> token, "limit" -> "1000").get().map { res =>
              (res.json \ "data").as[List[JsValue]].map { d =>
                val key = (d \ "id").as[String]
                Elasticsearch.insertDocument(id, "facebook", key, d)
              }
            }
        }
      }
  }
}