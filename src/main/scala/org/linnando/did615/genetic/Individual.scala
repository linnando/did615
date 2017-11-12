package org.linnando.did615.genetic

import scala.util.Random

trait Individual {
  def fitness: Double

  def crossover(that: Individual, generator: Random): Individual

  def mutation(generator: Random): Individual
}
