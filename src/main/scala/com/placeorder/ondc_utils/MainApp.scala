package com.placeorder.ondc_utils

import zio._
import zio.http.Server
import zio.http.Client
import zio.telemetry.opentelemetry.OpenTelemetry
import zio.telemetry.opentelemetry.tracing.Tracing
import com.placeorder.ondc_utils.Config.loadConfig
import zio.telemetry.opentelemetry.baggage.Baggage
import zio.telemetry.opentelemetry.baggage.propagation.BaggagePropagator
import zio.http.netty.NettyConfig
import io.getquill.jdbczio.Quill
import io.getquill.SnakeCase
import com.placeorder.ondc_utils.Utils.run_custom_commands
object MainApp extends ZIOAppDefault {

  private val instrumentationScopeName = "com.placeorder.ondc_utils"

  def run =
    loadConfig().flatMap { appConfig =>
      getArgs.flatMap {
        case Chunk(command, _*) =>
          run_custom_commands(command, appConfig).exitCode

        case Chunk() =>
          val appLayer = ZLayer.succeed(appConfig)

          Server
            .serve(MainRoutes())
            // .exitCode
            // .onInterrupt(
            //     ZIO.logInfo("Server interrupted, performing cleanup...") *>
            //     ZIO.unit
            //   )
            // .catchAllCause(cause => ZIO.logErrorCause("Server failed", cause))
            .provide(
              OtelSdk.custom(appConfig.tracing.otelServiceName, appConfig.tracing.otelExporterTracesEndpoint),
              OpenTelemetry.tracing(instrumentationScopeName),
              OpenTelemetry.logging(instrumentationScopeName),
              OpenTelemetry.contextZIO,
              Server.defaultWithPort(appConfig.application.port),
              OpenTelemetry.baggage(),
              appLayer,
              Client.default,
              UserClient.live,
              Quill.Postgres.fromNamingStrategy(SnakeCase),
              DatabaseConfig.makeDataSourceLive
            )
      }
    }.catchAll { error =>
      Console.printLine(s"Failed to load config: $error").as(ExitCode.failure)
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
