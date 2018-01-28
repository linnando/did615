package org.linnando.did615.backprop

import angulate2.http.Http
import angulate2.std._
import rxjs.Subject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.Random

@Injectable
class BackPropagationService(http: Http) {
  val result = new Subject[TrainingResult]()

  private val trainingSet: Future[Vector[Example]] = {
    val promise = Promise[Vector[Example]]
    http.get("src/main/resources/train.csv").subscribe(response =>
      if (response.ok) {
        val examples = response.text().split("\n").toVector.map(line =>
          Example(line.split(",").toVector.map(field => if (field == "") None else Some(field.toDouble)))
        )
        promise.success(examples)
      } else {
        promise.failure(new Error(response.status.toString))
      })
    promise.future
  }
  private val testSet: Future[Vector[Example]] = {
    val promise = Promise[Vector[Example]]
    http.get("src/main/resources/test.csv").subscribe(response =>
      if (response.ok) {
        val examples = response.text().split("\n").toVector.map(line =>
          Example(line.split(",").toVector.map(field => if (field == "") None else Some(field.toDouble)))
        )
        promise.success(examples)
      } else {
        promise.failure(new Error(response.status.toString))
      })
    promise.future
  }

  def run(learningRate: Double, allowedErrorShare: Double, hiddenLayerNeurons: Seq[Int], maxIterations: Int, seed: Option[Int]): Future[Unit] = {
    val generator = seed match {
      case Some(x) => new Random(x)
      case None => new Random()
    }
    val initialNetwork = NeuralNetwork.init(Example.numberOfInputs +: hiddenLayerNeurons :+ Example.numberOfOutputs, generator)
    for {
      examples <- trainingSet
      network = initialNetwork.train(
        examples.map(_.inputs),
        examples.map(_.outputs),
        learningRate,
        allowedErrorShare,
        maxIterations
      )
      testExamples <- testSet
    } yield result.next(TrainingResult(
      network,
      examples.map(example => (example, network.classify(example.inputs))),
      testExamples.map(example => (example, network.classify(example.inputs)))
    ))
  }
}
