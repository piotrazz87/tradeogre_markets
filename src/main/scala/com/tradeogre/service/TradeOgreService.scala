package com.tradeogre.service

import cats.effect.{Resource, Sync}
import cats.implicits._
import com.tradeogre.client.{TradeOgreClientTrait, TradeOgreClient}
import com.tradeogre.domain.{MarketInfoIn24Hours, MarketPair}
import com.tradeogre.dsl.{DBError, Repository, TradeOgreRepository}
import com.tradeogre.service.TradeOgreService.MainMarketToAnalyze
import com.typesafe.scalalogging.StrictLogging

class TradeOgreService[F[+ _]: Sync](client: Resource[F, TradeOgreClientTrait[F]], repository: Repository[F])
    extends TradingService[F]
    with StrictLogging {

  def fetchBTCMarkets(): F[Map[MarketPair, MarketInfoIn24Hours]] =
    for {
      markets <- client.use(_.fetchAllMarkets())
      _ <- Sync[F].delay(logger.info(s"Fetched all markets ${markets.size}"))
      market <- Sync[F].pure(TradeOgreResponseMapper.mapMarketsInfo(markets))
      btcMarkets = market.filter { case (pair, _) => pair.from == MainMarketToAnalyze }
      _ <- Sync[F].delay(logger.info(s"BTC markets ${btcMarkets.size}"))
    } yield btcMarkets

  def persistMarkets(markets: Map[MarketPair, MarketInfoIn24Hours]): F[List[Either[DBError, Unit]]] =
    markets.toList.traverse({ case (pair, info) => repository.save(pair, info) })

  def calculateArbitrage(): Unit = {}
}

object TradeOgreService {
  private val MainMarketToAnalyze = "BTC"

  def apply[F[+ _]: Sync](client: Resource[F, TradeOgreClient[F]], repository: TradeOgreRepository[F]) =
    new TradeOgreService[F](client, repository)
}
