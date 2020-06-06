package com.tradeogre.service

import cats.effect.{Resource, Sync}
import cats.implicits._
import com.tradeogre.client.TradeOgreClient
import com.tradeogre.client.response.OrderBookResponse
import com.tradeogre.domain.{MarketInfoIn24Hours, MarketPair}
import com.typesafe.scalalogging.StrictLogging

class TradeOgreService[F[_]: Sync](client: Resource[F, TradeOgreClient[F]]) extends StrictLogging {

  def fetchMarkets(): F[Map[MarketPair, MarketInfoIn24Hours]] =
    for {
      markets <- client.use(_.fetchAllMarkets())
      _ <- Sync[F].pure(logger.info(s"Fetched all markets ${markets.size}"))
      market <- Sync[F].pure(TradeOgreResponseMapper.mapMarketsInfo(markets))
      btcMarkets = market.filter { case (pair, _) => pair.from == TradeOgreService.MainMarketToAnalyze }
      _ <- Sync[F].pure(logger.info(s"BTC markets ${btcMarkets.size}"))
    } yield btcMarkets

  def fetchOrderBook(market: MarketPair): F[OrderBookResponse] =
    for { orders <- client.use(_.getOrderBook(market.toString)) } yield orders
}

object TradeOgreService {
  private val MainMarketToAnalyze = "BTC"

  def apply[F[_]: Sync](client: Resource[F, TradeOgreClient[F]]) = new TradeOgreService[F](client)
}
