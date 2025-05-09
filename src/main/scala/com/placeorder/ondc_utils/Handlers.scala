package com.placeorder.ondc_utils
import zio._
import zio.http.template.Dom
import zio.telemetry.opentelemetry.tracing.Tracing
import io.opentelemetry.api.trace.{Span, SpanKind}
import io.opentelemetry.api.GlobalOpenTelemetry
import zio.http.Status



object MainHandlers {
  val healthCheckRequest: UIO[Dom] = ZIO.succeed(Dom.text("Running Server"))

  def fetchURLRequest(body: FetchURLBody): ZIO[Tracing & AppConfig, GenericError, GenericSuccess[Map[String, String]]] = {
    for {
      config <- ZIO.service[AppConfig]
      tracing <- ZIO.service[Tracing]
      response <- tracing.span("fetch-url-request", SpanKind.SERVER) {
        for {
          _ <- tracing.addEvent("Processing Fetch URL Request")
          _ <- ZIO.logInfo(s"Fetch URL Request: $body")
          response <- body.id match {
            case None =>
              ZIO.succeed(config.urlMapping.view.mapValues(_.url).toMap)
            case Some(id) =>
              ZIO.fromOption(config.urlMapping.get(id.toString))
                .map(urlMapping => Map(id.toString -> urlMapping.url))
                .orElseFail(GenericError.DataNotFound(customerMessage = "No data found"))
          }
          _ <- tracing.addEvent(s"Response: $response")
          _ <- ZIO.logInfo(s"Fetch URL Response: $response") // Log response
          _ <- tracing.addEvent("Finished Processing Fetch URL Request")
        } yield response
      }
    } yield GenericSuccess.SuccessResponse(customerMessage="Successfully fetched data", data= Some(response))
  }
}