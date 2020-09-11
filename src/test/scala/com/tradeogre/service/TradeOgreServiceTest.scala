package com.tradeogre.service

import cats.effect.{IO, Resource}
import com.tradeogre.client.response.{MarketInfoResponse, OrderBookResponse}
import com.tradeogre.client.{ClientAddressNotFound, ExchangeClient, Market}
import com.tradeogre.{domain, UnitSpec}
import com.tradeogre.domain.{MarketInfoIn24Hours, MarketPair}
import com.tradeogre.dsl.{DBError, DBSyntaxError, Repository}
import com.tradeogre.service.TradeOgreServiceTest.{client, failingClient, failingRepository, repository}
import cats.syntax.either
import cats.implicits._

class TradeOgreServiceTest extends UnitSpec {

  it should "fetch markets" in {
    Given("client with mocked response")
    val service = new TradeOgreService[IO](Resource.liftF(IO(client)), repository)

    When("fetching markets")
    Then("mapped markets should be returned")
    service.fetchBTCMarkets().unsafeRunSync() should contain only (
      MarketPair("BTC", "GRFT") -> MarketInfoIn24Hours(0.001, 2.021, 2.222, 3.021, 5.43, 3.3, 1.2),
      MarketPair("BTC", "ETH") -> MarketInfoIn24Hours(0.031, 2.0221, 2.222, 6.021, 1.43, 6.3, 1.2)
    )
  }

  it should "raise error when client sent error" in {
    Given("failing client")
    val service = new TradeOgreService[IO](Resource.liftF(IO(failingClient)), repository)

    When("fetching btc markets")
    Then("error should be raised")
    service.fetchBTCMarkets().attempt.unsafeRunSync() shouldEqual Left(
      ClientAddressNotFound("http://tr.com not exists")
    )
  }

  it should "persist markets" in {
    Given("markets to persist with working repo")
    val marketsToPersist = Map(
      MarketPair("BTC", "GRFT") -> MarketInfoIn24Hours(0.001, 2.021, 2.222, 3.021, 5.43, 3.3, 1.2),
      MarketPair("BTC", "ETH") -> MarketInfoIn24Hours(0.031, 2.0221, 2.222, 6.021, 1.43, 6.3, 1.2)
    )

    val service = new TradeOgreService[IO](Resource.liftF(IO(failingClient)), repository)

    When("persisting markets")
    Then("no error should be raised")
    service.persistMarkets(marketsToPersist).attempt.unsafeRunSync() shouldEqual (List(().asRight, ().asRight)).asRight
  }

  it should "fail while persisting markets" in {
    Given("markets to persist with failing repo")
    val marketsToPersist = Map(
      MarketPair("BTC", "GRFT") -> MarketInfoIn24Hours(0.001, 2.021, 2.222, 3.021, 5.43, 3.3, 1.2),
      MarketPair("BTC", "ETH") -> MarketInfoIn24Hours(0.031, 2.0221, 2.222, 6.021, 1.43, 6.3, 1.2)
    )
    val service = new TradeOgreService[IO](Resource.liftF(IO(failingClient)), failingRepository)

    When("persisting markets")
    Then("raise syntax error")
    service.persistMarkets(marketsToPersist).attempt.unsafeRunSync() shouldEqual Right(
      List(
        Left(DBSyntaxError("4281")),
        Left(DBSyntaxError("4281"))
      )
    )
  }
}

private object TradeOgreServiceTest {

  private val client: ExchangeClient[IO] = new ExchangeClient[IO]() {
    override def fetchAllMarkets(): IO[Map[Market, MarketInfoResponse]] =
      IO.pure(
        Map(
          "BTC-GRFT" -> MarketInfoResponse(0.001, 2.021, 3.021, 5.43, 2.222, 3.3, 1.2),
          "BTC-ETH" -> MarketInfoResponse(0.031, 2.0221, 6.021, 1.43, 2.222, 6.3, 1.2)
        )
      )
    //TODO:implement when real code will be used
    override def getOrderBook(market: Market): IO[OrderBookResponse] =
      IO.pure(OrderBookResponse(Map.empty, Map.empty, success = true))
  }
  private val failingClient: ExchangeClient[IO] = new ExchangeClient[IO]() {
    override def fetchAllMarkets(): IO[Map[Market, MarketInfoResponse]] =
      IO.raiseError(ClientAddressNotFound("http://tr.com not exists"))

    //TODO:implement when real code will be used
    override def getOrderBook(market: Market): IO[OrderBookResponse] =
      IO.pure(OrderBookResponse(Map.empty, Map.empty, success = true))
  }
  
  private val repository: Repository[IO] = (market: domain.MarketPair, info: MarketInfoIn24Hours) => IO.pure(Right(()))

  private val failingRepository: Repository[IO] = (_: domain.MarketPair, _: MarketInfoIn24Hours) =>
    IO.pure(Left(DBSyntaxError("4281")))
}
