package org.linnando.did615.backprop

import angulate2.std._

@Component(
  selector = "back-propagation",
  templateUrl = "src/main/resources/back-propagation.component.html",
  styleUrls = @@@("src/main/resources/back-propagation.component.css")
)
class BackPropagationComponent(backPropagationService: BackPropagationService) extends OnInit {
  var learningRate = 0.5
  var allowedErrorShare = 0.2
  var hiddenLayers = "5,3"
  var maxIterations = 10
  var seed = ""
  var result = ""

  override def ngOnInit(): Unit = {
    backPropagationService.result.subscribe(res => {
      val testingLines = "Test set:" +: res.testSet.zipWithIndex.map(example => {
        val isCorrect = if (example._1._1.outputs == example._1._2) "CORRECT" else "WRONG"
        s"${example._2}. Expected ${example._1._1.outputs}, actual ${example._1._2}. $isCorrect"
      })
      val trainingCorrect = res.trainingSet.count(example => example._1.outputs == example._2)
      val testCorrect = res.testSet.count(example => example._1.outputs == example._2)
      val stats = Seq(
        f"Training examples classified correctly: ${100.0 * trainingCorrect / res.trainingSet.length}%.1f%%",
        f"Test examples classified correctly: ${100.0 * testCorrect / res.testSet.length}%.1f%%"
      )
      result = ((res.network.toString +: testingLines) ++ stats).mkString("\n")
    })
  }

  def run(): Unit = {
    val hiddenLayerNeurons = hiddenLayers.split(",").map(_.toInt).toSeq
    val intSeed = seed match {
      case "" => None
      case s => Some(s.toInt)
    }
    backPropagationService.run(learningRate, allowedErrorShare, hiddenLayerNeurons, maxIterations, intSeed)
  }
}
