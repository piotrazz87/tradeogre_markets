package com.tradeogre

import cats.effect.{Blocker, ContextShift, IO}
import com.tradeogre.config.DatabaseConfig
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import doobie.util.transactor.Transactor.Aux

import scala.concurrent.ExecutionContext

package object dsl {

  val inMemoryDBConfig: DatabaseConfig = DatabaseConfig(
    "org.postgresql.Driver",
    "jdbc:h2:mem:test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
    "sa",
    ""
  )

  implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  val transactor: Aux[IO, Unit] = Transactor.fromDriverManager[IO](
    inMemoryDBConfig.driver,
    inMemoryDBConfig.url,
    inMemoryDBConfig.user,
    inMemoryDBConfig.password,
    Blocker.liftExecutionContext(ExecutionContexts.synchronous)
  )
}
