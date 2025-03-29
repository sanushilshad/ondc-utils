package com.placeorder.ondc_utils

import zio.schema.DeriveSchema
import zio.schema.Schema
import zio.schema.derived
import zio.http.Status
import scala.compiletime.ops.int
import scala.compiletime.ops.string

// sealed trait GenericErrorResponse

// object GenericErrorResponse {
//   def toGenericResponse(error: GenericError): GenericResponse[Unit] = error match {
//     case GenericError.DataNotFound()       => GenericResponse.error("Data Not Found", Status.NotFound)
//     case GenericError.ValidationError(msg) => GenericResponse.error(s"Validation Error: $msg", Status.BadRequest)
//     case GenericError.UnexpectedError()    => GenericResponse.error("Internal Server Error", Status.InternalServerError)
//   }
// }
sealed trait GenericError  derives Schema
object GenericError {
  final case class DataNotFound(status: Boolean= false, code: Int = Status.NotFound.code, customerMessage: String) extends GenericError
  final case class ValidationError(status: Boolean = false, code: Int = Status.BadRequest.code, customerMessage: String) extends GenericError
  final case class UnexpectedError(status: Boolean = false, code: Int = Status.InternalServerError.code, customerMessage: String) extends GenericError

  implicit val dataNotFoundSchema: Schema[GenericError.DataNotFound] =
    DeriveSchema.gen[GenericError.DataNotFound]

  implicit val validationErrorSchema: Schema[GenericError.ValidationError] =
    DeriveSchema.gen[GenericError.ValidationError]

  implicit val unexpectedErrorSchema: Schema[GenericError.UnexpectedError] =
    DeriveSchema.gen[GenericError.UnexpectedError]

    

}