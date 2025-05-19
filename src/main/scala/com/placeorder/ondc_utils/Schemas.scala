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
import zio.schema.annotation.{fieldName, caseName}
import zio.config.derivation.{describe, snakeCase, name}
import zio.config.magnolia.*
// import zio.config.*

import zio.{Config}
import zio.config.ConfigOps
import scala.compiletime.ops.string
import scala.collection.mutable.HashMap


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







final case class FetchCountryBody(query: Option[String]) derives Schema, JsonCodec



// object FetchCountryBody:
//   implicit val fetchCountryBodyDecoder: JsonDecoder[FetchCountryBody] = DeriveJsonDecoder.gen[FetchCountryBody]
//   implicit val fetchCountryBodyEncoder: JsonEncoder[FetchCountryBody] = DeriveJsonEncoder.gen[FetchCountryBody]


// case class countryMapData(label: String, key: String)

// val countryMapping: Map[String, String] = Map(
//   "Afghanistan"         → "AFG",
//   "India"               → "IND",
//   "Singapore"           → "SGP"
//   )



// val countryMapping: Map[String, countryMapData] = Map(
//   "Afghanistan"         → countryMapData(label= "Afghanistan", key= "AFG"),
//   "India"               → countryMapData(label= "India", key= "IND"),
//   "Singapore"           → countryMapData(label= "Singapore", key= "SGP"),
//   )



// sealed abstract class Country(val label: String, val code: String)  derives Schema, JsonCodec


// object Country {
//   case object Afghanistan extends Country("Afghanistan", "AFG")
//   case object India       extends Country("India",       "IND")
//   case object Singapore   extends Country("Singapore",   "SGP")
//   // … add the rest here …

//   // 2) A single source‑of‑truth list
//   val values: List[Country] =
//     List(Afghanistan, India, Singapore /*, …*/)

//   // 3) Derived lookup maps
//   lazy val byLabel: Map[String, Country] =
//     values.iterator.map(c => c.label -> c).toMap

//   lazy val byCode: Map[String, Country] =
//     values.iterator.map(c => c.code  -> c).toMap
// }

// case class countryMapData(label: String, key: String) derives  JsonCodec, Schema



enum Country(val label: String, val code: String) derives Schema, JsonCodec:
  case Afghanistan extends Country("Afghanistan", "AFG")
  case India       extends Country("India",       "IND")
  case Singapore   extends Country("Singapore",   "SGP")
  // … add the rest here …

object Country:
  // `values` is provided by Scala 3 enums
  private val all: List[Country] = values.toList
  lazy val byLabel: Map[String, Country] = all.map(c => c.label -> c).toMap
  lazy val byCode:  Map[String, Country] = all.map(c => c.code  -> c).toMap

case class countryMapData(label: String, code: String) derives  JsonCodec, Schema





case class FetchCategoryRequest(query: Option[String], categoryCodeList: Option[List[String]],  domainCode: String) derives Schema, JsonCodec


case class Category(label: String, code: String, image: String, domainCode: String) derives  JsonCodec, Schema


case class FetchDomainRequest(query: Option[String]) derives Schema, JsonCodec
case class Domain(label: String, code: String, image: String) derives  JsonCodec, Schema


case class FetchStateRequest(query: Option[String], countryCode: Option[String]) derives Schema, JsonCodec
case class State(label: String, code: String, countryCode: String) derives  JsonCodec, Schema


case class FetchCityRequest(query: Option[String], stateCode: String) derives Schema, JsonCodec

case class City(label: String, code: String, pincode: String) derives  JsonCodec, Schema