package org.linnando.did615.backprop

case class TrainingResult(network: NeuralNetwork,
                          trainingSet: Seq[(Example, Vector[Double])],
                          testSet: Seq[(Example, Vector[Double])])
