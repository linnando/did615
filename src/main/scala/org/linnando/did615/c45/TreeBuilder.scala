package org.linnando.did615.c45

case class TreeBuilder[T](examples: Vector[T], classify: T => Int, classes: Map[Int, String]) {
  def build(subset: Vector[Double],
            discreteAttributes: Map[String, (T => Option[Int], Map[Int, String])],
            continuousAttributes: Map[String, T => Option[Double]]): TreeNode[T] = {
    val classWeights = examples.map(classify).zip(subset).groupBy(_._1).mapValues(cls => cls.map(_._2).sum).filter(_._2 > 0.0)
    // If subset is empty, build should not have been called
    if (classWeights.isEmpty) throw new Error
    val defaultClass = classWeights.maxBy(_._2)._1
    if (classWeights.size == 1 || discreteAttributes.isEmpty && continuousAttributes.isEmpty) {
      // Only one class or no attributes left, can return a leaf
      TreeNode(_ => None, Map.empty, (classes(defaultClass), defaultClass))
    } else {
      val entropy = TreeBuilder.entropy(classWeights.values)
      bestAttribute(subset, discreteAttributes, continuousAttributes, entropy) match {
        case None =>
          // All attributes give only trivial splits
          TreeNode(_ => None, Map.empty, (classes(defaultClass), defaultClass))
        case Some((attribute, test, descriptions)) =>
          val children = splitSubset(subset, attribute, test, descriptions) mapValues {
            case (description, childWeight, subSubset) =>
              val childNode = build(subSubset, discreteAttributes - attribute, continuousAttributes - attribute)
              (description, childWeight, childNode)
          }
          TreeNode(test, children, (classes(defaultClass), defaultClass))
      }
    }
  }

  private def bestAttribute(subset: Vector[Double],
                            discreteAttributes: Map[String, (T => Option[Int], Map[Int, String])],
                            continuousAttributes: Map[String, T => Option[Double]],
                            entropy: Double): Option[(String, T => Option[Int], Map[Int, String])] = {
    val attributeEvaluation =
      discreteAttributes.mapValues(attribute => {
        evaluateDiscrete(subset, attribute._1, entropy).map(evaluation =>
          (evaluation._1, evaluation._2, attribute._2.mapValues(s => s"= $s"))
        )
      }) ++ continuousAttributes.mapValues(attribute => evaluateContinuous(subset, attribute, entropy))
    val workingAttributes = attributeEvaluation.filter(_._2.isDefined).mapValues(_.get)
    if (workingAttributes.isEmpty) None
    else {
      val bestAttribute = workingAttributes.maxBy(attribute => attribute._2._2)
      Some((bestAttribute._1, bestAttribute._2._1, bestAttribute._2._3))
    }
  }

  private def evaluateDiscrete(subset: Vector[Double], attribute: T => Option[Int], entropy: Double): Option[(T => Option[Int], Double)] = {
    val n = subset.sum
    val stats = examples.zip(subset).filter(_._2 > 0.0)
      .groupBy(example => attribute(example._1))
      .mapValues(_.groupBy(example => classify(example._1)).mapValues(_.map(_._2).sum))
    val outcomeStats = stats.mapValues(s => (s.values.sum, TreeBuilder.entropy(s.values)))
    val splitEntropy = TreeBuilder.entropy(outcomeStats.values.map(_._1))
    if (splitEntropy == 0.0) None
    else {
      val totalDefinedWeight = outcomeStats.filterKeys(_.isDefined).values.map(p => p._1).sum
      val residualEntropy = outcomeStats.filterKeys(_.isDefined).values.map(p => p._1 * p._2).sum / n
      Some((attribute, (entropy * totalDefinedWeight / n - residualEntropy) / splitEntropy))
    }
  }

  private def evaluateContinuous(subset: Vector[Double], attribute: T => Option[Double], entropy: Double): Option[(T => Option[Int], Double, Map[Int, String])] = {
    val n = subset.sum
    val stats = examples.zip(subset).filter(_._2 > 0.0)
      .groupBy(example => attribute(example._1))
      .mapValues(_.groupBy(example => classify(example._1)).mapValues(_.map(_._2).sum))
    val sortedStats = stats.filterKeys(_.isDefined)
      .map(value => (value._1.get, value._2))
      .toVector.sortBy(_._1)
    if (sortedStats.length < 2) None
    else {
      val thresholdEvaluation = evaluateThresholds(n, sortedStats)
      val i = (0 until sortedStats.length - 1).minBy(thresholdEvaluation(_)._1)
      val threshold = sortedStats(i)._1
      val (residualEntropy, weights0, weights1) = thresholdEvaluation(i)
      val splitEntropy = TreeBuilder.entropy(List(n - weights0 - weights1, weights0, weights1))
      if (splitEntropy == 0.0) None
      else {
        val gain = entropy * (weights0 + weights1) / n - residualEntropy + math.log(sortedStats.length - 1) / math.log(2) / n
        Some((item => attribute(item).map(value => if (value <= threshold) 0 else 1),
          gain / splitEntropy,
          Map(0 -> s"<= $threshold", 1 -> s"> $threshold")))
      }
    }
  }

  private def evaluateThresholds(n: Double, sortedStats: Vector[(Double, Map[Int, Double])]): Vector[(Double, Double, Double)] = {
    val emptyMap = Map.empty[Int, Double].withDefaultValue(0.0)
    val classStats = sortedStats.map(_._2).foldLeft(emptyMap) { (map, valueClassStats) =>
      valueClassStats.foldLeft(map) { (innerMap, cls) =>
        innerMap.updated(cls._1, innerMap(cls._1) + cls._2)
      }
    }
    val initialState = (List.empty[(Double, Double, Double)], Map(0 -> emptyMap, 1 -> classStats))
    val thresholdVariants = (0 until sortedStats.length - 1).foldLeft(initialState) { (state, i) =>
      val outcome0 = sortedStats(i)._2.foldLeft(state._2(0)) { (map, value) =>
        map.updated(value._1, map(value._1) + value._2)
      }
      val outcome1 = sortedStats(i)._2.foldLeft(state._2(1)) { (map, value) =>
        map.updated(value._1, map(value._1) - value._2)
      }
      val weights0 = outcome0.values.sum
      val weights1 = outcome1.values.sum
      val residualEntropy =
        (weights0 * TreeBuilder.entropy(outcome0.values) +
          weights1 * TreeBuilder.entropy(outcome1.values)) / n
      ((residualEntropy, weights0, weights1) :: state._1, Map(0 -> outcome0, 1 -> outcome1))
    }
    thresholdVariants._1.reverse.toVector
  }

  private def splitSubset(subset: Vector[Double], name: String, test: T => Option[Int], descriptions: Map[Int, String]): Map[Int, (String, Double, Vector[Double])] = {
    val outcomesWithWeights = examples.map(test).zip(subset)
    val outcomeWeights = outcomesWithWeights
      .filter(example => example._1.isDefined && example._2 > 0.0)
      .groupBy(_._1.get)
      .mapValues(_.map(_._2).sum)
    val totalOutcomeWeight = outcomeWeights.values.sum
    val initialMap = outcomeWeights.mapValues(_ => Vector.fill(examples.length)(0.0))
    val weightsMap = examples.indices.filter(i => outcomesWithWeights(i)._2 > 0.0).foldLeft(initialMap) { (map, i) =>
      outcomesWithWeights(i) match {
        case (Some(outcome), weight) =>
          val weights = map(outcome)
          val updatedWeight = weights(i) + weight
          map.updated(outcome, weights.updated(i, updatedWeight))
        case (None, weight) =>
          outcomeWeights.keys.foldLeft(map) { (state, outcome) =>
            val weights = state(outcome)
            val updatedWeight = weights(i) + weight * outcomeWeights(outcome) / totalOutcomeWeight
            state.updated(outcome, weights.updated(i, updatedWeight))
          }
      }
    }
    weightsMap.map(p => {
      val outcome = p._1
      val description = descriptions(outcome)
      val childWeight = outcomeWeights(outcome) / totalOutcomeWeight
      (outcome, (s"$name $description", childWeight, p._2))
    })
  }
}

object TreeBuilder {
  def entropy(weights: Iterable[Double]): Double = {
    val sum = weights.sum
    -weights.filter(weight => weight > 0.0)
      .map(weight => {
        val p = weight / sum
        p * math.log(p)
      }).sum / math.log(2)
  }
}
