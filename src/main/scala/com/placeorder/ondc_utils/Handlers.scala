package com.placeorder.ondc_utils
import zio._
import zio.http.template.Dom
import zio.telemetry.opentelemetry.tracing.Tracing
import io.opentelemetry.api.trace.{Span, SpanKind}
import io.opentelemetry.api.GlobalOpenTelemetry
import zio.http.Status
import io.getquill.jdbczio.Quill
import io.getquill.SnakeCase
import io.getquill.SqlMirrorContext
import io.getquill.MirrorSqlDialect
import io.getquill.Literal
import io.getquill._
object MainHandlers {
  val healthCheckRequest: UIO[Dom] = ZIO.succeed(Dom.text("Running Server"))

  def fetchURLRequest(body: FetchURLBody): ZIO[Tracing & AppConfig, GenericError, GenericSuccess[Map[String, String]]] = 
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


  def fetchCountryRequest(body: FetchCountryBody): ZIO[Tracing & AppConfig, GenericError, GenericSuccess[List[countryMapData]]] =
    for {
      tracing   <- ZIO.service[Tracing]                       // 1) grab Tracing
      countries <- tracing.span("fetch-country-request", SpanKind.SERVER) {
        for {
          _         <- tracing.addEvent("processing fetch country request")
          _         <- ZIO.logInfo(s"Fetch country request body: $body")
          all        = Country.values.toList

          filtered   = body.query.fold(all.take(10)) { q =>
                          all.filter(c => c.label.equalsIgnoreCase(q)) match {
                            case Nil     => all.filter(_.label.toLowerCase.contains(q.toLowerCase)).take(10)
                            case exact   => exact
                          }
                        }

          result    <- if filtered.nonEmpty then ZIO.succeed(filtered)
                        else ZIO.fail(GenericError.DataNotFound(customerMessage="No data found"))

          finalData <- ZIO.succeed(result.map(c => countryMapData(c.label, c.code)))
          _         <- tracing.addEvent(s"Response: $finalData")
          _         <- ZIO.logInfo(s"Fetch Country Response: $finalData")
          _         <- tracing.addEvent("Finished Processing Fetch Country Request")
        } yield finalData
      }
    } yield GenericSuccess.SuccessResponse(
              customerMessage = "Successfully fetched data",
              data            = Some(countries)
              )


  def fetchCategoryRequest(
    body: FetchCategoryRequest
  ): ZIO[Tracing & AppConfig & Quill.Postgres[SnakeCase], GenericError, GenericSuccess[List[Category]]] = 
    for {
      tracing <- ZIO.service[Tracing]
      result <- tracing.span("fetch-category-request", SpanKind.SERVER) {
        ZIO.serviceWithZIO[Quill.Postgres[SnakeCase]] { ctx =>
          {
            import ctx._

            val q = body.query match {
              case Some(queryStr) =>
                val pattern = "%" + queryStr.toLowerCase + "%"
                quote {
                  CategoryModel.categorySchema.filter(c =>
                    c.label.toLowerCase.like(lift(pattern))
                  )
                }
              case None =>
                quote {
                  CategoryModel.categorySchema
                }
            }

            ctx.run(q)
              .map(_.map(c => Category(c.label, c.code, c.image, c.domainCode)))
              .tap(a => tracing.addEvent(s"Fetched categories: $a"))
              .map(a => GenericSuccess.SuccessResponse(customerMessage = "Successfully fetched data", data = Some(a)))
              .mapError(e => GenericError.UnexpectedError(customerMessage = e.toString))
          }
        }
      }
    } yield result






}