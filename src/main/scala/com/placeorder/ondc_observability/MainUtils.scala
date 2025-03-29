package com.placeorder.ondc_utils
import zio.{UIO, ZIO}
import scala.util.matching.Regex
object MainUtils {
    def extractValidationErrorMessage(error: Throwable): UIO[String] = {
        val html = error.getMessage // Assuming the error message contains the HTML response
        val messagePattern: Regex = """<p id="message">(.*?)</p>""".r

        val extractedMessage = messagePattern.findFirstMatchIn(html).map(_.group(1)).getOrElse("Unknown error")
        ZIO.succeed(extractedMessage)
    }
}
