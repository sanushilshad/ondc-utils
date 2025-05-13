package com.placeorder.ondc_utils
import zio._
import zio.http._
import scala.util.Try
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import zio.json._
// Secret Authentication key
val SECRET_KEY = "secretKey"

// def jwtDecode(token: String, key: String): Try[JwtClaim] =
//   Jwt.decode(token, key, Seq(JwtAlgorithm.HS512))

// val bearerAuthMiddleware : HandlerAspect[Any, String] =
//   HandlerAspect.interceptIncomingHandler(Handler.fromFunctionZIO[Request] { request =>
//     request.header(Header.Authorization) match {
//       case Some(Header.Authorization.Bearer(token)) =>
//         ZIO
//           .fromTry(jwtDecode(token.value.asString, SECRET_KEY))
//           .orElseFail(Response.badRequest("Invalid or expired token!"))
//           .flatMap(claim => ZIO.fromOption(claim.subject).orElseFail(Response.badRequest("Missing subject claim!")))
//           .map(u => (request, u))

//       case _ => ZIO.fail(Response.unauthorized.addHeaders(Headers(Header.WWWAuthenticate.Bearer(realm = "Access"))))
//     }
//   })

def jwtDecode(token: String, key: String): Try[JwtClaim] =
  Jwt.decode(token, key, Seq(JwtAlgorithm.HS512))


// val bearerAuthWithContext: HandlerAspect[Any, String] =
//   HandlerAspect.interceptIncomingHandler(Handler.fromFunctionZIO[Request] { request =>
//     request.header(Header.Authorization) match {
//       case Some(Header.Authorization.Bearer(token)) =>
//         ZIO
//           .fromTry(jwtDecode(token.value.asString, SECRET_KEY))
//           .orElseFail(Response.badRequest("Invalid or expired token!"))
//           .flatMap(claim => ZIO.fromOption(claim.subject).orElseFail(Response.badRequest("Missing subject claim!")))
//           .map(u => (request, u))

//       // case _ => ZIO.fail(GenericError.UnexpectedError(customerMessage = "Authorization header not found")).mapError(handleAuthError)
      
//       case _ => ZIO.fail(GenericError.UnexpectedError(customerMessage = "Authorization header not found")).mapError(handleAuthError)
//     }
//   })




val bearerAuthWithContext: HandlerAspect[UserClient, UserAccount] =
  HandlerAspect.interceptIncomingHandler(Handler.fromFunctionZIO[Request] { request =>
    request.header(Header.Authorization) match {
      case Some(Header.Authorization.Bearer(token)) =>
        for {
          user <- ZIO
            .serviceWithZIO[UserClient](_.getUserAccount(userAuthToken = Some(token.value.mkString)))
            .mapError(e => Response.json(GenericError.UnexpectedError(customerMessage = e.getMessage).toJson).status(Status.InternalServerError))
        } yield (request, user)

      case _ =>
        ZIO
          .fail(GenericError.ValidationError(customerMessage = "Authorization header not found"))
          .mapError(e=> Response.json(e.toJson).status(Status.BadRequest))
    }
  })