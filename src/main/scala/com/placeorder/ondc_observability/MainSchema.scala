package com.placeorder.ondc_observability
import zio.schema.{Schema, DeriveSchema}
import zio.schema.annotation.{validate}
import zio.schema.validation.Validation
import zio.json.JsonEncoder
import zio.json.DeriveJsonEncoder
import zio.http.Response.status
import zio.http.Response
import zio.http.Status
import zio.schema.derived

final case class ObsProducerBody(city: String, @validate(Validation.greaterThan(17)) age: Int) derives Schema

// object ObsProducerBody {
//     implicit val schema: Schema[ObsProducerBody] = DeriveSchema.gen[ObsProducerBody]
// }




case class GenericResponse[D](
  status: Boolean,
  customerMessage: String,
  code: String,
  data: D
) 
derives Schema, JsonEncoder



object GenericResponse {


    def apply[D](message: String, statusCode: Status): GenericResponse[Unit] =
        GenericResponse(false, message, statusCode.code.toString(), ())

    def success[D](data: D, message: String = "Success", statusCode: Status = Status.Ok): GenericResponse[D] =
        GenericResponse(true, message, statusCode.code.toString(), data)

    
    def error[D](message: String, statusCode: Status): GenericResponse[Unit] =
        GenericResponse(false, message, statusCode.code.toString(), ())


    // implicit val encoderUnit: JsonEncoder[GenericResponse[Unit]] = DeriveJsonEncoder.gen[GenericResponse[Unit]]
        
}



// case class CustomError(
//   response: GenericResponse[Unit]
// ) derives Schema


case class CustomError(message: String) extends Throwable derives Schema

