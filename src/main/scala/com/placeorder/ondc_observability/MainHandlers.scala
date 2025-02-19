package com.placeorder.ondc_observability
import zio._
import zio.http.template.Dom
import zio.telemetry.opentelemetry.tracing.Tracing
import io.opentelemetry.api.trace.{Span, SpanKind}
import io.opentelemetry.api.GlobalOpenTelemetry




object MainHandlers {
  val healthCheckRequest: UIO[Dom] =  ZIO.succeed(Dom.text("Running Server"))

  def ObsProducerRequest(body: ObsProducerBody): ZIO[Tracing & AppConfig, CustomError, GenericResponse[Unit]] = {
    for {
  
      _ <- ZIO.serviceWithZIO[AppConfig] { config =>
        ZIO.succeed(println(s"Finished Processing Hello Request on ports: ${config.port}"))
      }

      response <- ZIO.serviceWithZIO[Tracing] { tracing =>
        tracing.span("hello-request", SpanKind.SERVER) {
          for {
            _   <- tracing.addEvent("Processing Hello Request")
            res <- ZIO.succeed("Hello, tracing with Jaeger!")
            _   <- tracing.addEvent("Finished Processing Hello Request")
          } yield res
        }
      }
    } yield GenericResponse.success(None)
  }


}
