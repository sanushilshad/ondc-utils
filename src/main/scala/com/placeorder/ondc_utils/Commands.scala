package com.placeorder.ondc_utils

import zio.{UIO, ZIO, Console}
import com.placeorder.ondc_utils.Utils.generateJwtToken

object Commands{

    def generateJwtTokenCommand(id: String, key: String): ZIO[Any, Throwable, Unit] =
        for {
            _ <- Console.printLine("Generating JWT token...")
            token <- generateJwtToken(id, key) // Replace with your actual implementation
            _ <- Console.printLine(s"Generated token: $token")
        } yield ()   
}