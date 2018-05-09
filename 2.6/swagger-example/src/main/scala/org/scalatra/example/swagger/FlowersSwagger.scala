package org.scalatra.example.swagger

import org.scalatra.{CorsSupport, ScalatraServlet}
import org.scalatra.json.NativeJsonSupport
import org.scalatra.swagger._

class ResourcesApp(implicit val swagger: SwaggerWithAuth) extends ScalatraServlet with OurSwaggerAuthBase with NativeJsonSupport with CorsSupport

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
