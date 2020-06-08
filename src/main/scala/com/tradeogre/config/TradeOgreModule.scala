package com.tradeogre.config

import cats.effect.{ConcurrentEffect, ContextShift, IO, Resource, Sync}
import com.tradeogre.client.TradeOgreClient
import com.tradeogre.dsl.TradeOgreRepository
import com.tradeogre.service.TradeOgreService
import com.typesafe.scalalogging.LazyLogging
import doobie.util.transactor.Transactor.Aux
import pureconfig.ConfigSource

class TradeOgreModule[F[+ _]: ConcurrentEffect: ContextShift] extends LazyLogging {
  import pureconfig.generic.auto._
  logger.info("Loading tradeogre client config")
  val clientConfig: TradeOgreClientConfig = ConfigSource.default.at("client").loadOrThrow[TradeOgreClientConfig]

  logger.info("Loading database config")
  val dbConfig: DatabaseConfig = ConfigSource.default.at("database").loadOrThrow[DatabaseConfig]
  val transactor: Aux[F, Unit] = DatabaseConfig.dbTransactor(dbConfig)

  logger.info("Creating components")
  val client: Resource[F, TradeOgreClient[F]] = TradeOgreClient[F](clientConfig)
  val repository: TradeOgreRepository[F] = TradeOgreRepository[F](transactor)
  val service: TradeOgreService[F] = TradeOgreService[F](client, repository)
}

object TradeOgreModule {
  def apply[F[+ _]: ConcurrentEffect: ContextShift]() = new TradeOgreModule[F]
}
