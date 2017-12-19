package org.linnando.did615.genprogramming

import angulate2.core.EventEmitter
import angulate2.std._

import scala.scalajs.js

@Component(
  selector = "ant-world",
  templateUrl = "src/main/resources/ant-world.component.html",
  styleUrls = @@@("src/main/resources/ant-world.component.css")
)
class AntWorldComponent(geneticProgrammingService: GeneticProgrammingService) extends OnInit {
  @Input()
  var modalId = "antWorld"
  @Input()
  var xSize: Int = _
  @Input()
  var ySize: Int = _
  @Input()
  var allowedSteps: Int = _
  @Output()
  val setFeed = new EventEmitter[Set[(Int, Int)]]()
  var feed: Set[(Int, Int)] = Set((0, 7), (1, 0), (1, 5), (2, 6), (3, 3), (5, 1), (5, 5), (6, 7), (7, 3), (9, 0))
  var bestProgram: AntProgram = _
  var executionState: Option[ExecutionState] = None

  override def ngOnInit(): Unit = {
    setFeed.emit(feed)
    geneticProgrammingService.best.subscribe {
      case program: AntProgram =>
        val world = new AntWorld((xSize, ySize), feed.toSeq.map(f => (f._1, f._2, 1.toByte)), allowedSteps)
        bestProgram = program
        executionState = Some(ExecutionState(world, Set((0, 0)), (0, 0), (0, 1), 0, allowedSteps))
      case _ => throw new Error
    }
  }

  def hasState: Boolean = executionState.isDefined

  def programText: String = bestProgram.toString

  def nextState(): Unit = executionState = executionState.map(bestProgram.nextState)

  def getMemory: String = executionState.map(_.memory.toString).getOrElse("-")

  def getSensor: String = executionState.map(_.sense match {
    case -1 => "-1 WALL"
    case 0 => "0 EMPTY"
    case x => x.toString
  }).getOrElse("-")

  def remainingSteps: String = executionState.map(_.remainingSteps.toString).getOrElse("-")

  def collectedFeed: String = executionState.map(_.visited.toSeq.map(feed).count(f => f).toString).getOrElse("-")

  def xIndices: js.Array[Int] = js.Array[Int]() ++ (0 until xSize)

  def reversedYIndices: js.Array[Int] = js.Array[Int]() ++ (ySize - 1 to 0 by -1)

  def toggleFeed(x: Int, y: Int): Unit =
    if (feed((x, y))) feed -= ((x, y))
    else feed += ((x, y))

  def feedAvailable(x: Int, y: Int): Boolean = feed((x, y)) && !executionState.exists(_.visited((x, y)))

  def hasAntDown(x: Int, y: Int): Boolean =
    executionState.exists(s => s.position == ((x, y)) && s.orientation == ((0, -1)))

  def hasAntLeft(x: Int, y: Int): Boolean =
    executionState.exists(s => s.position == ((x, y)) && s.orientation == ((-1, 0)))

  def hasAntRight(x: Int, y: Int): Boolean =
    executionState.exists(s => s.position == ((x, y)) && s.orientation == ((1, 0)))

  def hasAntUp(x: Int, y: Int): Boolean =
    executionState.exists(s => s.position == ((x, y)) && s.orientation == ((0, 1)))

  def saveFeed(): Unit = setFeed.emit(feed)
}
