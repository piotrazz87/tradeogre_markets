package com.tradeogre.domain

case class MarketPair(from: String, to: String) {
   def asString: String = s"$from-$to"
}
