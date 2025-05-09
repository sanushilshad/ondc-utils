package com.placeorder.ondc_utils
import zio.schema.{Schema, DeriveSchema}
import zio.schema.annotation.{validate}
import zio.schema.validation.Validation
import zio.json._
import zio.http.Response.status
import zio.http.Response
import zio.http.Status
import zio.schema._
import zio.schema.Schema.Case
import zio.schema.annotation.fieldName
import zio.schema.annotation.caseName
import zio.config.derivation.name
import zio.config.derivation.describe
import zio.config.derivation.snakeCase
import zio.config.magnolia.*
// import zio.config.*

import zio.{Config}
import zio.config.ConfigOps
import scala.compiletime.ops.string


// @snakeCase
sealed abstract class URLId(val idType: String) derives Schema, JsonCodec, Config {
  override def toString: String = idType
}

object URLId {

  // @caseName("user")
  // // @describe("user")
  // @name("user")
  // case object User extends URLId("user")

  @caseName("websocket")
  // @describe("websocket")
  @name("websocket")
  case object Websocket extends URLId("websocket")

  @caseName("placeorder")
  // @describe("placeorder")
  @name("placeorder")
  case object Placeorder extends URLId("placeorder")

}

final case class FetchURLBody(id: Option[String]) derives Schema



object FetchURLBody:
  implicit val fetchURLBodyDecoder: JsonDecoder[FetchURLBody] = DeriveJsonDecoder.gen[FetchURLBody]
  implicit val fetchURLBodyEncoder: JsonEncoder[FetchURLBody] = DeriveJsonEncoder.gen[FetchURLBody]
//final case class FetchURLBody(city: String, @validate(Validation.greaterThan(17)) age: Int) derives Schema
// object ObsProducerBody {
//     implicit val schema: Schema[ObsProducerBody] = DeriveSchema.gen[ObsProducerBody]
// }



trait GenericResponse[D]:
  val status: Boolean
  val customerMessage: String
  val code: Int  
  val data: Option[D]



sealed trait GenericSuccess[D] extends GenericResponse[D] derives  Schema:
    override val status: Boolean = true

object GenericSuccess:
    final case class SuccessResponse[D](code: Int = Status.Ok.code, customerMessage: String, data: Option[D]) extends GenericSuccess[D] derives  JsonCodec


// object GenericResponse {


//     def apply[D](message: String, statusCode: Status): GenericResponse[Unit] =
//         GenericResponse(false, message, statusCode.code.toString(), None)

//     def success[D](data: Option[D], message: String = "Success", statusCode: Status = Status.Ok): GenericResponse[D] =
//         GenericResponse(true, message, statusCode.code.toString(), data)

    
//     def error[D](message: String, statusCode: Status): GenericResponse[Unit] =
//         GenericResponse(false, message, statusCode.code.toString(), None)

        
// }


