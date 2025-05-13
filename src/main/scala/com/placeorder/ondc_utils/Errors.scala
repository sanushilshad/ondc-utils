package com.placeorder.ondc_utils

import zio.schema.DeriveSchema
import zio.schema.Schema
import zio.schema.derived
import zio.http.Status
import scala.compiletime.ops.int
import scala.compiletime.ops.string
import zio.json.JsonCodec

// sealed trait GenericErrorResponse

// object GenericErrorResponse {
//   def toGenericResponse(error: GenericError): GenericResponse[Unit] = error match {
//     case GenericError.DataNotFound()       => GenericResponse.error("Data Not Found", Status.NotFound)
//     case GenericError.ValidationError(msg) => GenericResponse.error(s"Validation Error: $msg", Status.BadRequest)
//     case GenericError.UnexpectedError()    => GenericResponse.error("Internal Server Error", Status.InternalServerError)
//   }
// }
// sealed trait GenericError  derives Schema



sealed trait GenericError extends GenericResponse[Nothing] derives  JsonCodec:
  override val status: Boolean = false
  override val data: Option[Nothing] = None

object GenericError:
  final case class DataNotFound(code: Int = Status.NotFound.code, customerMessage: String) extends GenericError derives JsonCodec
  final case class ValidationError(code: Int = Status.BadRequest.code, customerMessage: String) extends GenericError derives JsonCodec
  final case class UnexpectedError(code: Int = Status.InternalServerError.code, customerMessage: String) extends GenericError derives JsonCodec
  
  given Schema[DataNotFound] = DeriveSchema.gen[DataNotFound]
  given Schema[ValidationError] = DeriveSchema.gen[ValidationError]
  given Schema[UnexpectedError] = DeriveSchema.gen[UnexpectedError]
