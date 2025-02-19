package com.placeorder.ondc_observability

import io.opentelemetry.api.common.Attributes
import io.opentelemetry.exporter.otlp.http.logs.OtlpHttpLogRecordExporter
import io.opentelemetry.sdk.logs.SdkLoggerProvider
import io.opentelemetry.sdk.logs.`export`.SimpleLogRecordProcessor
import io.opentelemetry.sdk.resources.Resource
import io.opentelemetry.semconv.ResourceAttributes
import zio._
import io.opentelemetry.exporter.logging.otlp.OtlpJsonLoggingLogRecordExporter



object LoggerProvider {

  def stdout(resourceName: String): RIO[Scope, SdkLoggerProvider] =
    for {
      logRecordExporter  <- ZIO.fromAutoCloseable(ZIO.succeed(OtlpJsonLoggingLogRecordExporter.create()))
      logRecordProcessor <- ZIO.fromAutoCloseable(ZIO.succeed(SimpleLogRecordProcessor.create(logRecordExporter)))
      loggerProvider     <-
        ZIO.fromAutoCloseable(
          ZIO.succeed(
            SdkLoggerProvider
              .builder()
              .setResource(Resource.create(Attributes.of(ResourceAttributes.SERVICE_NAME, resourceName)))
              .addLogRecordProcessor(logRecordProcessor)
              .build()
          )
        )
    } yield loggerProvider

  def seq(resourceName: String): RIO[Scope, SdkLoggerProvider] =
    for {
      logRecordExporter  <-
        ZIO.fromAutoCloseable(
          ZIO.succeed(
            OtlpHttpLogRecordExporter
              .builder()
              .setEndpoint("http://localhost:14250/ingest/otlp/v1/logs")
              .build()
          )
        )
      logRecordProcessor <- ZIO.fromAutoCloseable(ZIO.succeed(SimpleLogRecordProcessor.create(logRecordExporter)))
      loggerProvider     <-
        ZIO.fromAutoCloseable(
          ZIO.succeed(
            SdkLoggerProvider
              .builder()
              .setResource(Resource.create(Attributes.of(ResourceAttributes.SERVICE_NAME, resourceName)))
              .addLogRecordProcessor(logRecordProcessor)
              .build()
          )
        )
    } yield loggerProvider
}