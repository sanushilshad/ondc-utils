package com.placeorder.ondc_utils

import com.typesafe.config.{Config, ConfigFactory}
import zio.{ZIO, ConfigProvider, Task, Unsafe, IO}
import zio.config.typesafe._
import zio.config.ConfigErrorOps
import zio.json.{JsonEncoder, JsonDecoder}
import zio.{Config as ZioConfig}
import zio.config.magnolia.{deriveConfigFromConfig, deriveConfig, derived}
import scala.compiletime.ops.string
import zio.ZLayer
import io.getquill.jdbczio.Quill
import io.getquill.SnakeCase
import org.postgresql.ds.PGSimpleDataSource
import javax.sql.DataSource
import com.zaxxer.hikari.{HikariDataSource, HikariConfig}


// enum NPType(val a: String) derives JsonEncoder{
//   case BuyerApp extends NPType("buyer_app")
//   case SellerApp extends NPType("seller_app")
// }



case class UrlMapping(
  // id: URLId,
  url: String
)


case class TracingSetting( 
  otelServiceName: String,
  otelExporterTracesEndpoint: String
)


case class AppSetting(
  port: Int,
  serviceId: String
)

case class UserSetting(
  url: String,
  token: String
)

case class DatabaseConfig(
  username: String,
  password: String,
  port: Int,
  host: String,
  name: String
)

object DatabaseConfig {
  // val makeDataSource: ZIO[AppConfig, Throwable, DataSource] =
  //   ZIO.serviceWith[AppConfig] { cfg =>
  //     val ds = new PGSimpleDataSource()
  //     ds.setServerNames(Array(cfg.database.host))
  //     ds.setPortNumbers(Array(cfg.database.port))
  //     ds.setDatabaseName(cfg.database.name)
  //     ds.setUser(cfg.database.username)
  //     ds.setPassword(cfg.database.password)
  //     ds
  //   }

  // val makeDataSourceLive: ZLayer[AppConfig, Throwable, DataSource] =
  //   ZLayer.fromZIO(makeDataSource)


  val makeDataSource: ZIO[AppConfig, Throwable, DataSource] =
    ZIO.serviceWith[AppConfig] { cfg =>
      val hikariConfig = new HikariConfig()
      hikariConfig.setJdbcUrl(s"jdbc:postgresql://${cfg.database.host}:${cfg.database.port}/${cfg.database.name}")
      hikariConfig.setUsername(cfg.database.username)
      hikariConfig.setPassword(cfg.database.password)
      hikariConfig.setMaximumPoolSize(10) // You can adjust this
      hikariConfig.setMinimumIdle(2)

      new HikariDataSource(hikariConfig)
    }

  val makeDataSourceLive: ZLayer[AppConfig, Throwable, DataSource] =
    ZLayer.fromZIO(makeDataSource)
}

case class JWTConfig(
  key: String
)

case class SecretConfig(
  jwt: JWTConfig
)

case class AppConfig(
  // keyMapping: List [TokenMapping],
  application: AppSetting,
  tracing: TracingSetting,
  user: UserSetting,
  urlMapping: Map[String, UrlMapping],
  database: DatabaseConfig,
  secret: SecretConfig


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