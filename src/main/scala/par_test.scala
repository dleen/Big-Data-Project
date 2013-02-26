import scala.collection._

import scala.collection.parallel.ForkJoinTaskSupport


// object Main extends App {
object Main extends testing.Benchmark {

  val par = sys.props("par").toInt
  // val par = 4

  @scala.volatile var w = collection.mutable.Map[Int, Double]()

  val mod = 100000
  val l = (1 to 200000000).par

  l.tasksupport = new ForkJoinTaskSupport(
      new scala.concurrent.forkjoin.ForkJoinPool(par))

  for (i <- (0 until mod)) w.put(i, 0.0)

  def run() = {
    for (i <- l) {
      w(i % mod) += 1.0
    }
  }

}

