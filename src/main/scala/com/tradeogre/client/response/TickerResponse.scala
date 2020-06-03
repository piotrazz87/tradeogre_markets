package com.tradeogre.client.response

final case class TickerResponse(
    success: Boolean,
    initialprice: BigDecimal,
    price: BigDecimal,
    high: BigDecimal,
    low: BigDecimal,
    volume: BigDecimal,
    bid: BigDecimal,
    ask: BigDecimal
)
