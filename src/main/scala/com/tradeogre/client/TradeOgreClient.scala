package com.tradeogre.client

import cats.effect.{ConcurrentEffect, Resource, Sync}
import cats.implicits._
import com.tradeogre.client.response.{MarketInfoResponse, OrderBookResponse, TickerResponse, TradeHistoryResponse}
import io.circe.generic.auto._
import org.http4s.Method.GET
import org.http4s._
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder

class TradeOgreClient[F[_]: Sync](httpClient: Client[F], config: HttpClientProperties) {
  type MarketPair = String

  def fetchAllMarkets(): F[Map[MarketPair, MarketInfoResponse]] =
    for {
      uri <- Uri.fromString(s"${config.endpointAddress}/markets").liftTo[F]
      request = Request[F](method = GET, uri = uri)
      result <- httpClient.expect[Seq[Map[MarketPair, MarketInfoResponse]]](request)
    } yield result.flatten.toMap

  def getOrderBook(market: MarketPair): F[OrderBookResponse] =
    for {
      uri <- Uri.fromString(s"${config.endpointAddress}/orders/$market").liftTo[F]
      request = Request[F](method = GET, uri = uri)
      result <- httpClient.expect[OrderBookResponse](request)
    } yield result

  def getTradeHistory(market: MarketPair): F[Seq[TradeHistoryResponse]] =
    for {
      uri <- Uri.fromString(s"${config.endpointAddress}/history/$market").liftTo[F]
      request = Request[F](method = GET, uri = uri)
      result <- httpClient.expect[Seq[TradeHistoryResponse]](request)
    } yield result

  def getTicker(market: MarketPair): F[TickerResponse] =
    for {
      uri <- Uri.fromString(s"${config.endpointAddress}/ticker/$market").liftTo[F]
      request = Request[F](method = GET, uri = uri)
      result <- httpClient.expect[TickerResponse](request)
    } yield result
}

object TradeOgreClient {

  def apply[F[_]: Sync: ConcurrentEffect](
      config: HttpClientProperties
  ): Resource[F, TradeOgreClient[F]] = {
    import scala.concurrent.ExecutionContext.Implicits.global
    import scala.concurrent.duration._
    for {
      httpClient <- BlazeClientBuilder[F](global)
        .withConnectTimeout(10 seconds)
        .withRequestTimeout(10 seconds)
        .resource
    } yield new TradeOgreClient[F](httpClient, config)
  }
}

case class HttpClientProperties(endpointAddress: String)
