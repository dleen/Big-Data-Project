import scala.collection._

object Main extends App {

    for (i <- (1 to 20).par) println(Thread.currentThread)

    (1 to 10).par.foreach(x => println(Thread.currentThread))

}
