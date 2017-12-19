package org.linnando.did615.genprogramming

import angulate2.std._
import org.linnando.did615.genetic.Individual

@Component(
  selector = "genetic-programming",
  templateUrl = "src/main/resources/genetic-programming.component.html",
  styleUrls = @@@("src/main/resources/genetic-programming.component.css")
)
class GeneticProgrammingComponent(geneticProgrammingService: GeneticProgrammingService) extends OnInit {
  var xSize = 10
  var ySize = 10
  var feed: Seq[(Int, Int, Byte)] = Seq.empty
  var allowedSteps = 50
  var n = 20
  var populationSize = 100
  var generationNumber = 100
  var seed = ""
  var log = ""

  override def ngOnInit(): Unit = {
    geneticProgrammingService.init.subscribe(g =>
      log = s"Generation ${g.step}\n" + formatGroup(g.population))
    geneticProgrammingService.step.subscribe(g => {
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
    AntProgram.world = new AntWorld((xSize, ySize), feed, allowedSteps)
    geneticProgrammingService.run(n, populationSize, generationNumber, intSeed)
  }

  def saveFeed(feed: Set[(Int, Int)]): Unit =
    this.feed = feed.toSeq.map(f => (f._1, f._2, 1.toByte))
}
