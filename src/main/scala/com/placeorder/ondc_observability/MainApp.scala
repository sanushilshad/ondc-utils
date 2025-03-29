package com.placeorder.ondc_utils

import zio._
import zio.http.Server
import zio.telemetry.opentelemetry.OpenTelemetry
import zio.telemetry.opentelemetry.tracing.Tracing
import com.placeorder.ondc_utils.Config.loadConfig
import zio.telemetry.opentelemetry.baggage.Baggage
import zio.telemetry.opentelemetry.baggage.propagation.BaggagePropagator

object MainApp extends ZIOAppDefault {

  private val instrumentationScopeName = "com.placeorder.ondc_utils"
  def run =
    loadConfig().flatMap { appConfig =>
      val appLayer = ZLayer.succeed(appConfig)
      Server
        .serve(
          MainRoutes()
        )
        .provide(
          OtelSdk.custom(appConfig.otelServiceName, appConfig.otelExporterTracesEndpoint),
          OpenTelemetry.tracing(instrumentationScopeName),
          OpenTelemetry.logging(instrumentationScopeName),
          OpenTelemetry.contextZIO,
          Server.defaultWithPort(appConfig.port),
          OpenTelemetry.baggage(),
          appLayer,
          // ZLayer.Debug.mermaid
        )
    }.catchAll { error =>
      Console.printLine(s"Failed to load config: $error").exitCode
    }
}



// object MainApp extends ZIOAppDefault {



//   def run =
//     Server
//       .serve(
//         MainRoutes()
//       )
//       .provide(
//         OtelSdk.custom(resourceName),
//         OpenTelemetry.tracing(instrumentationScopeName),
//         OpenTelemetry.metrics(instrumentationScopeName),
//         OpenTelemetry.logging(instrumentationScopeName),
//         Server.defaultWithPort(8002)
//       )
// }
