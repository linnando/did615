package org.linnando.did615.backprop

import scala.util.Random

case class NeuralNetwork(weights: Seq[Vector[Vector[Double]]]) {
  def classify(inputs: Vector[Double]): Vector[Double] =
    evaluate(inputs).map(p => if (p < 0.5) 0.0 else 1.0)

  def evaluate(inputs: Vector[Double]): Vector[Double] = {
    val layerInputs = forwardTrace(inputs)
    layerInputs.head.map(NeuralNetwork.logistic)
  }

  private def forwardTrace(inputs: Vector[Double]): List[Vector[Double]] = {
    val trace = weights.foldLeft((inputs, List.empty[Vector[Double]])) { (state, layerWeights) =>
      val weightedInputs = layerWeights.map(neuronInputWeights =>
        neuronInputWeights.zip(-1.0 +: state._1).map(p => p._1 * p._2).sum
      )
      val layerOutputs = weightedInputs.map(NeuralNetwork.logistic)
      (layerOutputs, weightedInputs :: state._2)
    }
    trace._2
  }

  def train(inputs: Seq[Vector[Double]], observed: Seq[Vector[Double]], learningRate: Double, allowedErrorShare: Double, maxIterations: Int): NeuralNetwork = {
    val error = NeuralNetwork.error(inputs.map(classify), observed) / observed.map(_.length).sum
    if (error <= allowedErrorShare || maxIterations <= 0) this
    else {
      val updatedNetwork = inputs.zip(observed).foldLeft(this) { (n, example) =>
        n.backPropagation(example._1, example._2, learningRate)
      }
      updatedNetwork.train(inputs, observed, learningRate, allowedErrorShare, maxIterations - 1)
    }
  }

  def backPropagation(inputs: Vector[Double], observed: Vector[Double], learningRate: Double): NeuralNetwork = {
    val allLayerWeightedInputs = forwardTrace(inputs)
    val outputDeltas = observed.zip(allLayerWeightedInputs.head)
      .map(p => (p._1 - NeuralNetwork.logistic(p._2)) * NeuralNetwork.logisticDerivative(p._2))
    val allLayerInputs = allLayerWeightedInputs.tail.map(_.map(NeuralNetwork.logistic)) :+ inputs
    val updatedWeights = weights.reverse.zip(allLayerInputs).foldLeft((outputDeltas, List.empty[Vector[Vector[Double]]])) {
      (state, layer) =>
        val layerWeights = layer._1
        val layerInputs = layer._2
        val receiverDeltas = state._1
        val newWeights = layerWeights.zip(receiverDeltas).map(receiver =>
          receiver._1.zip(-1.0 +: layerInputs).map(p => p._1 + learningRate * receiver._2 * p._2))
        val errors = layerWeights(0).indices.tail.map(i =>
          layerWeights.map(_ (i)).zip(receiverDeltas).map(p => p._1 * p._2).sum).toVector
        val delta = errors.zip(layerInputs.map(NeuralNetwork.logisticDerivative)).map(p => p._1 * p._2)
        (delta, newWeights :: state._2)
    }
    NeuralNetwork(updatedWeights._2)
  }
}

object NeuralNetwork {
  def logistic(x: Double): Double = 1.0 / (1.0 + math.exp(-x))

  def logisticDerivative(x: Double): Double = logistic(x) * (1 - logistic(x))

  def error(outputs: Seq[Vector[Double]], observed: Seq[Vector[Double]]): Double = {
    outputs.zip(observed).map(p => p._1.zip(p._2).map(q => math.abs(q._1 - q._2)).sum).sum
  }

  def init(sizes: Seq[Int], generator: Random): NeuralNetwork = {
    val weights = sizes.init.zip(sizes.tail).map(layer =>
      Vector.tabulate(layer._2)(_ =>
        Vector.tabulate(1 + layer._1)(_ => generator.nextDouble() - 1.0)
      )
    )
    NeuralNetwork(weights)
  }
}
