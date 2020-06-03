package com.tradeogre.config

import cats.effect.{ConcurrentEffect, ContextShift, Resource}
import com.tradeogre.client.{HttpClientProperties, TradeOgreClient}
import com.tradeogre.dsl.{DBConfig, DBConnection, TradeOgreRepository}
import com.tradeogre.service.TradeOgreService

class TradeOgreModule[F[_]: ConcurrentEffect: ContextShift] {
  private val dbConfig = DBConfig(
    driver = "org.postgresql.Driver",
    url = "jdbc:postgresql://localhost:5432/questionnaire_system",
    user = "postgres_user",
    password = "postgres_user"
  )

  private val clientConfig = HttpClientProperties("https://tradeogre.com/api/v1")
  implicit private val dbConnection: DBConnection[F] = DBConnection[F](dbConfig)

  lazy val client: Resource[F, TradeOgreClient[F]] = TradeOgreClient[F](clientConfig)
  lazy val repository: TradeOgreRepository[F] = TradeOgreRepository[F]
  lazy val service: TradeOgreService[F] = TradeOgreService[F](client)
}

object TradeOgreModule {
  def apply[F[_]: ConcurrentEffect: ContextShift]() = new TradeOgreModule[F]
}
