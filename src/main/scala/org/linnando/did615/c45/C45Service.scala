package org.linnando.did615.c45

import angulate2.http.Http
import angulate2.std._
import org.linnando.did615.backprop._
import rxjs.Subject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.scalajs.js

@Injectable
class C45Service(http: Http) {
  val result = new Subject[C45Result]()

  private val trainingSet: Future[Vector[Example]] = {
    val promise = Promise[Vector[Example]]
    http.get("src/main/resources/train.csv").subscribe(response =>
      if (response.ok) {
        val lines = response.text().split("\n")
        val examples = lines.take(lines.length / 10).toVector.map(line =>
          Example(line.split(",").toVector.map(field => if (field == "") None else Some(field.toDouble)))
        )
        promise.success(examples.filter(_.income.isDefined))
      } else {
        promise.failure(new Error(response.status.toString))
      })
    promise.future
  }
  private val testSet: Future[Vector[Example]] = {
    val promise = Promise[Vector[Example]]
    http.get("src/main/resources/test.csv").subscribe(response =>
      if (response.ok) {
        val lines = response.text().split("\n")
        val examples = lines.take(lines.length / 10).toVector.map(line =>
          Example(line.split(",").toVector.map(field => if (field == "") None else Some(field.toDouble)))
        )
        promise.success(examples.filter(_.income.isDefined))
      } else {
        promise.failure(new Error(response.status.toString))
      })
    promise.future
  }

  def run(): Future[Unit] = {
    println(new js.Date())
    for {
      examples <- trainingSet
      builder = TreeBuilder[Example](
        examples,
        e => e.income.get.id,
        Income.values.map(value => (value.id, value.desc)).toMap
      )
      tree = builder.build(
        examples.map(_ => 1.0),
        Map(
          ("workclass", ( { e: Example => e.workclass.map(_.id) }, Workclass.values.map(value => (value.id, value.desc)).toMap)),
          ("education", ( { e: Example => e.education.map(_.id) }, Education.values.map(value => (value.id, value.desc)).toMap)),
          ("maritalStatus", ( { e: Example => e.maritalStatus.map(_.id) }, MaritalStatus.values.map(value => (value.id, value.desc)).toMap)),
          ("occupation", ( { e: Example => e.occupation.map(_.id) }, Occupation.values.map(value => (value.id, value.desc)).toMap)),
          ("relationship", ( { e: Example => e.relationship.map(_.id) }, Relationship.values.map(value => (value.id, value.desc)).toMap)),
          ("race", ( { e: Example => e.race.map(_.id) }, Race.values.map(value => (value.id, value.desc)).toMap)),
          ("sex", ( { e: Example => e.sex.map(_.id) }, Sex.values.map(value => (value.id, value.desc)).toMap)),
          ("nativeCountry", ( { e: Example => e.nativeCountry.map(_.id) }, NativeCountry.values.map(value => (value.id, value.desc)).toMap))
        ),
        Map(
          ("age", e => e.age),
          ("fnlwgt", e => e.fnlwgt),
          ("educationNum", e => e.educationNum),
          ("capitalGain", e => e.capitalGain),
          ("capitalLoss", e => e.capitalLoss),
          ("hoursPerWeek", e => e.hoursPerWeek)
        )
      )
      testExamples <- testSet
    } yield {
      println(new js.Date().toString + tree.toString)
      result.next(C45Result(
        tree,
        testExamples.map(example => (example, Income(tree.classify(example).maxBy(_._2)._1)))
      ))
      println(new js.Date())
    }
  }
}
