package com.placeorder.ondc_utils
import zio._
import zio.http._
import scala.util.Try
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import zio.json._
import scala.util.{Success, Failure}
import com.placeorder.ondc_utils.Utils.jwtDecode
// Secret Authentication key
// val SECRET_KEY = "secretKey"


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







// val bearerAuthWithContext: HandlerAspect[UserClient, UserAccount] =
//   HandlerAspect.interceptIncomingHandler(Handler.fromFunctionZIO[Request] { request =>
//     request.header(Header.Authorization) match {
//       case Some(Header.Authorization.Bearer(token)) =>
//         for {
//           user <- ZIO
//             .serviceWithZIO[UserClient](_.getUserAccount(userAuthToken = Some(token.value.mkString)))
//             .mapError(e => Response.json(GenericError.UnexpectedError(customerMessage = e.getMessage).toJson).status(Status.InternalServerError))
//         } yield (request, user)

//       case _ =>
//         ZIO
//           .fail(GenericError.ValidationError(customerMessage = "Authorization header not found"))
//           .mapError(e=> Response.json(e.toJson).status(Status.BadRequest))
//     }
//   })



val bearerAuthentication: HandlerAspect[UserClient & AppConfig, Option[UserAccount]] =
  HandlerAspect.interceptIncomingHandler(Handler.fromFunctionZIO[Request] { request =>
    val requestTypeOpt = request.rawHeader("x-request-type").map(_.toLowerCase)

    request.header(Header.Authorization) match {
      case Some(Header.Authorization.Bearer(token)) =>
        val tokenStr = token.value.mkString

        requestTypeOpt match {
          case Some("internal") =>
            for {
              config <- ZIO.service[AppConfig]
              result <- jwtDecode(tokenStr, config.secret.jwt.key) match {
                case Success(claim)  if claim.subject == Some(config.application.serviceId) =>
                  ZIO.succeed((request, None)) // Valid internal token, no user info needed
                case Success(_) =>
                  ZIO.fail(
                    Response.json(
                      GenericError.ValidationError(
                        customerMessage = s"Unauthorized internal token"
                      ).toJson
                    ).status(Status.Unauthorized)
                  )
                case Failure(ex) =>
                  ZIO.fail(
                    Response.json(
                      GenericError.ValidationError(
                        customerMessage = s"Invalid internal token: ${ex.getMessage}"
                      ).toJson
                    ).status(Status.BadRequest)
                  )
              }
            } yield (request, None)

          case _ =>
            for {
              user <- ZIO
                .serviceWithZIO[UserClient](_.getUserAccount(Some(tokenStr)))
                .mapError(e =>
                  Response.json(
                    GenericError.UnexpectedError(customerMessage = e.getMessage).toJson
                  ).status(Status.InternalServerError)
                )
            } yield (request, Some(user))
        }

      case _ =>
        ZIO.fail(
          Response.json(
            GenericError.ValidationError(customerMessage = "Authorization header not found").toJson
          ).status(Status.BadRequest)
        )
    }
  })