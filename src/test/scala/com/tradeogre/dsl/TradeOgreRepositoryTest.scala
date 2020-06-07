package com.tradeogre.dsl

import com.tradeogre.UnitSpec
import com.tradeogre.domain.{MarketInfoIn24Hours, MarketPair}
import doobie.free.connection.ConnectionIO
import doobie.implicits._

class TradeOgreRepositoryTest extends UnitSpec {
  private val repository = TradeOgreRepository(transactor)

  override def beforeAll(): Unit = {
    super.beforeAll()
    createMarketsTable.transact(transactor).unsafeRunSync()
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
        .transact(transactor)
        .unsafeRunSync()
    result should contain only MarketInfoIn24Hours(0.001, 2.021, 2.222, 3.021, 5.43, 3.3, 1.2)
  }

  //TODO:delete it when Flyway will be added
  private val createMarketsTable: ConnectionIO[Int] =
    sql"""
   create table market(
	id serial not null
		constraint market_pkey
			primary key,
	base_currency varchar,
	target_currency varchar,
	created_date timestamp,
	current_price numeric(10,8),
	volume numeric(10,8),
	start_price numeric(10,8),
	low numeric(10,8),
	high numeric(10,8),
	buy_offer numeric(10,8),
	sell_offer numeric(10,8)
)
      """.update.run
}
