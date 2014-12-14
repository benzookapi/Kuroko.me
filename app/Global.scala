

import akka.actor._
import scala.concurrent.duration._
import play.api._
import play.api.libs.json._
import play.api.libs.concurrent._
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._
import service.FacebookActor
import play.Logger

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    val actor = Akka.system.actorOf(Props(new FacebookActor), "facebook")
    Akka.system.scheduler.schedule(0.milliseconds, 10.minutes, actor, "check")
  }
}