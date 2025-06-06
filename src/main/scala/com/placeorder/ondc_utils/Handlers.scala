package com.placeorder.ondc_utils
import zio._
import zio.http.template.Dom
import zio.telemetry.opentelemetry.tracing.Tracing
import io.opentelemetry.api.trace.{Span, SpanKind}
import io.opentelemetry.api.GlobalOpenTelemetry
import zio.http.Status
import io.getquill.jdbczio.Quill

import io.getquill.{MirrorSqlDialect, Literal, SqlMirrorContext, SnakeCase}
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


  def fetchCountryRequest(body: FetchCountryBody): ZIO[Tracing, GenericError, GenericSuccess[List[countryMapData]]] =
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
  ): ZIO[Tracing & Quill.Postgres[SnakeCase], GenericError, GenericSuccess[List[Category]]] =
    for {
      tracing <- ZIO.service[Tracing]
      result <- tracing.span("fetch-category-request", SpanKind.SERVER) {
        ZIO.serviceWithZIO[Quill.Postgres[SnakeCase]] { ctx =>
          import ctx._

          // Define domain + category join and filter by domain.code
          val baseQuery = quote {
            for {
              c <- CategoryModel.schema
              d <- DomainModel.schema if c.domain_id == d.id && d.code == lift(body.domainCode)
            } yield c
          }

          // Filter by query string
          val filteredByQuery = body.query match {
            case Some(queryStr) =>
              val pattern = "%" + queryStr.toLowerCase + "%"
              quote {
                baseQuery.filter(c => c.label.toLowerCase.like(lift(pattern)))
              }
            case None => baseQuery
          }

          // Filter by categoryCodeList
          val filteredFinal = body.categoryCodeList match {
            case Some(codes) if codes.nonEmpty =>
              quote {
                filteredByQuery.filter(c => liftQuery(codes).contains(c.code))
              }
            case _ => filteredByQuery
          }

          ctx.run(filteredFinal)
            .map(_.map(c => Category(c.label, c.code, c.image, body.domainCode)))
            .tap(cats => tracing.addEvent(s"Fetched categories: $cats"))
            .map(cats => GenericSuccess.SuccessResponse(
                customerMessage = "Successfully fetched data",
                data = Some(cats)
              ))
            .mapError(e => GenericError.UnexpectedError(customerMessage = e.toString))
        }
      }
    } yield result


  def fetchDomainRequest(
    body: FetchDomainRequest
  ): ZIO[Tracing & Quill.Postgres[SnakeCase], GenericError, GenericSuccess[List[Domain]]] =
    for {
      tracing <- ZIO.service[Tracing]
      result <- tracing.span("fetch-domain-request", SpanKind.SERVER) {
        ZIO.serviceWithZIO[Quill.Postgres[SnakeCase]] { ctx =>
          import ctx._

          val baseQuery = quote {
            DomainModel.schema
          }

          // Filter by query string
          val filteredByQuery = body.query match {
            case Some(queryStr) =>
              val pattern = "%" + queryStr.toLowerCase + "%"
              quote {
                baseQuery.filter(c => c.label.toLowerCase.like(lift(pattern)))
              }
            case None => baseQuery
          }


          ctx.run(filteredByQuery)
            .map(_.map(c => Domain(c.label, c.code, c.image)))
            .tap(cats => tracing.addEvent(s"Fetched domain: $cats"))
            .map(cats => GenericSuccess.SuccessResponse(
                customerMessage = "Successfully fetched data",
                data = Some(cats)
              ))
            .mapError(e => GenericError.UnexpectedError(customerMessage = e.toString))
        }
      }
    } yield result


  def fetchStateRequest(
    body: FetchStateRequest
  ): ZIO[Tracing & Quill.Postgres[SnakeCase], GenericError, GenericSuccess[List[State]]] =
    for {
      tracing <- ZIO.service[Tracing]
      result <- tracing.span("fetch-state-request", SpanKind.SERVER) {
        ZIO.serviceWithZIO[Quill.Postgres[SnakeCase]] { ctx =>
          import ctx._
          
          val filteredByQuery = body.countryCode match {
            case Some(queryStr) =>
              quote {
                StateModel.schema.filter(_.countryCode == lift(queryStr))
              }
            case None => quote{
              StateModel.schema
            }
          }

          body.query match {
              case Some(queryStr) =>
                val pattern = "%" + queryStr.toLowerCase + "%"
                quote {

                  filteredByQuery.filter(_.label.toLowerCase.like(lift(pattern)))
                }

              case None =>
                filteredByQuery
            }

          ctx.run(filteredByQuery)
            .map(_.map(c => State(c.label, c.code, c.countryCode)))
            .tap(states => tracing.addEvent(s"Fetched states: $states"))
            .map(states =>
              GenericSuccess.SuccessResponse(
                customerMessage = "Successfully fetched data",
                data = Some(states)
              )
            )
            .mapError(e => GenericError.UnexpectedError(customerMessage = e.toString))
        }
      }
    } yield result



  def fetchCityRequest(
    body: FetchCityRequest
  ): ZIO[Tracing & Quill.Postgres[SnakeCase], GenericError, GenericSuccess[List[City]]] =
    for {
      tracing <- ZIO.service[Tracing]
      result <- tracing.span("fetch-city-request", SpanKind.SERVER) {
        ZIO.serviceWithZIO[Quill.Postgres[SnakeCase]] { ctx =>
          import ctx._
          

          val baseQuery = quote {
            for {
              c <- CityModel.schema
              d <- StateModel.schema if c.state_id == d.id && d.code == lift(body.stateCode)
            } yield c
          }

          val filteredFinal = body.query match {
              case Some(queryStr) =>
                val pattern = "%" + queryStr.toLowerCase + "%"
                quote {

                  baseQuery.filter(_.label.toLowerCase.like(lift(pattern)))
                }

              case None =>
                baseQuery
            }

          ctx.run(filteredFinal)
            .map(_.map(c => City(c.label, c.code, c.pincode)))
            .tap(states => tracing.addEvent(s"Fetched city: $states"))
            .map(states =>
              GenericSuccess.SuccessResponse(
                customerMessage = "Successfully fetched data",
                data = Some(states)
              )
            )
            .mapError(e => GenericError.UnexpectedError(customerMessage = e.toString))
        }
      }
    } yield result

}





