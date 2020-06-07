package com.tradeogre.config

import cats.effect.{Async, ContextShift, Sync}
import doobie.util.transactor.Transactor
import doobie.util.transactor.Transactor.Aux
import org.flywaydb.core.Flyway

final case class DatabaseConfig(driver: String, url: String, user: String, password: String)

object DatabaseConfig {

  def dbTransactor[F[_]: Async: ContextShift](dbConfig: DatabaseConfig): Aux[F, Unit] =
    Transactor.fromDriverManager[F](dbConfig.driver, dbConfig.url, dbConfig.user, dbConfig.password)

  def initializeDb[F[_]: Sync](dbConfig: DatabaseConfig): F[Unit] =
    Sync[F].delay {
      Flyway
        .configure()
        .dataSource(dbConfig.url, dbConfig.user, dbConfig.password)
        .load()
        .migrate()
    }
}
