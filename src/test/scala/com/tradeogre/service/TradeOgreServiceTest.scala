package com.tradeogre.service

import cats.effect.{IO, Resource}
import com.tradeogre.client.response.{MarketInfoResponse, OrderBookResponse}
import com.tradeogre.client.{Market, NotFound, TradeOgreClientTrait}
import com.tradeogre.domain
import com.tradeogre.domain.{MarketInfoIn24Hours, MarketPair}
import com.tradeogre.dsl.{DBError, Repository}

class TradeOgreServiceTest extends UnitSpec {

  it should "fetch markets" in {
    val service =
      new TradeOgreService[IO](Resource.liftF(IO(TradeOgreServiceTest.client)), TradeOgreServiceTest.repository)
    service.fetchBTCMarkets().unsafeRunSync() should contain only (
      MarketPair("BTC", "GRFT") -> MarketInfoIn24Hours(0.001, 2.021, 2.222, 3.021, 5.43, 3.3, 1.2),
      MarketPair("BTC", "ETH") -> MarketInfoIn24Hours(0.031, 2.0221, 2.222, 6.021, 1.43, 6.3, 1.2)
    )
  }

  it should "raise error when client sent error" in {
    val service =
      new TradeOgreService[IO](Resource.liftF(IO(TradeOgreServiceTest.failingClient)), TradeOgreServiceTest.repository)

    service.fetchBTCMarkets().attempt.unsafeRunSync() shouldEqual Left(NotFound("http://tr.com not exists"))
  }
}

private object TradeOgreServiceTest {

  private val client: TradeOgreClientTrait[IO] = new TradeOgreClientTrait[IO]() {
    override def fetchAllMarkets(): IO[Map[Market, MarketInfoResponse]] =
      IO.pure(
        Map(
          "BTC-GRFT" -> MarketInfoResponse(0.001, 2.021, 3.021, 5.43, 2.222, 3.3, 1.2),
          "BTC-ETH" -> MarketInfoResponse(0.031, 2.0221, 6.021, 1.43, 2.222, 6.3, 1.2)
        )
      )
    //TODO:implement when real code will be used
    override def getOrderBook(market: Market): IO[OrderBookResponse] =
      IO.pure(OrderBookResponse(Map.empty, Map.empty, "true"))
  }
  private val failingClient: TradeOgreClientTrait[IO] = new TradeOgreClientTrait[IO]() {
    override def fetchAllMarkets(): IO[Map[Market, MarketInfoResponse]] =
      IO.raiseError(NotFound("http://tr.com not exists"))

    //TODO:implement when real code will be used
    override def getOrderBook(market: Market): IO[OrderBookResponse] =
      IO.pure(OrderBookResponse(Map.empty, Map.empty, "true"))
  }
  private val repository: Repository[IO] = new Repository[IO] {
    override def save(market: domain.MarketPair, info: MarketInfoIn24Hours): IO[Either[DBError, Unit]] = ???

    override def findByPair(marketPair: domain.MarketPair): IO[List[MarketInfoIn24Hours]] = ???
  }
}
