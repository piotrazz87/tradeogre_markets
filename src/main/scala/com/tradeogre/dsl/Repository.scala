package com.tradeogre.dsl

import com.tradeogre.domain.{MarketInfoIn24Hours, MarketPair}

trait Repository[F[_]] {
  def save(market: MarketPair, info: MarketInfoIn24Hours): F[Either[DBError, Unit]]
  def findByPair(marketPair: MarketPair): F[List[MarketInfoIn24Hours]]
}
