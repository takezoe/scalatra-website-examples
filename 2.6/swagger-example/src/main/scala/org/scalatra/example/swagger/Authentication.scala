package org.scalatra.example.swagger

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import org.scalatra.{CorsSupport, ScalatraBase}
import org.scalatra.auth.{ScentryConfig, ScentrySupport}
import org.scalatra.auth.strategy.BasicAuthStrategy
import org.scalatra.json.JsonSupport
import org.scalatra.swagger.SwaggerAuthBase

case class User(id: String)

class OurBasicAuthStrategy(protected override val app: ScalatraBase, realm: String)
  extends BasicAuthStrategy[User](app, realm) {

  protected def validate(userName: String, password: String)(implicit request: HttpServletRequest, response: HttpServletResponse): Option[User] = {
    if(userName == "scalatra" && password == "scalatra") Some(User("scalatra"))
    else None
  }

  protected def getUserId(user: User)(implicit request: HttpServletRequest, response: HttpServletResponse): String = user.id

}


trait OurSwaggerAuthBase extends SwaggerAuthBase[User] with ScentrySupport[User] {
  self: JsonSupport[_] with CorsSupport =>
  val realm = "Scalatra Basic Auth Example"

  protected def fromSession = { case id: String => User(id)  }
  protected def toSession   = { case usr: User => usr.id }
  protected val scentryConfig = (new ScentryConfig {}).asInstanceOf[ScentryConfiguration]

  override protected def userManifest: Manifest[User] = manifest[User]

  override protected def configureScentry = {
    scentry.unauthenticated {
      scentry.strategies("Basic").unauthenticated()
    }
  }

  override protected def registerAuthStrategies = {
    scentry.register("Basic", app => new OurBasicAuthStrategy(app, realm))
  }
}
