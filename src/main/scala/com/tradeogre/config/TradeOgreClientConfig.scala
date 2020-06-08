package com.tradeogre.config

import scala.concurrent.duration.Duration

final case class TradeOgreClientConfig(endpoint: String, timeout: Duration)