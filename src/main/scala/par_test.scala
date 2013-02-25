import scala.collection._
import java.util.concurrent.ConcurrentHashMap
import scala.collection.JavaConverters._

object Main extends App {

  // for (i <- (1 to 10).par) println(Thread.currentThread)
  // println()

  // var w = new ConcurrentHashMap[Int, Double]().asScala
  // var w = collection.parallel.mutable.ParMap[Int, Double]()
  val w = collection.mutable.Map[Int, Double]()
  // val w = mutable.Map[Int, Double]().withDefaultValue(0)

  // var parMap = mutable.Map[Int, Double]().withDefaultValue(0D)
  // for (i <- (0 to 9).par) parMap(i) += 0
  // for (i <- (1 to 20).par) {
  //   parMap(i % 10) += 1.0
  // }

  val l = (1 to 20)
  // val l = (1 to 20)
  val ll = l.par

  val mod = 10

  for (i <- (0 until mod)) w.put(i, 0.0)

  for (i <- ll) {
  //   // w(i % 100000) += 1.0
    // val t = w.getOrElse(i % mod, 0.0)
    // w.put(i % mod, w(i % mod) + 1.0)
    w(i % mod) += 1.0
    // w putIfAbsent(i % mod, 0.0)
    // w replace(i % mod, w(i % mod) + 1.0)
  }

  // ll.map(i => w.put(i % mod, w.getOrElse(i % mod, 0.0) + 1.0))

  // println("Printing Map")
  // parMap.map(println)
  // println()
  w.map(println)

}

