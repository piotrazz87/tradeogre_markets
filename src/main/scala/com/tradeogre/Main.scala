package com.tradeogre

import cats.effect.{ExitCode, IO, IOApp}
import com.tradeogre.config.TradeOgreModule
import com.typesafe.scalalogging.LazyLogging

object Main extends IOApp with LazyLogging {

  override def run(args: List[String]): IO[ExitCode] = {
    logger.info("Starting application")
    val module = TradeOgreModule[IO]()

    for {
      _ <- IO(logger.info("Analyzing markets from TO"))
      markets <- module.service.fetchBTCMarkets()
      _ <- IO(logger.info(s"Fetched ${markets.size} markets of BTC, trying to persist..."))
      persistResult = module.service.persistMarkets(markets).unsafeRunSync()
      _ <- IO(logger.info(s"Persisted ${persistResult.count(_.isRight)} markets"))
    } yield ExitCode.Success
  }
}
