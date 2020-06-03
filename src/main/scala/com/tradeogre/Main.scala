package com.tradeogre

import cats.effect.{ExitCode, IO, IOApp}
import com.tradeogre.client.response.MarketInfoResponse
import com.tradeogre.config.TradeOgreModule

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    val module = TradeOgreModule[IO]()

    for {
      _ <- IO.unit
      results <- module.repository.findByPair()
      _ <- IO(println(results.toString()))
      markets: Map[String, MarketInfoResponse] <- module.service.fetchInfo()
      _ <- IO(println(markets))
    } yield ExitCode.Success
  }
}
