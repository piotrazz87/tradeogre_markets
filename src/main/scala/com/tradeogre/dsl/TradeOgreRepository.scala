package com.tradeogre.dsl

import java.sql.SQLException

import cats.effect.Sync
import com.tradeogre.domain.{MarketInfoIn24Hours, MarketPair}
import com.typesafe.scalalogging.LazyLogging
import doobie.implicits._
import doobie.util.fragment.Fragment
import cats.implicits._

import scala.util.{Failure, Success, Try}

class TradeOgreRepository[F[_]: Sync](implicit connection: DBConnection[F]) extends LazyLogging {

  def save(market: MarketPair, info: MarketInfoIn24Hours): F[Int] = {
    logger.info("Persisting markets")
    val values =
      fr"VALUES (${market.from}, ${market.to}, current_timestamp, ${info.currentPrice}, ${info.volume}, ${info.startingPrice}, ${info.low},${info.high},${info.buyOffer},${info.sellOffer})"
    Try((TradeOgreRepository.InsertFragment ++ values).update.run .transact(connection.transactor)) match {
      case Success(z)  =>
        val sql: F[Either[SQLException, Int]] =z.attemptSql
        for{
        res<-sql
        _=  logger.info(res.toString)
        }yield ()
      case Failure(ex) => logger.info(ex.toString)
    }
    Sync[F].pure(10)
  }

  def findByPair(): F[List[String]] =
    sql"select name from trade_ogre.markets"
      .query[String]
      .to[List]
      .transact(connection.transactor)
}

object TradeOgreRepository {
  private val (columns, columnsWithComma) = {
    val columns = Set(
      "id",
      "from",
      "to",
      "created_date",
      "current_price",
      "24h_volume",
      "24h_start_price",
      "24h_low",
      "24h_high",
      "buy_offer",
      "sell_offer"
    )
    (columns, columns.mkString(","))
  }
  val InsertFragment: Fragment = fr"INSERT INTO market (" ++ Fragment.const(columnsWithComma) ++ fr")"
  def apply[F[_]: Sync](implicit xa: DBConnection[F]) = new TradeOgreRepository[F]()
}
