package com.placeorder.ondc_utils

import com.typesafe.config.{Config, ConfigFactory}
import zio.{ZIO, ConfigProvider, Task, Unsafe, IO}
import zio.config.typesafe._
import zio.config.magnolia.deriveConfig
import zio.config.ConfigErrorOps
import zio.json.{JsonEncoder, JsonDecoder}
import zio.{Config as ZioConfig}
import zio.config.magnolia.derived
import zio.config.magnolia.deriveConfigFromConfig
// enum NPType(val a: String) derives JsonEncoder{
//   case BuyerApp extends NPType("buyer_app")
//   case SellerApp extends NPType("seller_app")
// }



case class UrlMapping(
  // id: URLId,
  url: String
)


case class AppConfig(
  // keyMapping: List [TokenMapping],
  port: Int,
  otelServiceName: String,
  otelExporterTracesEndpoint: String,
  urlMapping: Map[String, UrlMapping]
)



object Config {


  def loadConfig(): ZIO[Any, Throwable, AppConfig] = { 
    for {
      appProperties <- ConfigProvider.fromHoconFilePathZIO("env.conf")
      config <- appProperties.load(deriveConfig[AppConfig])
    } yield config
  }
 

  
  def readConfigFile(): Task[Config] = ZIO.attempt {
    val config: Config = ConfigFactory.load("env.conf")
    config
  }

  def loadConfig2(): AppConfig = {
    val io: IO[String, AppConfig] =
      ConfigProvider.fromHoconFilePath("env.conf").load(deriveConfig[AppConfig]).mapError(_.prettyPrint())
    val readResult = Unsafe.unsafe { implicit u =>
      zio.Runtime.default.unsafe.run(io).getOrThrowFiberFailure()
    }
    readResult

  }

}