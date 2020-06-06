package com.tradeogre.client

sealed trait ClientError extends RuntimeException with Product with Serializable
case class NotFound(message:String) extends ClientError
