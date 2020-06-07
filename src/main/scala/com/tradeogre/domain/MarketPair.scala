package com.tradeogre.domain

case class MarketPair(from: String, to: String) {
  override def toString: String = s"$from-$to"
}
