package com.tradeogre.dsl

sealed trait DBError extends Exception with Product with Serializable {
  def message(): String
}

case class DBSyntaxError(error: String) extends DBError {
  override def message(): String = s"SQL syntax is not properly formed - exception code:$error"
}
