package com.tradeogre.service

import com.tradeogre.UnitSpec
import com.tradeogre.client.response.MarketInfoResponse
import com.tradeogre.domain.{MarketInfoIn24Hours, MarketPair}

class TradeOgreResponseMapperTest extends UnitSpec {
  "mapper" should "determine pair" in {
    TradeOgreResponseMapper.determineMarketPair("BTC-GRFT") shouldEqual MarketPair("BTC", "GRFT")
  }

  "mapper" should "map market info" in {
    val response = MarketInfoResponse(0.001, 2.021, 3.021, 5.43, 2.222, 3.3, 1.2)
    TradeOgreResponseMapper.mapMarketInfo(response) shouldEqual MarketInfoIn24Hours(0.001, 2.021, 2.222, 3.021, 5.43,
      3.3, 1.2)
  }

  "mapper" should "map multiple infos" in {
    val responses = Map(
      "BTC-GRFT" -> MarketInfoResponse(0.001, 2.021, 3.021, 5.43, 2.222, 3.3, 1.2),
      "BTC-ETH" -> MarketInfoResponse(0.031, 2.0221, 6.021, 1.43, 2.222, 6.3, 1.2)
    )

    TradeOgreResponseMapper.mapMarketsInfo(responses) should contain only (
      MarketPair("BTC", "GRFT") -> MarketInfoIn24Hours(0.001, 2.021, 2.222, 3.021, 5.43, 3.3, 1.2),
      MarketPair("BTC", "ETH") -> MarketInfoIn24Hours(0.031, 2.0221, 2.222, 6.021, 1.43, 6.3, 1.2)
    )
  }
}
