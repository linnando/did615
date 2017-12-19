package org.linnando.did615.genprogramming

class AntWorld(val size: (Int, Int), feed: Seq[(Int, Int, Byte)], val allowedSteps: Int) {
  private val feedMap = feed.foldLeft(Map.empty[(Int, Int), Byte].withDefaultValue(0.toByte)) { (m, f) =>
    val position = (f._1, f._2)
    m.updated(position, (m(position) + f._3).toByte)
  }

  def apply(position: (Int, Int)): Byte = feedMap(position)

  def neighbour(from: (Int, Int), by: (Int, Int)): Option[(Int, Int)] = {
    val nextX = from._1 + by._1
    val nextY = from._2 + by._2
    if (nextX < 0 || nextX >= size._1 || nextY < 0 || nextY >= size._2) None
    else Some((nextX, nextY))
  }

  def turnLeft(orientation: (Int, Int)): (Int, Int) = (-orientation._2, orientation._1)

  def turnRight(orientation: (Int, Int)): (Int, Int) = (orientation._2, -orientation._1)
}
