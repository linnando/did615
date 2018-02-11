package org.linnando.did615.c45

case class TreeNode[T](test: T => Option[Int], children: Map[Int, (String, Double, TreeNode[T])], default: (String, Int)) {
  def classify(item: T): Map[Int, Double] = test(item) match {
    case Some(x) =>
      if (children.contains(x)) children(x)._3.classify(item)
      else Map(default._2 -> 1.0) withDefaultValue 0.0
    case None =>
      val withoutDefault = children.values.foldLeft(Map.empty[Int, Double] withDefaultValue 0.0) { (map, child) =>
        child._3.classify(item).foldLeft(map) { (innerMap, pair) =>
          innerMap.updated(pair._1, innerMap(pair._1) + child._2 * pair._2)
        }
      }
      withoutDefault.updated(default._2, withoutDefault(default._2) + 1.0 - children.map(_._2._2).sum)
  }

  override def toString: String = stringify.mkString("\n")

  private def stringify: List[String] = {
    val conditions = children.values.map(child => (child._1, child._3.stringify))
    if (conditions.isEmpty) List(default._1)
    else
      List(s"if ${conditions.head._1}") ++ conditions.head._2.map(str => s"  $str") ++
        conditions.tail.flatMap(condition =>
          List(s"else if ${condition._1}") ++ condition._2.map(str => s"  $str")) ++
        List(s"else ${default._1}")
  }
}
