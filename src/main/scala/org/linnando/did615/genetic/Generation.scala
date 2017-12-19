package org.linnando.did615.genetic

import scala.util.Random

class Generation(val population: Vector[Individual], val step: Int = 0) {
  def best: Individual = population.maxBy(_.fitness)

  def next(generator: Random): GenerationLog = {
    val elite = Vector(best)
    val nForCrossover = ((Generation.CROSSOVER_SHARE * population.size + 1.0) / 2.0).toInt * 2
    val crossoverParticipants = Generation.select(population, nForCrossover, generator)
    val children = (0 until nForCrossover by 2).map(i =>
      crossoverParticipants(i).crossover(crossoverParticipants(i + 1), generator))
    val nForMutation = (Generation.MUTATION_SHARE * population.size + 0.5).toInt
    val splitChildren = children.splitAt(nForMutation)
    val childrenAfterMutation = splitChildren._1.map(_.mutation(generator)) ++ splitChildren._2
    val allCandidates = population ++ childrenAfterMutation
    val survivors = elite ++ Generation.select(allCandidates, population.size - 1, generator)
    GenerationLog(
      nextGeneration = new Generation(survivors, step + 1),
      elite = elite,
      crossoverParticipants = crossoverParticipants,
      children = children.toVector,
      childrenAfterMutation = childrenAfterMutation.toVector
    )
  }
}

object Generation {
  val CROSSOVER_SHARE = 1.0
  val MUTATION_SHARE = 0.1

  private def select(population: Vector[Individual], n: Int, generator: Random): Vector[Individual] = {
    val fitness = population.map(_.fitness)
    val runningTotals = fitness.foldLeft(List(0.0))((d, f) => (d.head + f) :: d)
    val cdf =
      if (runningTotals.head > 0) runningTotals.map(_ / runningTotals.head).reverse.tail
      else Vector.tabulate(population.length)(i => 1.0 * (i + 1) / population.length)
    (0 until n).map(_ => {
      val p = generator.nextDouble()
      population(cdf.indexWhere(p < _))
    }).toVector
  }
}
