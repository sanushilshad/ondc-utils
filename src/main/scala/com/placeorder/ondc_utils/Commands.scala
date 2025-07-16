package com.placeorder.ondc_utils

import zio._
import io.getquill.jdbczio.Quill
import io.getquill.context.ExecutionInfo
import com.placeorder.ondc_utils.Utils.generateJwtToken
import java.io.File
import scala.io.Source
import io.getquill.SnakeCase

object Commands {
  def generateJwtTokenCommand(id: String, key: String): ZIO[Any, Throwable, Unit] =
    for {
      _     <- Console.printLine("Generating JWT token...")
      token <- generateJwtToken(id, key) // Replace with your actual implementation
      _     <- Console.printLine(s"Generated token: $token")
    } yield ()

  def runSqlMigrations(directoryPath: String): ZIO[Quill.Postgres[SnakeCase], Throwable, Unit] =
    ZIO.serviceWithZIO[Quill.Postgres[SnakeCase]] { ctx =>
      import ctx._
      for {
        files <- ZIO.attempt {
          new File(directoryPath)
            .listFiles()
            .filter(f => f.isFile && f.getName.endsWith(".sql"))
            .sortBy(_.getName)
        }

        _ <- ZIO.foreachDiscard(files) { file =>
          for {
            sql <- ZIO.attempt(Source.fromFile(file).getLines().mkString("\n"))
            _   <- Console.printLine(s"📄 Running migration: ${file.getName}")

            statements = sql.split(";").map(_.trim).filter(_.nonEmpty)

            _ <- ZIO.foreachDiscard(statements) { stmt =>
              for {
                _ <- Console.printLine(s"🔹 Executing statement: $stmt")
                _ <- ctx.executeAction(stmt)(ExecutionInfo.unknown, ())
              } yield ()
            }
          } yield ()
        }
      } yield ()
    }
}
