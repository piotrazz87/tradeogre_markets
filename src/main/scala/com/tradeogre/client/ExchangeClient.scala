package com.tradeogre.client

import com.tradeogre.client.response.{MarketInfoResponse, OrderBookResponse}

trait ExchangeClient[F[_]] {
  def fetchAllMarkets(): F[Map[Market, MarketInfoResponse]]
  def getOrderBook(market: Market): F[OrderBookResponse]
}
