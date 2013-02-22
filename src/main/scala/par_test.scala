import scala.collection._

object Main extends App {

  for (i <- (1 to 10).par) println(Thread.currentThread)
  println()
  // (1 to 10).par.foreach(x => println(Thread.currentThread))

  var parMap = mutable.Map[Int, Double]().withDefaultValue(0D)
  for (i <- (0 to 9).par) parMap(i) += 0
  for (i <- (1 to 20).par) {
    parMap(i % 10) += 1.0
  }

  println("Printing Map")
  parMap.map(println)
  println()

}

