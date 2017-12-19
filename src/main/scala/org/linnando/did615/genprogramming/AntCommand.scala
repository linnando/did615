package org.linnando.did615.genprogramming

import org.linnando.did615.genprogramming.AntOperator.AntOperator

import scala.util.Random

case class AntCommand(operator: AntOperator, value: Byte, scope: Int) {
  def mutation(generator: Random): AntCommand =
    if (generator.nextBoolean()) copy(operator = AntOperator.random(generator))
    else copy(value = generator.nextInt(256).toByte)

  override def toString: String = operator match {
    case AntOperator.Val => value.toString
    case AntOperator.Arg => "ARG"
    case AntOperator.Sensor => "SENSOR"
    case AntOperator.Add => "+"
    case AntOperator.Sub => "-"
    case AntOperator.Mul => "*"
    case AntOperator.Div => "/"
    case AntOperator.If => "IF"
  }
}

object AntCommand {
  def random(minScope: Int, maxScope: Int, generator: Random): AntCommand = AntCommand(
    AntOperator.random(generator),
    generator.nextInt(256).toByte,
    minScope + generator.nextInt(maxScope - minScope + 1)
  )
}
