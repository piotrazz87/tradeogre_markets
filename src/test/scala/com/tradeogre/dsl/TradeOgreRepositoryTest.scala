package com.tradeogre.dsl

import cats.effect.IO
import com.tradeogre.config.DatabaseConfig
import com.tradeogre.domain.{MarketInfoIn24Hours, MarketPair}
import com.tradeogre.{UnitSpec, dsl}
import doobie.implicits._

class TradeOgreRepositoryTest extends UnitSpec {
  private val repository = TradeOgreRepository[IO](dsl.transactor)

  override def beforeAll(): Unit = {
    super.beforeAll()
    DatabaseConfig.initializeDb[IO](inMemoryDBConfig).unsafeRunSync()
  }

  it should "persist to db" in {
    When("persisting market to db")
    repository
      .save(MarketPair("BTC", "GRFT"), MarketInfoIn24Hours(0.001, 2.021, 2.222, 3.021, 5.43, 3.3, 1.2))
      .unsafeRunSync()

    Then("market info should be persisted")
    val result =
      sql"""SELECT start_price,current_price,volume,high,low,sell_offer,buy_offer FROM market"""
        .query[MarketInfoIn24Hours]
        .to[List]
        .transact(dsl.transactor)
        .unsafeRunSync()
    result should contain only MarketInfoIn24Hours(0.001, 2.021, 2.222, 3.021, 5.43, 3.3, 1.2)
  }
}
