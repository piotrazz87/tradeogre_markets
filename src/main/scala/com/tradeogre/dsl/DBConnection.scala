package com.tradeogre.dsl

import cats.effect.{Async, ContextShift}
import com.tradeogre.config.DatabaseConfig
import doobie.util.transactor.Transactor
import doobie.util.transactor.Transactor.Aux

class DBConnection[F[_]: Async: ContextShift](databaseConfig: DatabaseConfig) {

  implicit lazy val transactor: Aux[F, Unit] = {
    val DatabaseConfig(driver, url, user, password) = databaseConfig
    Transactor.fromDriverManager[F](driver, url, user, password)
  }
}

object DBConnection {
  def apply[F[_]: Async: ContextShift](databaseConfig: DatabaseConfig) = new DBConnection[F](databaseConfig)
}
