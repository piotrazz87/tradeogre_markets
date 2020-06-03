package com.tradeogre.service

import cats.effect.{Resource, Sync}
import com.tradeogre.client.TradeOgreClient
import com.tradeogre.client.response.MarketInfoResponse

class TradeOgreService[F[_]: Sync](client: Resource[F, TradeOgreClient[F]]) {

  def fetchInfo(): F[Map[String, MarketInfoResponse]] ={

   val z: F[Map[String, MarketInfoResponse]] = client.use(_.fetchAllMarkets())
     .


  }
}

object TradeOgreService {
  def apply[F[_]: Sync](client: Resource[F, TradeOgreClient[F]]) = new TradeOgreService[F](client)
}
