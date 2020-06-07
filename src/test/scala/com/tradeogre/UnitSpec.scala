package com.tradeogre

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterAll, GivenWhenThen, TryValues}

trait UnitSpec extends AnyFlatSpec with Matchers with GivenWhenThen with TryValues with BeforeAndAfterAll
