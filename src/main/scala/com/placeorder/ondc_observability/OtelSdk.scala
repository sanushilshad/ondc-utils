package com.placeorder.ondc_observability


import zio.{TaskLayer, ZIO}
import zio.telemetry.opentelemetry.OpenTelemetry
import io.opentelemetry.sdk.OpenTelemetrySdk
import io.opentelemetry.api

object OtelSdk {

  def custom(resourceName: String, tracingEndPoint: String): TaskLayer[api.OpenTelemetry] =
    OpenTelemetry.custom(
      for {
        tracerProvider <- TracerProvider.jaeger(resourceName, tracingEndPoint)
        // meterProvider  <- MeterProvider.stdout(resourceName)
        loggerProvider <- LoggerProvider.stdout(resourceName)
        // tracer = tracerProvider.get("com.placeorder.ondc_observability") // Get a tracer
        // _ <- ZIO.attempt {
        //        val span = tracer.spanBuilder("initialization-warning").startSpan()
        //        try {
        //          span.addEvent("Warning: Custom OpenTelemetry initialization started.")
        //        } finally {
        //          span.end()
        //        }
        //      }.orDie // Ensure the warning is logged, even if an error occurs
        openTelemetry  <- ZIO.fromAutoCloseable(
                            ZIO.succeed(
                              OpenTelemetrySdk
                                .builder()
                                .setTracerProvider(tracerProvider)
                                // .setMeterProvider(meterProvider)
                                .setLoggerProvider(loggerProvider)
                                .build
                            )
                          )
      } yield openTelemetry
    )

}