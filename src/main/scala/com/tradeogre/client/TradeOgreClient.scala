package com.tradeogre.client

import cats.effect.{ConcurrentEffect, Resource, Sync}
import cats.implicits._
import com.tradeogre.client.response.{MarketInfoResponse, OrderBookResponse}
import com.tradeogre.config.TradeOgreClientConfig
import com.typesafe.scalalogging.StrictLogging
import io.circe.generic.auto._
import org.http4s.Method.GET
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.{Status, _}

class TradeOgreClient[F[_]: Sync](httpClient: Client[F], config: TradeOgreClientConfig)
    extends ExchangeClient[F]
    with StrictLogging {

  def fetchAllMarkets(): F[Map[Market, MarketInfoResponse]] =
    for {
      uri <- Uri.fromString(s"${config.endpoint}/markets").liftTo[F]
      requestDescription = Request[F](method = GET, uri = uri)
      _ <- Sync[F].delay(logger.info("Fetching all markets"))
      result <- httpClient.expectOr[Seq[Map[Market, MarketInfoResponse]]](requestDescription)(handleErrorResponse("markets"))
      markets <- Sync[F].pure(result.flatten.toMap)
    } yield markets

  def getOrderBook(market: Market): F[OrderBookResponse] =
    for {
      uri <- Uri.fromString(s"${config.endpoint}/orders/$market").liftTo[F]
      request = Request[F](method = GET, uri = uri)
      result <- httpClient.expectOr[OrderBookResponse](request)(handleErrorResponse("orders"))
    } yield result

  private def handleErrorResponse(request: String): Response[F] => F[Throwable] = { err =>
    {
      logger.error(s"Error occurred during fetching $request")
      (err.status match {
        case Status.NotFound => ClientAddressNotFound(s"Requested address is not available :$err")
        case _               => new RuntimeException(s"Something went wrong. Failed HTTP response: $err")
      }).raiseError[F, Throwable]
    }
  }
}
object TradeOgreClient {

  def apply[F[_]: Sync: ConcurrentEffect](
      config: TradeOgreClientConfig
  ): Resource[F, TradeOgreClient[F]] = {
    import scala.concurrent.ExecutionContext.Implicits.global
    for {
      httpClient <- BlazeClientBuilder[F](global)
        .withConnectTimeout(config.timeout)
        .withRequestTimeout(config.timeout)
        .resource
    } yield new TradeOgreClient[F](httpClient, config)
  }
}
