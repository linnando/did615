package org.linnando.did615.genprogramming

import angulate2.std._
import org.linnando.did615.genetic.{Generation, GenerationLog, Individual}
import rxjs.Subject

import scala.util.Random

@Injectable
class GeneticProgrammingService {
  val init = new Subject[Generation]()
  val step = new Subject[GenerationLog]()
  val best = new Subject[Individual]()

  def run(n: Int, populationSize: Int, generationNumber: Int, seed: Option[Int]): Unit = {
    val generator = seed match {
      case Some(x) => new Random(x)
      case None => new Random()
    }
    val initialGeneration = new Generation(
      Vector.tabulate(populationSize)(_ => AntProgram.random(n, generator))
    )
    init.next(initialGeneration)
    (0 until generationNumber).foldLeft(initialGeneration)((g, _) => {
      val log = g.next(generator)
      step.next(log)
      best.next(log.nextGeneration.best)
      log.nextGeneration
    })
  }
}
