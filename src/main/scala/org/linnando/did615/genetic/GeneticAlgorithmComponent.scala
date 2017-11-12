package org.linnando.did615.genetic

import angulate2.std._

@Component(
  selector = "genetic-algorithm",
  templateUrl = "src/main/resources/genetic-algorithm.component.html",
  styleUrls = @@@("src/main/resources/genetic-algorithm.component.css")
)
class GeneticAlgorithmComponent(geneticAlgorithmService: GeneticAlgorithmService) extends OnInit {
  var n = 2
  var a = 10.0
  var populationSize = 10
  var generationNumber = 100
  var seed = ""
  var log = ""

  override def ngOnInit(): Unit = {
    geneticAlgorithmService.init.subscribe(g =>
      log = s"Generation ${g.step}\n" + formatGroup(g.population))
    geneticAlgorithmService.step.subscribe(g => {
      log += "\nElite:\n" + formatGroup(g.elite)
      log += "\nCrossover participants:\n" + formatGroup(g.crossoverParticipants)
      log += "\nChildren:\n" + formatGroup(g.children)
      log += "\nChildren after mutation:\n" + formatGroup(g.childrenAfterMutation)
      log += s"\nSurvivors (generation ${g.nextGeneration.step}):\n" + formatGroup(g.nextGeneration.population)
    })
  }

  private def formatGroup(group: Vector[Individual]): String =
    group.map(s => s"  $s").mkString("\n")

  def run(): Unit = {
    val intSeed = seed match {
      case "" => None
      case s => Some(s.toInt)
    }
    geneticAlgorithmService.run(n, a, populationSize, generationNumber, intSeed)
  }
}
