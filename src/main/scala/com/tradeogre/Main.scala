package com.tradeogre

import cats.effect.{ExitCode, IO, IOApp}
import com.tradeogre.config.TradeOgreModule

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    val module = TradeOgreModule[IO]()

    for {
      _ <- IO.unit
      results <- module.repository.findByPair()
      _ <- IO(println(results.toString()))
    } yield ExitCode.Success
  }
}
