package org.linnando.did615.c45

import utest._

object DecisionTreeTests extends TestSuite {
  val booleanDesc = Map(0 -> "false", 1 -> "true")

  def booleanToInt(b: Boolean): Int = if (b) 1 else 0

  val tests = Tests {
    "build a decision tree for logical AND" - {
      val examples = Vector(
        BooleanExample2(arg0 = false, arg1 = false, result = false),
        BooleanExample2(arg0 = false, arg1 = true, result = false),
        BooleanExample2(arg0 = true, arg1 = false, result = false),
        BooleanExample2(arg0 = true, arg1 = true, result = true)
      )
      val builder = TreeBuilder[BooleanExample2](examples, e => booleanToInt(e.result), booleanDesc)
      val tree = builder.build(
        examples.map(_ => 1.0),
        Map(
          ("arg0", ( { e: BooleanExample2 => Some(booleanToInt(e.arg0)) }, booleanDesc)),
          ("arg1", ( { e: BooleanExample2 => Some(booleanToInt(e.arg1)) }, booleanDesc))
        ),
        Map.empty
      )
      assert(examples.forall(example =>
        tree.classify(example).maxBy(_._2)._1 == booleanToInt(example.result)
      ))
    }

    "build a decision tree for logical OR" - {
      val examples = Vector(
        BooleanExample2(arg0 = false, arg1 = false, result = false),
        BooleanExample2(arg0 = false, arg1 = true, result = true),
        BooleanExample2(arg0 = true, arg1 = false, result = true),
        BooleanExample2(arg0 = true, arg1 = true, result = true)
      )
      val builder = TreeBuilder[BooleanExample2](examples, e => booleanToInt(e.result), booleanDesc)
      val tree = builder.build(
        examples.map(_ => 1.0),
        Map(
          ("arg0", ( { e: BooleanExample2 => Some(booleanToInt(e.arg0)) }, booleanDesc)),
          ("arg1", ( { e: BooleanExample2 => Some(booleanToInt(e.arg1)) }, booleanDesc))
        ),
        Map.empty
      )
      assert(examples.forall(example =>
        tree.classify(example).maxBy(_._2)._1 == booleanToInt(example.result)
      ))
    }
  }
}
