package com.tradeogre.service

import com.tradeogre.client.response.MarketInfoResponse
import com.tradeogre.domain.{MarketInfoIn24Hours, MarketPair}

object TradeOgreResponseMapper {

  def mapMarketsInfo(markets: Map[String, MarketInfoResponse]): Map[MarketPair, MarketInfoIn24Hours] =
    markets.map { case (pair, response) => determineMarketPair(pair) -> mapMarketInfo(response) }

  private[service] def mapMarketInfo(response: MarketInfoResponse): MarketInfoIn24Hours = {
    val MarketInfoResponse(initialprice, price, high, low, volume, bid, ask) = response
    MarketInfoIn24Hours(initialprice, price, volume, high, low, bid, ask)
  }

  private[service] def determineMarketPair(market: String): MarketPair = {
    val Array(from, to) = market.split("-")
    MarketPair(from, to)
  }
}
