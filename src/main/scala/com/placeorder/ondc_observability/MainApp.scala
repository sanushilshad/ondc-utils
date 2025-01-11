package com.placeorder.ondc_observability

import zio._
import zio.http._
import zio.http.codec.PathCodec.path
import zio.http.codec._
import zio.http.endpoint._
import zio.schema._
import zio.schema.annotation._
import zio.http.endpoint.openapi._
import zio.http.template.Dom
import zio.schema.validation.Validation


object MainApp extends ZIOAppDefault {
  def run =
    Server
      .serve(
        MainRoutes()
      )
      .provide(
        Server.defaultWithPort(8002)
      )
}
