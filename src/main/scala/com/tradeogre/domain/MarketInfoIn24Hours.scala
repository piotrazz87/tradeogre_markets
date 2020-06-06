package com.tradeogre.domain

final case class MarketInfoIn24Hours(
    startingPrice: BigDecimal,
    currentPrice: BigDecimal,
    volume: BigDecimal,
    high: BigDecimal,
    low: BigDecimal,
    sellOffer: BigDecimal,
    buyOffer: BigDecimal
)
