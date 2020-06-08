package com.tradeogre.domain

final case class MarketPair(from: String, to: String) {
   def asString: String = s"$from-$to"
}
