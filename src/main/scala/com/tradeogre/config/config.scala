package com.tradeogre.config

import scala.concurrent.duration.Duration

final case class HttpClientProperties(endpoint: String, timeout:Duration)

final case class DatabaseConfig(driver: String, url: String, user: String, password: String)
