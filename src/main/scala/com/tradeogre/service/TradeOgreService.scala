package com.tradeogre.service

import cats.effect.{Resource, Sync}
import com.tradeogre.client.TradeOgreClient

class TradeOgreService[F[_]: Sync](client: Resource[F, TradeOgreClient[F]]) {}

object TradeOgreService {
  def apply[F[_]: Sync](client: Resource[F, TradeOgreClient[F]]) = new TradeOgreService[F](client)
}
