package com.placeorder.ondc_utils

import zio._
import zio.http.{Method, Response, Routes}
import zio.http.codec.PathCodec.path
import zio.http.codec.Doc
import zio.http.endpoint.Endpoint
import zio.http.endpoint.openapi.{OpenAPIGen, SwaggerUI}
import zio.http.template.Dom
import zio.http._
import zio.telemetry.opentelemetry.tracing.Tracing
import zio.telemetry.opentelemetry.baggage.Baggage
import zio.telemetry.opentelemetry.baggage.propagation.BaggagePropagator
import zio.http.codec.HttpCodec
import zio.http.Status
import zio.http.codec.HttpCodecError
import zio.http.Status.ClientError
import zio.json.EncoderOps
import io.getquill.jdbczio.Quill
import io.getquill.SnakeCase

object MainRoutes {
  def apply()
      : Routes[Tracing & Baggage & AppConfig & UserClient & Quill.Postgres[SnakeCase], Response] = {

    val healthCheckEndpoint =
      Endpoint(Method.GET / "health").out[Dom](Doc.p("Successful execution")) ?? Doc.p(
        "API for checking the status of the server",
      )
    val ONDCURLPoint        = Endpoint(Method.POST / "url")
      .in[FetchURLBody]
      .out[GenericSuccess[Map[String, String]]](Doc.p("Successful execution"))
      .outErrors(
        HttpCodec.error[GenericError.UnexpectedError](Status.InternalServerError) ?? Doc
          .p("Internal Server Error"),
        HttpCodec.error[GenericError.ValidationError](Status.BadRequest) ?? Doc.p("Bad Request"),
        HttpCodec.error[GenericError.DataNotFound](Status.NotFound) ?? Doc.p("Data not found"),
      ) ?? Doc.p("API for fetching all URLs required by UI")

    val countryListPoint = Endpoint(Method.POST / "country/fetch")
      .in[FetchCountryBody]
      .out[GenericSuccess[List[countryMapData]]](Doc.p("Successful execution"))
      .outErrors(
        HttpCodec.error[GenericError.UnexpectedError](Status.InternalServerError) ?? Doc
          .p("Internal Server Error"),
        HttpCodec.error[GenericError.ValidationError](Status.BadRequest) ?? Doc.p("Bad Request"),
        HttpCodec.error[GenericError.DataNotFound](Status.NotFound) ?? Doc.p("Data not found"),
      ) ?? Doc.p("API for listing  countries")

    val categoryFetchtPoint = Endpoint(Method.POST / "category/fetch")
      .in[FetchCategoryRequest]
      .out[GenericSuccess[List[Category]]](Doc.p("Successful execution"))
      .outErrors(
        HttpCodec.error[GenericError.UnexpectedError](Status.InternalServerError) ?? Doc
          .p("Internal Server Error"),
        HttpCodec.error[GenericError.ValidationError](Status.BadRequest) ?? Doc.p("Bad Request"),
        HttpCodec.error[GenericError.DataNotFound](Status.NotFound) ?? Doc.p("Data not found"),
      ) ?? Doc.p("API for fetching categories")

    val domainFetchtPoint = Endpoint(Method.POST / "domain/fetch")
      .in[FetchDomainRequest]
      .out[GenericSuccess[List[Domain]]](Doc.p("Successful execution"))
      .outErrors(
        HttpCodec.error[GenericError.UnexpectedError](Status.InternalServerError) ?? Doc
          .p("Internal Server Error"),
        HttpCodec.error[GenericError.ValidationError](Status.BadRequest) ?? Doc.p("Bad Request"),
        HttpCodec.error[GenericError.DataNotFound](Status.NotFound) ?? Doc.p("Data not found"),
      ) ?? Doc.p("API for fetching domains")

    val stateFetchtPoint = Endpoint(Method.POST / "state/fetch")
      .in[FetchStateRequest]
      .out[GenericSuccess[List[State]]](Doc.p("Successful execution"))
      .outErrors(
        HttpCodec.error[GenericError.UnexpectedError](Status.InternalServerError) ?? Doc
          .p("Internal Server Error"),
        HttpCodec.error[GenericError.ValidationError](Status.BadRequest) ?? Doc.p("Bad Request"),
        HttpCodec.error[GenericError.DataNotFound](Status.NotFound) ?? Doc.p("Data not found"),
      ) ?? Doc.p("API for fetching states")

    val cityFetchtPoint = Endpoint(Method.POST / "city/fetch")
      .in[FetchCityRequest]
      .out[GenericSuccess[List[City]]](Doc.p("Successful execution"))
      .outErrors(
        HttpCodec.error[GenericError.UnexpectedError](Status.InternalServerError) ?? Doc
          .p("Internal Server Error"),
        HttpCodec.error[GenericError.ValidationError](Status.BadRequest) ?? Doc.p("Bad Request"),
        HttpCodec.error[GenericError.DataNotFound](Status.NotFound) ?? Doc.p("Data not found"),
      ) ?? Doc.p("API for fetching cities")

    val openApi = OpenAPIGen.fromEndpoints(
      "ONDC Util API",
      "1.0.0",
      healthCheckEndpoint,
      ONDCURLPoint,
      countryListPoint,
      categoryFetchtPoint,
      domainFetchtPoint,
      stateFetchtPoint,
      cityFetchtPoint,
    )

    val routes = healthCheckEndpoint.implement {
      case _ =>
        MainHandlers.healthCheckRequest
    }.toRoutes ++
      ONDCURLPoint.implement {
        MainHandlers.fetchURLRequest
      }.toRoutes ++
      countryListPoint.implement {
        // case body  =>
        //     MainHandlers.fetchURLRequest(body)
        MainHandlers.fetchCountryRequest
      }.toRoutes.@@(bearerAuthentication) ++
      categoryFetchtPoint.implement {
        MainHandlers.fetchCategoryRequest
      }.toRoutes.@@(bearerAuthentication) ++
      domainFetchtPoint.implement {
        MainHandlers.fetchDomainRequest
      }.toRoutes.@@(bearerAuthentication) ++
      stateFetchtPoint.implement {
        MainHandlers.fetchStateRequest
      }.toRoutes.@@(bearerAuthentication) ++
      cityFetchtPoint.implement {
        MainHandlers.fetchCityRequest
      }.toRoutes.@@(bearerAuthentication) ++
      SwaggerUI.routes("/docs", openApi)

    routes
  }
}
