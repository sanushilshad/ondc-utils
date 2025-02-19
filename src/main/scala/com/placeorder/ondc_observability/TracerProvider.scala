package com.placeorder.ondc_observability



import io.opentelemetry.api.common.Attributes
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter
import io.opentelemetry.sdk.resources.Resource
import io.opentelemetry.sdk.trace.SdkTracerProvider
import io.opentelemetry.semconv.ResourceAttributes
import zio.{RIO, ZIO, Scope}
import io.opentelemetry.sdk.trace.`export`.SimpleSpanProcessor

object TracerProvider {


  def jaeger(resourceName: String, tracingEndPoint: String): RIO[Scope, SdkTracerProvider] =
    for {
      spanExporter   <- ZIO.fromAutoCloseable( ZIO.succeed(
                        io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter.builder()
                          .setEndpoint(tracingEndPoint) // Default Jaeger OTLP endpoint
                          .build()
                      ))
      spanProcessor  <- ZIO.fromAutoCloseable(ZIO.succeed(SimpleSpanProcessor.create(spanExporter)))
      tracerProvider <-
        ZIO.fromAutoCloseable(
          ZIO.succeed(
            SdkTracerProvider
              .builder()
              .setResource(Resource.create(Attributes.of(ResourceAttributes.SERVICE_NAME, resourceName)))
              .addSpanProcessor(spanProcessor)
              .build()
          )
        )
    } yield tracerProvider
}