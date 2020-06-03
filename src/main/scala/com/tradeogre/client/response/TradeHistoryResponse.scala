package com.tradeogre.client.response

final case class TradeHistoryResponse(date: Long, `type`: String, price: BigDecimal, quantity: BigDecimal)
