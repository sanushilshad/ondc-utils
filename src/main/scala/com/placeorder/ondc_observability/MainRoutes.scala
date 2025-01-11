package com.placeorder.ondc_observability

import zio._
import zio.http._
import zio.http.codec.PathCodec.path
import zio.http.codec._
import zio.http.endpoint._
import zio.schema._
import zio.schema.annotation._
import zio.http.endpoint.openapi._
import zio.http.template.Dom
import zio.schema.validation.Validation
import zio.http.endpoint.openapi.JsonSchema.EnumValue.Str


final case class UserParams(city: String, @validate(Validation.greaterThan(17)) age: Int)

object UserParams {
    implicit val schema: Schema[UserParams] = DeriveSchema.gen[UserParams]
}


object MainRoutes {
    def apply(): Routes[Any, Response] = {

        val endpoint = Endpoint(Method.GET / "health_check").out[String] 
        val openApi = OpenAPIGen.fromEndpoints("Observability API", "1.0.0", endpoint)
        val routes = endpoint.implement { case _ =>
             MainHandler.healthCheckRequest
        }.toRoutes ++ SwaggerUI.routes( "/docs", openApi)

        routes
    }


}
