package com.tradeogre.client.response

final case class MarketInfoResponse(
    initialprice: BigDecimal,
    price: BigDecimal,
    high: BigDecimal,
    low: BigDecimal,
    volume: BigDecimal,
    bid: BigDecimal,
    ask: BigDecimal
)


