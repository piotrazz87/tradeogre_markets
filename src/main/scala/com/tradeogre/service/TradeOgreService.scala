package com.tradeogre.service

import cats.effect.{Resource, Sync}
import cats.implicits._
import com.tradeogre.client.{ExchangeClient, TradeOgreClient}
import com.tradeogre.domain.{MarketInfoIn24Hours, MarketPair}
import com.tradeogre.dsl.{DBError, Repository, TradeOgreRepository}
import com.tradeogre.service.TradeOgreService.MainMarketToAnalyze
import com.typesafe.scalalogging.StrictLogging

class TradeOgreService[F[+ _]: Sync](client: Resource[F, ExchangeClient[F]], repository: Repository[F])
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
    markets.toList.traverse {
      case (pair, info) =>
        for {
          persistResult <- repository.save(pair, info)
          _ = persistResult.handleError(error => logger.error(error.message()))
        } yield persistResult
    }
}

object TradeOgreService {
  private val MainMarketToAnalyze = "BTC"

  def apply[F[+ _]: Sync](client: Resource[F, TradeOgreClient[F]], repository: TradeOgreRepository[F]) =
    new TradeOgreService[F](client, repository)
}
