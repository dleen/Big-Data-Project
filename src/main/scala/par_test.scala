import scala.collection._

object Main extends App {

  for (i <- (1 to 10).par) println(Thread.currentThread)
  println()
  // (1 to 10).par.foreach(x => println(Thread.currentThread))

  var parMap = mutable.Map[Int, Double]().withDefaultValue(0D)
  for (i <- (0 to 9)) parMap(i) += 0
  for (i <- (1 to 20).par) {
    parMap(i % 10) += 1.0
  }

  println("Printing Map")
  parMap.map(println)
  println()

  val repeatedKey = Map(0 -> 2.0, 1 -> 1.0, 1 -> 1.0, 3 -> 2.0)
  var mutRepKey = mutable.Map(0 -> 2.0, 1 -> 1.0, 1 -> 1.0, 3 -> 2.0)
  mutRepKey += (1 -> 1.0)
  mutRepKey(1) += 1.0
  println("Repeated Key")
  repeatedKey.map(println)
  println()
  mutRepKey.map(println)

}

