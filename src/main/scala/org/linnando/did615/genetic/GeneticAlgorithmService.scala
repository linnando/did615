package org.linnando.did615.genetic

import angulate2.std._
import rxjs.Subject

import scala.util.Random

@Injectable
class GeneticAlgorithmService {
  val init = new Subject[Generation]()
  val step = new Subject[GenerationLog]()

  def run(n: Int, a: Double, populationSize: Int, generationNumber: Int, seed: Option[Int]): Unit = {
    val generator = seed match {
      case Some(x) => new Random(x)
      case None => new Random()
    }
    val initialGeneration = new Generation(
      Vector.tabulate(populationSize)(_ => RastriginIndividual.random(n, a, generator))
    )
    init.next(initialGeneration)
    (0 until generationNumber).foldLeft(initialGeneration)((g, _) => {
      val log = g.next(generator)
      step.next(log)
      log.nextGeneration
    })
  }
}
