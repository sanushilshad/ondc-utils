package com.placeorder.ondc_utils

import zio._
import zio.http._
// import zio.http.model._
import zio.json._
import zio.schema.codec.JsonCodec.schemaBasedBinaryCodec
import com.placeorder.ondc_utils.GenericSuccess.SuccessResponse

case class UserAccount(id: String, username: String)  derives JsonCodec
object UserAccount {
  implicit val decoder: JsonDecoder[UserAccount] = DeriveJsonDecoder.gen[UserAccount]
}

// final case class UserClient(baseUrl: String, authToken: String, client: Client) {
//   def getAuthToken(userAuthToken: Option[String]): Headers =
//     Headers(Header.Authorization.Bearer(userAuthToken.getOrElse(authToken)))

//   def getUserAccount(userAuthToken: Option[String] = None, userId: Option[String] = None): Task[UserAccount] = {
//     val baseHeaders = getAuthToken(userAuthToken) ++ Headers(
//       Header.Custom("x-request-id", "internal"),
//       Header.Custom("x-device-id", "internal")
//     )

//     val userIdHeader = userId
//       .map(id => Headers(Header.Custom("x-user-id", id)))
//       .getOrElse(Headers.empty)

//     val headers = baseHeaders ++ userIdHeader
//     val finalUrl = URL.decode(s"$baseUrl/user/fetch").toOption.get
//     val request = Request.post(finalUrl, Body.empty).addHeaders(headers)

    // for {
    //   response <- client.batched(request)
    //   body     <- response.body.asString
    //   user     <- ZIO.fromEither(body.fromJson[UserAccount])
    // } yield user
//   }
// }



final case class UserClient(baseUrl: String, authToken: String, client: Client) {
  def getAuthToken(userAuthToken: Option[String]): Headers =
    Headers(Header.Authorization.Bearer(userAuthToken.getOrElse(authToken)))

  def getUserAccount(userAuthToken: Option[String] = None, userId: Option[String] = None): Task[UserAccount] = {
    val baseHeaders = getAuthToken(userAuthToken) ++ Headers(
      Header.Custom("x-request-id", "internal"),
      Header.Custom("x-device-id", "internal")

      
    )

    val userIdHeader = userId
      .map(id => Headers(Header.Custom("x-user-id", id)))
      .getOrElse(Headers.empty)

    val headers = baseHeaders ++ userIdHeader
    val finalUrl = URL.decode(s"$baseUrl/user/fetch").toOption.get
    val request = Request.post(finalUrl, Body.empty).addHeaders(headers)
    // val request = Request.get(finalUrl).addHeaders(headers)
    for {
      response <- client.batched(request)
      body     <- response.body.asString
      parsed       <- ZIO.fromEither(body.fromJson[SuccessResponse[UserAccount]])
                        .mapError(err => new RuntimeException(s"Failed to parse user account response: $err"))
      user         <- ZIO.fromOption(parsed.data)
                        .mapError(_ => new RuntimeException("No user data found in response"))
    } yield user
  }
}

object UserClient {
  val live: ZLayer[Client & AppConfig, Nothing, UserClient] =
    ZLayer {
      for {
        client   <- ZIO.service[Client]
        config   <- ZIO.service[AppConfig]
      } yield UserClient(config.user.url, config.user.token, client)
    }
}