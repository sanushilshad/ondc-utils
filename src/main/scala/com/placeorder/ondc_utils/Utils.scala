package com.placeorder.ondc_utils
import zio.{UIO, ZIO, Console}
import scala.util.matching.Regex
import com.placeorder.ondc_utils.Commands.generateJwtTokenCommand
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import scala.util.Try
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import java.time.Instant
object Utils {
    def extractValidationErrorMessage(error: Throwable): UIO[String] = {
        val html = error.getMessage // Assuming the error message contains the HTML response
        val messagePattern: Regex = """<p id="message">(.*?)</p>""".r

        val extractedMessage = messagePattern.findFirstMatchIn(html).map(_.group(1)).getOrElse("Unknown error")
        ZIO.succeed(extractedMessage)
    }

    def jwtDecode(token: String, key: String): Try[JwtClaim] =
        Jwt.decode(token, key, Seq(JwtAlgorithm.HS512))


    def generateJwtToken(id: String, key: String): ZIO[Any, Nothing, String] = 
        val now = Instant.now.getEpochSecond
        val claim = JwtClaim(
            subject = Some(id),
            issuedAt = Some(now),
            expiration = Some(now + 3600) // token valid for 1 hour
        )

        val token = Jwt.encode(claim, key, JwtAlgorithm.HS256)
        ZIO.succeed(token)
    




    def run_custom_commands(commandKey: String, config: AppConfig): ZIO[Any, Throwable, Unit] =
        commandKey match {
            case "generate_jwt_token" => generateJwtTokenCommand(config.application.serviceId, config.secret.jwt.key)

            case unknown =>
                Console.printLine(s"Unknown command: $unknown") *>
                ZIO.fail(new IllegalArgumentException(s"Invalid command: $unknown"))
        }

}


