package com.tradeogre.dsl

import cats.effect.Sync
import com.tradeogre.domain.{MarketInfoIn24Hours, MarketPair}
import com.typesafe.scalalogging.LazyLogging
import doobie.`enum`.SqlState
import doobie.implicits._
import doobie.util.transactor.Transactor

class TradeOgreRepository[F[+ _]: Sync](transactor: Transactor[F]) extends Repository[F] with LazyLogging {

  def save(market: MarketPair, info: MarketInfoIn24Hours): F[Either[DBError, Unit]] = {
    logger.info(s"Trying to insert market info for market pair: $market")

    val query =
      sql"INSERT INTO market (base_currency,target_currency, created_date,current_price,volume,start_price,low,high,buy_offer,sell_offer) " ++
        sql"VALUES (${market.from}, ${market.to}, current_timestamp, ${info.currentPrice}, ${info.volume}, ${info.startingPrice}, ${info.low},${info.high},${info.buyOffer},${info.sellOffer})"

    query.update
      .withUniqueGeneratedKeys("id")
      .attemptSomeSqlState {
        case exception: SqlState =>
          logger.error(s"Something is wrong with query of market ${market.asString}", exception.toString)
          DBSyntaxError(s"${exception.value}, for pair ${market.asString}")
      }
      .transact(transactor)
  }
}

object TradeOgreRepository {
  def apply[F[+ _]: Sync](transactor: Transactor[F]) = new TradeOgreRepository[F](transactor)
}
