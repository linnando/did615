package org.linnando.did615.genprogramming

import org.linnando.did615.genetic.Individual

import scala.util.Random

case class AntProgram(commands: Vector[AntCommand]) extends Individual {
  override lazy val fitness: Double =
    try {
      execute(AntProgram.world)
    }
    catch {
      case _: ArithmeticException => 0.0
    }

  private def execute(world: AntWorld): Int = {
    def _execute(state: ExecutionState): ExecutionState = state.remainingSteps match {
      case 0 => state
      case _ => _execute(nextState(state))
    }

    val initialState = ExecutionState(world, Set((0, 0)), (0, 0), (0, 1), 0, world.allowedSteps)
    val finalState = _execute(initialState)
    finalState.visited.toList.map(world(_).toInt).sum
  }

  def nextState(state: ExecutionState): ExecutionState = {
    val result = calculate(0, state.memory, state.sense)
    val (memory, action) =
      if (result >= 0 || result % 3 == 0) ((result / 3).toByte,  result % 3)
      else ((result / 3 - 1).toByte,  result % 3 + 3)
    action match {
      case 0 => state.move.copy(memory = memory)
      case 1 => state.turnLeft.copy(memory = memory)
      case 2 => state.turnRight.copy(memory = memory)
    }
  }

  private def calculate(i: Int, arg: Byte, sensor: Byte): Byte = commands(i).operator match {
    case AntOperator.Val => commands(i).value
    case AntOperator.Arg => arg
    case AntOperator.Sensor => sensor
    case AntOperator.Add =>
      val subtrees = findSubtrees(i)
      subtrees.length match {
        case 0 => 0
        case 1 => calculate(subtrees.head, arg, sensor)
        case _ => (calculate(subtrees.head, arg, sensor) + calculate(subtrees(1), arg, sensor)).toByte
      }
    case AntOperator.Sub =>
      val subtrees = findSubtrees(i)
      subtrees.length match {
        case 0 => 0
        case 1 => calculate(subtrees.head, arg, sensor)
        case _ => (calculate(subtrees.head, arg, sensor) - calculate(subtrees(1), arg, sensor)).toByte
      }
    case AntOperator.Mul =>
      val subtrees = findSubtrees(i)
      subtrees.length match {
        case 0 => 1
        case 1 => calculate(subtrees.head, arg, sensor)
        case _ => (calculate(subtrees.head, arg, sensor) * calculate(subtrees(1), arg, sensor)).toByte
      }
    case AntOperator.Div =>
      val subtrees = findSubtrees(i)
      subtrees.length match {
        case 0 => 1
        case 1 => calculate(subtrees.head, arg, sensor)
        case _ => (calculate(subtrees.head, arg, sensor) / calculate(subtrees(1), arg, sensor)).toByte
      }
    case AntOperator.If =>
      val subtrees = findSubtrees(i)
      subtrees.length match {
        case 0 => 0
        case 1 => 0
        case 2 =>
          if (calculate(subtrees.head, arg, sensor) >= 0) calculate(subtrees(1), arg, sensor)
          else 0
        case _ =>
          if (calculate(subtrees.head, arg, sensor) >= 0) calculate(subtrees(1), arg, sensor)
          else calculate(subtrees(2), arg, sensor)
      }
  }

  private def findSubtrees(i: Int): List[Int] = {
    def _withFollowingSubtrees(subtrees: List[Int]): List[Int] = {
      if (commands(subtrees.head).scope == commands(i).scope) subtrees
      else _withFollowingSubtrees(commands(subtrees.head).scope :: subtrees)
    }

    if (commands(i).scope == i + 1) List.empty
    else _withFollowingSubtrees(List(i + 1)).reverse
  }

  override def crossover(that: Individual, generator: Random): AntProgram = that match {
    case other: AntProgram =>
      val thisFrom = generator.nextInt(commands.length)
      val thisUntil = commands(thisFrom).scope
      val thatFrom = generator.nextInt(other.commands.length)
      val thatUntil = other.commands(thatFrom).scope
      val baseDiff = thisFrom - thatFrom
      val lengthDiff = (thatUntil - thatFrom) - (thisUntil - thisFrom)
      val prefix = (0 until thisFrom).map(i =>
        if (commands(i).scope <= thisFrom) commands(i)
        else commands(i).copy(scope = commands(i).scope + lengthDiff)
      ).toVector
      val replacement = (thatFrom until thatUntil).map(i =>
        other.commands(i).copy(scope = other.commands(i).scope + baseDiff)
      ).toVector
      val suffix = (thisUntil until commands.length).map(i =>
        commands(i).copy(scope = commands(i).scope + lengthDiff)
      ).toVector
      AntProgram(prefix ++ replacement ++ suffix)
    case _ => throw new Error
  }

  override def mutation(generator: Random): AntProgram = {
    val i = generator.nextInt(commands.length)
    val mutatedCommands = commands.updated(i, commands(i).mutation(generator))
    AntProgram(mutatedCommands)
  }

  override def toString: String = {
    def _toString(i: Int): String =
      commands(i).toString + findSubtrees(i).map("(" + _toString(_) + ")").mkString

    f"${_toString(0)}-> $fitness%.0f"
  }
}

object AntProgram {
  var world: AntWorld = _

  def random(n: Int, generator: Random): AntProgram = {
    val root = AntCommand.random(n, n, generator)
    val nodes = (1 until n).foldLeft((Vector(root), List(n))) { (state, i) =>
      val command = AntCommand.random(i + 1, state._2.head, generator)
      val scopes =
        if (state._2.head == i + 1) state._2.tail
        else if (command.scope == i + 1 || command.scope == state._2.head) state._2
        else command.scope :: state._2
      (state._1 :+ command, scopes)
    }
    AntProgram(nodes._1)
  }
}
