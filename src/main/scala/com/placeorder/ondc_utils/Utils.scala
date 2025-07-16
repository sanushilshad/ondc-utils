package com.placeorder.ondc_utils

import zio.{Console, UIO, ZIO}
import scala.util.matching.Regex
import com.placeorder.ondc_utils.Commands.{generateJwtTokenCommand, runSqlMigrations}
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import scala.util.Try
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import java.time.Instant
import io.getquill.jdbczio.Quill
import io.getquill.SnakeCase
object Utils {
  def extractValidationErrorMessage(error: Throwable): UIO[String] = {
    val html = error.getMessage // Assuming the error message contains the HTML response
    val messagePattern: Regex = """<p id="message">(.*?)</p>""".r

    val extractedMessage =
      messagePattern.findFirstMatchIn(html).map(_.group(1)).getOrElse("Unknown error")
    ZIO.succeed(extractedMessage)
  }

  def jwtDecode(token: String, key: String): Try[JwtClaim] =
    Jwt.decode(token, key, Seq(JwtAlgorithm.HS256))

  def generateJwtToken(id: String, key: String): ZIO[Any, Nothing, String] =
    val now   = Instant.now.getEpochSecond
    val claim = JwtClaim(
      subject = Some(id),
      issuedAt = Some(now),
      expiration = Some(now + 3600), // token valid for 1 hour
    )

    val token = Jwt.encode(claim, key, JwtAlgorithm.HS256)
    ZIO.succeed(token)

  def run_custom_commands(commandKey: String)
      : ZIO[Quill.Postgres[SnakeCase] & AppConfig, Throwable, Unit] =
    commandKey match {

      case "generate_jwt_token" =>
        for {
          config <- ZIO.service[AppConfig]
          _      <- generateJwtTokenCommand(config.application.serviceId, config.secret.jwt.key)
        } yield ()

      case "migrate" =>
        runSqlMigrations("migrations") // use relative path

      case unknown =>
        Console.printLine(s"Unknown command: $unknown") *>
          ZIO.fail(new IllegalArgumentException(s"Invalid command: $unknown"))
    }
}
