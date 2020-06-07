package com.tradeogre.client

import cats.effect.IO
import com.tradeogre.UnitSpec
import com.tradeogre.client.TradeOgreClientTest.MockedHttpClient
import com.tradeogre.client.response.{MarketInfoResponse, OrderBookResponse}
import com.tradeogre.config.HttpClientProperties
import org.http4s.Method.GET
import org.http4s.Status.NotFound
import org.http4s.client.Client
import org.http4s.{HttpApp, Request, Response, Status, _}

import scala.concurrent.duration._

class TradeOgreClientTest extends UnitSpec {
  private val properClientProperties = HttpClientProperties("http://tradeogre/api", 10 seconds)
  private val wrongClientProperties = HttpClientProperties("http://tr", 10 seconds)

  it should "fetch markets" in {
    val tradeOgreClient = new TradeOgreClient[IO](MockedHttpClient, properClientProperties)

    tradeOgreClient.fetchAllMarkets().attempt.unsafeRunSync() shouldEqual Right(
      Map(
        "BTC-AEON" -> MarketInfoResponse(0.00022004, 0.00025992, 0.00025992, 0.00022003, 0.00359066, 0.00022456,
          0.00025993),
        "BTC-BTCP" -> MarketInfoResponse(0.00300573, 0.00325000, 0.00379000, 0.00300010, 0.04753022, 0.00300099,
          0.00325000)
      )
    )
  }

  it should "fetch orders" in {
    val tradeOgreClient = new TradeOgreClient[IO](MockedHttpClient, properClientProperties)

    tradeOgreClient.getOrderBook("BTC-AEON").attempt.unsafeRunSync() shouldEqual Right(
      OrderBookResponse(
        Map("0.02425501" -> "36.46986607", "0.02425502" -> "93.64201137"),
        Map("0.02427176" -> "737.34633975", "0.02427232" -> "94.30483300"),
        success = true
      )
    )
  }

  it should "raise error when not found" in {
    val tradeOgreClient = new TradeOgreClient[IO](MockedHttpClient, wrongClientProperties)
    tradeOgreClient.fetchAllMarkets().attempt.unsafeRunSync() shouldEqual Left(
      ClientAddressNotFound("Requested address is not available :Response(status=404, headers=Headers())")
    )
  }
  it should "raise error when not found in order book request" in {
    val tradeOgreClient = new TradeOgreClient[IO](MockedHttpClient, wrongClientProperties)
    tradeOgreClient.getOrderBook("BTC-AEON").attempt.unsafeRunSync() shouldEqual Left(
      ClientAddressNotFound("Requested address is not available :Response(status=404, headers=Headers())")
    )
  }
}

private object TradeOgreClientTest {

  private val MockedHttpClient = {
    val marketsResponse =
      """[{"BTC-AEON":{"initialprice":"0.00022004","price":"0.00025992","high":"0.00025992","low":"0.00022003","volume":"0.00359066","bid":"0.00022456","ask":"0.00025993"}},
        |{"BTC-BTCP":{"initialprice":"0.00300573","price":"0.00325000","high":"0.00379000","low":"0.00300010","volume":"0.04753022","bid":"0.00300099","ask":"0.00325000"}}]
        |""".stripMargin

    val ordersResponse =
      """{"success":true,
        |"buy":{"0.02425501":"36.46986607","0.02425502":"93.64201137"},
        |"sell":{"0.02427176":"737.34633975","0.02427232":"94.30483300"}
        |}
        |""".stripMargin

    val failedMarketsUri = Uri.unsafeFromString("http://tr/markets")
    val failedOrdersUri = Uri.unsafeFromString("http://tr/orders/BTC-AEON")
    val marketsUri = Uri.unsafeFromString("http://tradeogre/api/markets")
    val ordersUri = Uri.unsafeFromString("http://tradeogre/api/orders/BTC-AEON")

    val app = HttpApp[IO]({
      case Request(GET, uri, _, _, _, _) if uri == marketsUri =>
        IO.pure(Response[IO](Status.Ok).withEntity(marketsResponse))
      case Request(GET, uri, _, _, _, _) if uri == failedMarketsUri => IO.pure(Response[IO](NotFound))
      case Request(GET, uri, _, _, _, _) if uri == ordersUri =>
        IO.pure(Response[IO](Status.Ok).withEntity(ordersResponse))
      case Request(GET, uri, _, _, _, _) if uri == failedOrdersUri =>
        IO.pure(Response[IO](NotFound))
    })

    Client.fromHttpApp(app)
  }
}
