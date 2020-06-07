package com.tradeogre.config

import scala.concurrent.duration.Duration

final case class HttpClientConfig(endpoint: String, timeout: Duration)