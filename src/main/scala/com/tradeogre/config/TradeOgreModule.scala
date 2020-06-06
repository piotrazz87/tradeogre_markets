package com.tradeogre.config

import cats.effect.{ConcurrentEffect, ContextShift, Resource}
import com.tradeogre.client.TradeOgreClient
import com.tradeogre.dsl.{DBConnection, TradeOgreRepository}
import com.tradeogre.service.TradeOgreService
import com.typesafe.scalalogging.LazyLogging
import pureconfig.ConfigSource

class TradeOgreModule[F[_]: ConcurrentEffect: ContextShift] extends LazyLogging{
  import pureconfig.generic.auto._

  logger.info("Loading tradeogre client config")
  private val clientConfig = ConfigSource.default.at("client").loadOrThrow[HttpClientProperties]

  logger.info("Loading database config")
  private val dbConfig = ConfigSource.default.at("database").loadOrThrow[DatabaseConfig]
  implicit private val dbConnection: DBConnection[F] = DBConnection[F](dbConfig)

  logger.info("Creating components")
  lazy val client: Resource[F, TradeOgreClient[F]] = TradeOgreClient[F](clientConfig)
  lazy val repository: TradeOgreRepository[F] = TradeOgreRepository[F]
  lazy val service: TradeOgreService[F] = TradeOgreService[F](client)
}

object TradeOgreModule {
  def apply[F[_]: ConcurrentEffect: ContextShift]() = new TradeOgreModule[F]
}
