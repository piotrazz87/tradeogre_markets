package com.tradeogre.client

sealed trait ClientError extends RuntimeException with Product with Serializable
case class ClientAddressNotFound(message: String) extends ClientError
