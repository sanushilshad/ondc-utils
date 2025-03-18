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
import zio.json.JsonDecoder
import zio.json.DeriveJsonDecoder

// enum URLId(val a: String) derives Schema,  JsonEncoder, JsonDecoder {
//   case User      extends URLId("user") 
//   case Websocket extends URLId("websocket")
//   case Placeorder extends URLId("placeorder")
// }


final case class FetchURLBody(id: URLId) derives Schema

enum URLId(val idType: String):
  case User extends URLId("user")
  case Websocket extends URLId("websocket")
  case Placeorder extends URLId("placeorder")


object URLId:
  // Custom JsonDecoder for URLId
  implicit val urlIdDecoder: JsonDecoder[URLId] = JsonDecoder.string.mapOrFail { str =>
    URLId.values.find(_.idType.equalsIgnoreCase(str)) match {
      case Some(urlId) => Right(urlId)
      case None => Left(s"Unknown URLId: $str")
    }
  }

  // Custom JsonEncoder for URLId (optional, for consistent output)
  implicit val urlIdEncoder: JsonEncoder[URLId] = JsonEncoder.string.contramap(_.idType)

// sealed trait URLId
// object URLId {
//   case object User extends URLId
//   case object Websocket extends URLId
//   case object Placeorder extends URLId


//   def fromString(value: String): Option[URLId] = value match {
//     case "user"   => Some(User)
//     case "websocket" => Some(Websocket)
//     case "placeorder"  => Some(Placeorder)
//     case _          => None
//   }
// }





object FetchURLBody:
  implicit val fetchURLBodyDecoder: JsonDecoder[FetchURLBody] = DeriveJsonDecoder.gen[FetchURLBody]
  implicit val fetchURLBodyEncoder: JsonEncoder[FetchURLBody] = DeriveJsonEncoder.gen[FetchURLBody]
//final case class FetchURLBody(city: String, @validate(Validation.greaterThan(17)) age: Int) derives Schema
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

