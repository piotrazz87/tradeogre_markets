package com.tradeogre.config

import cats.effect.{ConcurrentEffect, ContextShift, Resource}
import com.tradeogre.client.TradeOgreClient
import com.tradeogre.dsl.TradeOgreRepository
import com.tradeogre.service.TradeOgreService
import com.typesafe.scalalogging.LazyLogging
import doobie.util.transactor.Transactor
import doobie.util.transactor.Transactor.Aux
import pureconfig.ConfigSource
import pureconfig.generic.auto._

class TradeOgreModule[F[+ _]: ConcurrentEffect: ContextShift] extends LazyLogging {

  logger.info("Loading tradeogre client config")
  private val clientConfig = ConfigSource.default.at("client").loadOrThrow[HttpClientProperties]

  logger.info("Loading database config")
  private val dbConfig = ConfigSource.default.at("database").loadOrThrow[DatabaseConfig]
  private val transactor: Aux[F, Unit] = {
    val DatabaseConfig(driver, url, user, password) = dbConfig
    Transactor.fromDriverManager[F](driver, url, user, password)
  }

  logger.info("Creating components")
  lazy val client: Resource[F, TradeOgreClient[F]] = TradeOgreClient[F](clientConfig)
  lazy val repository: TradeOgreRepository[F] = TradeOgreRepository[F](transactor)
  lazy val service: TradeOgreService[F] = TradeOgreService[F](client, repository)
}

object TradeOgreModule {
  def apply[F[+ _]: ConcurrentEffect: ContextShift]() = new TradeOgreModule[F]
}
