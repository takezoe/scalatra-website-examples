package org.scalatra.example.swagger

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import org.scalatra.auth.{ScentryConfig, ScentrySupport}
import org.scalatra.{CorsSupport, NotFound, ScalatraBase, ScalatraServlet}
import org.scalatra.auth.strategy.{BasicAuthStrategy, BasicAuthSupport}
import org.scalatra.json.NativeJsonSupport
import org.scalatra.swagger._

case class User(id: String)

class OurBasicAuthStrategy(protected override val app: ScalatraBase, realm: String)
  extends BasicAuthStrategy[User](app, realm) {

  protected def validate(userName: String, password: String)(implicit request: HttpServletRequest, response: HttpServletResponse): Option[User] = {
    if(userName == "scalatra" && password == "scalatra") Some(User("scalatra"))
    else None
  }

  protected def getUserId(user: User)(implicit request: HttpServletRequest, response: HttpServletResponse): String = user.id

}

class ResourcesApp(implicit val swagger: SwaggerWithAuth) extends ScalatraServlet with NativeJsonSupport
          with SwaggerAuthBase[User] with ScentrySupport[User] with BasicAuthSupport[User] with CorsSupport {
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

//  override def initialize(config: ConfigT): Unit = {
//    super.initialize(config)
//    if (swagger.swaggerVersion.startsWith("2.")) {
//      get("/swagger.json") {
//        val docs = filterDocs(swagger.docs)
//        if (docs.isEmpty) halt(NotFound())
//        renderSwagger2(docs.asInstanceOf[List[ApiType]])
//      }
//    } else {
//      get("/:doc(.:format)") {
//        def isAllowed(doc: AuthApi[AnyRef]) = doc.apis.exists(_.operations.exists(_.allows(userOption)))
//
//        swagger.doc(params("doc")) match {
//          case Some(doc) if isAllowed(doc) ⇒ renderDoc(filterDoc(doc).asInstanceOf[ApiType])
//          case _ ⇒ NotFound()
//        }
//      }
//
//      get("/(" + indexRoute + "(.:format))") {
//        val docs = filterDocs(swagger.docs)
//        if (docs.isEmpty) halt(NotFound())
//        renderIndex(docs.asInstanceOf[List[ApiType]])
//      }
//    }
//  }
}

object FlowersSwagger{
  val Info = ApiInfo(
    "The Flowershop API",
    "Docs for the Flowers API",
    "http://scalatra.org",
    "apiteam@scalatra.org",
    "MIT",
    "http://opensource.org/licenses/MIT")
}

class FlowersSwagger extends SwaggerWithAuth(Swagger.SpecVersion, "1", FlowersSwagger.Info)
