package org.linnando.did615.backprop

import utest._

object NeuralNetworkTests extends TestSuite {
  val tests = Tests {
    "single layer network" - {
      "evaluate logical or" - {
        val network = NeuralNetwork(Seq(
          Vector(
            Vector(0.5, 1.0, 1.0)
          )
        ))
        assert(network.evaluate(Vector(0.0, 0.0)).length == 1)
        assert(network.evaluate(Vector(0.0, 0.0))(0) < 0.5)
        assert(network.evaluate(Vector(0.0, 1.0))(0) > 0.5)
        assert(network.evaluate(Vector(1.0, 0.0))(0) > 0.5)
        assert(network.evaluate(Vector(1.0, 1.0))(0) > 0.5)
      }

      "evaluate logical and" - {
        val network = NeuralNetwork(Seq(
          Vector(
            Vector(1.5, 1.0, 1.0)
          )
        ))
        assert(network.evaluate(Vector(0.0, 0.0)).length == 1)
        assert(network.evaluate(Vector(0.0, 0.0))(0) < 0.5)
        assert(network.evaluate(Vector(0.0, 1.0))(0) < 0.5)
        assert(network.evaluate(Vector(1.0, 0.0))(0) < 0.5)
        assert(network.evaluate(Vector(1.0, 1.0))(0) > 0.5)
      }

      "adjust weights on counter-examples" - {
        val network = NeuralNetwork(Seq(
          Vector(
            Vector(1.5, 1.0, 1.0)
          )
        ))
        val example = Vector(0.0, 1.0)
        val updatedNetwork = network.backPropagation(example, Vector(1.0), 0.1)
        assert(updatedNetwork.evaluate(example)(0) > network.evaluate(example)(0))
      }

      "train on counter-examples" - {
        val network = NeuralNetwork(Seq(
          Vector(
            Vector(1.5, 1.0, 1.0)
          )
        ))
        val updatedNetwork = network.train(
          Seq(Vector(0.0, 0.0), Vector(0.0, 1.0), Vector(1.0, 0.0), Vector(1.0, 1.0)),
          Seq(Vector(0.0), Vector(1.0), Vector(1.0), Vector(1.0)),
          0.1, 0.0, 100
        )
        assert(updatedNetwork.evaluate(Vector(0.0, 0.0))(0) < 0.5)
        assert(updatedNetwork.evaluate(Vector(0.0, 1.0))(0) > 0.5)
        assert(updatedNetwork.evaluate(Vector(1.0, 0.0))(0) > 0.5)
        assert(updatedNetwork.evaluate(Vector(1.0, 1.0))(0) > 0.5)
      }
    }

    "double layer network" - {
      "evaluate logical xor" - {
        val network = NeuralNetwork(Seq(
          Vector(
            Vector(0.5, 1.0, 1.0),
            Vector(1.5, 1.0, 1.0)
          ),
          Vector(
            Vector(0.2, 1.0, -1.0)
          )
        ))
        assert(network.evaluate(Vector(0.0, 0.0)).length == 1)
        assert(network.evaluate(Vector(0.0, 0.0))(0) < 0.5)
        assert(network.evaluate(Vector(0.0, 1.0))(0) > 0.5)
        assert(network.evaluate(Vector(1.0, 0.0))(0) > 0.5)
        assert(network.evaluate(Vector(1.0, 1.0))(0) < 0.5)
      }

      "adjust weights on counter-examples" - {
        val network = NeuralNetwork(Seq(
          Vector(
            Vector(0.5, 1.0, 1.0),
            Vector(1.5, 1.0, 1.0)
          ),
          Vector(
            Vector(0.5, 1.0, -1.0)
          )
        ))
        val example = Vector(0.0, 1.0)
        val updatedNetwork = network.backPropagation(example, Vector(1.0), 0.1)
        assert(updatedNetwork.evaluate(example)(0) > network.evaluate(example)(0))
      }

      "train on counter-examples" - {
        val network = NeuralNetwork(Seq(
          Vector(
            Vector(0.5, 1.0, 1.0),
            Vector(1.5, 1.0, 1.0)
          ),
          Vector(
            Vector(0.5, 1.0, -1.0)
          )
        ))
        val updatedNetwork = network.train(
          Seq(Vector(0.0, 0.0), Vector(0.0, 1.0), Vector(1.0, 0.0), Vector(1.0, 1.0)),
          Seq(Vector(0.0), Vector(1.0), Vector(1.0), Vector(0.0)),
          0.1, 0.0, 100
        )
        assert(updatedNetwork.evaluate(Vector(0.0, 0.0))(0) < 0.5)
        assert(updatedNetwork.evaluate(Vector(0.0, 1.0))(0) > 0.5)
        assert(updatedNetwork.evaluate(Vector(1.0, 0.0))(0) > 0.5)
        assert(updatedNetwork.evaluate(Vector(1.0, 1.0))(0) < 0.5)
      }
    }
  }
}
