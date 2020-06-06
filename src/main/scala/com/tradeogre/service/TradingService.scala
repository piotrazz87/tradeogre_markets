package com.tradeogre.service

import com.tradeogre.domain.{MarketInfoIn24Hours, MarketPair}
import com.tradeogre.dsl.DBError

trait TradingService[F[+ _]] {
  def fetchBTCMarkets(): F[Map[MarketPair, MarketInfoIn24Hours]]

  def persistMarkets(markets: Map[MarketPair, MarketInfoIn24Hours]): F[List[Either[DBError, Unit]]]
}
