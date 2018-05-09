package org.scalatra.example.swagger

import org.scalatra._

// Swagger-specific Scalatra imports
import org.scalatra.swagger._
import org.scalatra.swagger.ResponseMessage

// JSON handling support from Scalatra
import org.scalatra.json._

class FlowersController(implicit val swagger: SwaggerWithAuth) extends ScalatraServlet with OurSwaggerAuthBase with SwaggerAuthSupport[User] with NativeJsonSupport with CorsSupport {


  // A description of our application. This will show up in the Swagger docs.
  protected val applicationDescription = "The flowershop API. It exposes operations for browsing and searching lists of flowers"

  // Before every action runs, set the content type to be in JSON format.
  before() {
    contentType = formats("json")
  }

  val getFlowers =
    (apiOperation[List[Flower]]("getFlowers")
      summary "Show all flowers"
      tags("Flowers")
      notes "Shows all the flowers in the flower shop. You can search it too."
      parameter queryParam[Option[String]]("name").description("A name to search for")
    ).allows(_.exists(_.id == "scalatra"))

  get("/", operation(getFlowers)){
    params.get("name") match {
      case Some(name) => FlowerData.all filter (_.name.toLowerCase contains name.toLowerCase())
      case None => FlowerData.all
    }
  }


  val findBySlug =
    (apiOperation[Flower]("findBySlug")
      summary "Find by a flower by its slug"
      tags("Flowers")
      parameters (
      pathParam[String]("slug").description("Slug of flower that needs to be fetched"))
      responseMessage ResponseMessage(404, "Slug Not Found")
    ).allows(_.exists(_.id == "scalatra"))

  get("/:slug", operation(findBySlug)) {
    FlowerData.all find (_.slug == params("slug")) match {
      case Some(b) => b
      case None => halt(404)
    }
  }
}


// A Flower object to use as a data model
case class Flower(slug: String, name: String)

// An amazing datastore!
object FlowerData {

  /**
   * Some fake flowers data so we can simulate retrievals.
   */
  var all = List(
    Flower("yellow-tulip", "Yellow Tulip"),
    Flower("red-rose", "Red Rose"),
    Flower("black-rose", "Black Rose"))
}
