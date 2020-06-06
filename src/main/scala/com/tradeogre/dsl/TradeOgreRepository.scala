package com.tradeogre.dsl

import cats.effect.Sync
import com.tradeogre.domain.{MarketInfoIn24Hours, MarketPair}
import com.typesafe.scalalogging.LazyLogging
import doobie.`enum`.SqlState
import doobie.implicits._

class TradeOgreRepository[F[+ _]: Sync](implicit connection: DBConnection[F]) extends Repository[F] with LazyLogging {

  def save(market: MarketPair, info: MarketInfoIn24Hours): F[Either[DBError, Unit]] = {
    logger.info(s"Trying to insert market info for market pair: $market")

    val query =
      sql"INSERT INTO trade_ogre.market (base_currency,target_currency, created_date,current_price,volume,start_price,low,high,buy_offer,sell_offer) " ++
        sql"VALUES (${market.from}, ${market.to}, current_timestamp, ${info.currentPrice}, ${info.volume}, ${info.startingPrice}, ${info.low},${info.high},${info.buyOffer},${info.sellOffer})"

    query.update
      .withUniqueGeneratedKeys("id")
      .attemptSomeSqlState {
        case exception: SqlState =>
          logger.error("Something is wrong with query", exception.toString)
          SyntaxError(exception.value)
      }
      .transact(connection.transactor)
  }

  def findByPair(marketPair: MarketPair): F[List[MarketInfoIn24Hours]] =
    sql"SELECT * FROM trade_ogre.market WHERE target_currency=${marketPair.to} "
      .query[MarketInfoIn24Hours]
      .to[List]
      .transact(connection.transactor)
}

object TradeOgreRepository {
  def apply[F[+ _]: Sync](implicit xa: DBConnection[F]) = new TradeOgreRepository[F]()
}
