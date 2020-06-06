package com.tradeogre

import cats.effect.{ExitCode, IO, IOApp}
import com.tradeogre.config.TradeOgreModule
import com.typesafe.scalalogging.LazyLogging

object Main extends IOApp with LazyLogging {

  override def run(args: List[String]): IO[ExitCode] = {
    logger.info("Starting application")
    val module = TradeOgreModule[IO]()

    for {
      _ <- IO.pure(logger.info("Analyzing markets from TO"))
      markets <- module.service.fetchMarkets()
      _ <- IO.pure(logger.info(markets.toString()))
      _ = markets.map { case (pair, info) => module.repository.save(pair, info) }
    } yield ExitCode.Success
  }
}
