package com.tradeogre.config

import cats.effect.{ConcurrentEffect, ContextShift, Resource}
import com.tradeogre.client.TradeOgreClient
import com.tradeogre.dsl.TradeOgreRepository
import com.tradeogre.service.TradeOgreService
import com.typesafe.scalalogging.LazyLogging
import pureconfig.ConfigSource
import pureconfig.error.ConfigReaderFailures


class TradeOgreModule[F[+ _]: ConcurrentEffect: ContextShift] extends LazyLogging {

  logger.info("Loading configs")
  val (clientConfig: HttpClientConfig, dbConfig: DatabaseConfig) =
    loadConfigs().fold(ex => logger.error("There is problem with loading configs", ex.toList.mkString("\n")), identity)
  private val transactor = DatabaseConfig.dbTransactor(dbConfig)

  logger.info("Creating components")
  val client: Resource[F, TradeOgreClient[F]] = TradeOgreClient[F](clientConfig)
  val repository: TradeOgreRepository[F] = TradeOgreRepository[F](transactor)
  val service: TradeOgreService[F] = TradeOgreService[F](client, repository)

  private def loadConfigs(): Either[ConfigReaderFailures, (HttpClientConfig, DatabaseConfig)] = {
    import pureconfig.generic.auto._
    for {
      clientConfig <- ConfigSource.default.at("client").load[HttpClientConfig]
      dbConfig <- ConfigSource.default.at("database").load[DatabaseConfig]
    } yield (clientConfig, dbConfig)
  }
}

object TradeOgreModule {
  def apply[F[+ _]: ConcurrentEffect: ContextShift]() = new TradeOgreModule[F]
}
