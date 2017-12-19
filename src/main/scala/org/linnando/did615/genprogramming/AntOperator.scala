package org.linnando.did615.genprogramming

import scala.util.Random

object AntOperator extends Enumeration {
  type AntOperator = Value
  val Val, Arg, Sensor, Add, Sub, Mul, Div, If = Value

  def random(generator: Random): AntOperator = AntOperator(generator.nextInt(AntOperator.maxId))
}
