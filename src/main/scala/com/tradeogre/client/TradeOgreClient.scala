package com.tradeogre.client

import cats.effect.{ConcurrentEffect, Resource, Sync}
import cats.implicits._
import com.tradeogre.client.response.{MarketInfoResponse, OrderBookResponse, TickerResponse, TradeHistoryResponse}
import com.tradeogre.config.HttpClientProperties
import com.typesafe.scalalogging.{LazyLogging, StrictLogging}
import io.circe.generic.auto._
import org.http4s.Method.GET
import org.http4s._
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder

class TradeOgreClient[F[_]: Sync](httpClient: Client[F], config: HttpClientProperties)extends StrictLogging {
  type MarketPair = String

  def fetchAllMarkets(): F[Map[MarketPair, MarketInfoResponse]] = {
    for {
      uri <- Uri.fromString(s"${config.endpoint}/markets").liftTo[F]
      request = Request[F](method = GET, uri = uri)
      _= logger.info("Fetching all markets")
      result <- httpClient.expect[Seq[Map[MarketPair, MarketInfoResponse]]](request)
    } yield result.flatten.toMap
  }

  def getOrderBook(market: MarketPair): F[OrderBookResponse] =
    for {
      uri <- Uri.fromString(s"${config.endpoint}/orders/$market").liftTo[F]
      request = Request[F](method = GET, uri = uri)
      result <- httpClient.expect[OrderBookResponse](request)
    } yield result

  def getTradeHistory(market: MarketPair): F[Seq[TradeHistoryResponse]] =
    for {
      uri <- Uri.fromString(s"${config.endpoint}/history/$market").liftTo[F]
      request = Request[F](method = GET, uri = uri)
      result <- httpClient.expect[Seq[TradeHistoryResponse]](request)
    } yield result

  def getTicker(market: MarketPair): F[TickerResponse] =
    for {
      uri <- Uri.fromString(s"${config.endpoint}/ticker/$market").liftTo[F]
      request = Request[F](method = GET, uri = uri)
      result <- httpClient.expect[TickerResponse](request)
    } yield result
}

object TradeOgreClient {

  def apply[F[_]: Sync: ConcurrentEffect](
      config: HttpClientProperties
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
