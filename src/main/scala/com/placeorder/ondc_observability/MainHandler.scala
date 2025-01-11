package com.placeorder.ondc_observability
import zio._
import zio.http._
object MainHandler {
  def healthCheckRequest: UIO[String] = ZIO.succeed("Running Server")
}
