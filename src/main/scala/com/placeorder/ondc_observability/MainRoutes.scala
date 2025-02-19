package com.placeorder.ondc_observability

import zio._
import zio.http.{Routes,Response, Method}
import zio.http.codec.PathCodec.path
import zio.http.codec.Doc
import zio.http.endpoint.{ Endpoint}
import zio.http.endpoint.openapi.{OpenAPIGen, SwaggerUI}
import zio.http.template.Dom

import zio.telemetry.opentelemetry.tracing.Tracing
import zio.telemetry.opentelemetry.baggage.Baggage
import zio.telemetry.opentelemetry.baggage.propagation.BaggagePropagator
import zio.http.codec.HttpCodec
import zio.http.Status
import zio.http.codec.HttpCodecError
import zio.http.Status.ClientError
import zio.json.EncoderOps



object MainRoutes {

    def apply(): Routes[Tracing & Baggage & AppConfig,  Response] = {

        val healthCheckEndpoint = Endpoint(Method.GET / "health_check").out[Dom](Doc.p("Successful execution")) ?? Doc.p("API for checking the status of the server")
        
        val ObsProducerPoint = Endpoint(Method.POST / "send")
        .in[ObsProducerBody]
        .out[GenericResponse[Unit]](Doc.p("Successful execution"))
        .outErrors(
            HttpCodec.error[CustomError](Status.InternalServerError)?? Doc.p("Internal Server Error"),
            HttpCodec.error[CustomError](Status.BadRequest) ?? Doc.p("Bad Request"),
        ) ?? Doc.p("API for sending observability asynchronously")


        
        val openApi = OpenAPIGen.fromEndpoints("Observability API", "1.0.0", healthCheckEndpoint, ObsProducerPoint)

        val routes = healthCheckEndpoint.implement { case _ =>
            MainHandlers.healthCheckRequest
        }.toRoutes ++
        ObsProducerPoint.implement { 
            case body  =>
                MainHandlers.ObsProducerRequest(body)
                // .mapError {
            //   case CustomError(errorMessage) =>
            //     // Return the appropriate GenericResponse on error
            //     GenericResponse.error("Internal Server Error", Status.BadRequest)
            // }


        }.toRoutes ++
        SwaggerUI.routes("/docs", openApi)

        routes
    }
}
