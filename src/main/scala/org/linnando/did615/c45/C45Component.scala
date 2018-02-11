package org.linnando.did615.c45

import angulate2.std._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

@Component(
  selector = "decision-trees",
  templateUrl = "src/main/resources/c45.component.html",
  styleUrls = @@@("src/main/resources/c45.component.css")
)
class C45Component(c45Service: C45Service) extends OnInit {
  var result = ""

  override def ngOnInit(): Unit = {
    c45Service.result.subscribe(res => {
      val testingLines = "Test set:" +: res.testSet.zipWithIndex.map(example => {
        val isCorrect = if (example._1._1.income.get == example._1._2) "CORRECT" else "WRONG"
        s"${example._2}. Expected ${example._1._1.income.get.desc}, actual ${example._1._2.desc}. $isCorrect"
      })
      val testCorrect = res.testSet.count(example => example._1.income.get == example._2)
      val stats = Seq(
        f"Test examples classified correctly: ${100.0 * testCorrect / res.testSet.length}%.1f%%"
      )
      result = ((res.tree.toString +: testingLines) ++ stats).mkString("\n")
    })
  }

  def run(): Unit = {
    c45Service.run() onComplete {
      case Success(_) => ()
      case Failure(e) => println(e)
    }
  }
}
