package controllers

import play.api._
import play.api.mvc._
import play.api.libs.ws._
import service._
import play.api.libs.concurrent.Execution.Implicits.defaultContext


object Application extends Controller {

  def index = Action {
    Ok(views.html.index.render)
  }

  def start = Action { req =>
    req.body.asFormUrlEncoded match {
      case None => BadRequest("ERROR")
      case Some(data) => {
        Elasticsearch.createIndex(data("id")(0))
        Ok("OK")
      }
    }
  }

  def search(token: String, query: String) = Action.async {
    Elasticsearch.search(token, "facebook", query).map { res =>
     
        Ok(res)
      
    }

  }

}