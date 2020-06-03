package com.tradeogre.dsl

import cats.effect.Sync
import doobie.implicits._

class TradeOgreRepository[F[_]: Sync](implicit connection: DBConnection[F]) {
  def save(): F[List[String]] = {
    sql"select name from trade_ogre.markets"
      .query[String]
      .to[List]
      .transact(connection.transactor)
  }

  def findByPair(): F[List[String]] =
    sql"select name from trade_ogre.markets"
      .query[String]
      .to[List]
      .transact(connection.transactor)
}

object TradeOgreRepository {
  def apply[F[_]: Sync](implicit xa: DBConnection[F]) = new TradeOgreRepository[F]()
}
