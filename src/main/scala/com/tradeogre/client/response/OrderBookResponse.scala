package com.tradeogre.client.response

final case class OrderBookResponse(buy: Map[String, String], sell: Map[String, String], success: Boolean)
