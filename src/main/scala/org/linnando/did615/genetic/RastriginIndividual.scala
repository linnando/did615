package org.linnando.did615.genetic

import scala.util.Random

class RastriginIndividual(private val bytes: Seq[Byte], private val a: Double) extends Individual {
  override def fitness: Double = {
    if (bytes.exists(b => b < -100 || b > 100)) 0.0
    else bytes.map(b => {
      val x = b.toDouble / 10.0
      x * x + a * (1.0 - math.cos(2.0 * math.Pi * x))
    }).sum
  }

  override def crossover(that: Individual, generator: Random): RastriginIndividual = that match {
    case other: RastriginIndividual =>
      val mask = new Array[Byte](bytes.length)
      generator.nextBytes(mask)
      val crossed = bytes.indices.foldLeft(new Array[Byte](bytes.length)) { (r, i) =>
        r.updated(i, (bytes(i) & mask(i) | other.bytes(i) & ~mask(i)).toByte)
      }
      new RastriginIndividual(crossed, a)
    case _ =>
      throw new Error
  }

  override def mutation(generator: Random): RastriginIndividual = {
    val position = generator.nextInt(8 * bytes.length)
    val mutated = bytes.indices.foldLeft(new Array[Byte](bytes.length)) { (r, i) =>
      val byte =
        if (i == position / 8) (bytes(i) ^ (1 << (position % 8))).toByte
        else bytes(i)
      r.updated(i, byte)
    }
    new RastriginIndividual(mutated, a)
  }

  override def toString: String = {
    val arguments = bytes.map(b => f"${b.toDouble / 10.0}%.1f").mkString(", ")
    f"($arguments) -> $fitness%.3f"
  }
}

object RastriginIndividual {
  def random(n: Int, a: Double, generator: Random): RastriginIndividual = {
    val bytes = new Array[Byte](n)
    generator.nextBytes(bytes)
    new RastriginIndividual(bytes, a)
  }
}
