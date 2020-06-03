package com.tradeogre.dsl

import cats.effect.{Async, ContextShift}
import doobie.util.transactor.Transactor
import doobie.util.transactor.Transactor.Aux

class DBConnection[F[_]: Async: ContextShift](dbConfig: DBConfig) {

  implicit lazy val transactor: Aux[F, Unit] = {
    val DBConfig(driver, url, user, password) = dbConfig
    Transactor.fromDriverManager[F](driver, url, user, password)
  }
}

object DBConnection {
  def apply[F[_]: Async: ContextShift](config: DBConfig) = new DBConnection[F](config)
}

case class DBConfig(driver: String, url: String, user: String, password: String)
