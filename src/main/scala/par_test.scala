import scala.collection._

object Main extends App {
    // for (i <- (1 to 20).par) println(Thread.currentThread)

    // (1 to 10).par.foreach(x => println(Thread.currentThread))

    def divisor(k : BigInt, n : BigInt) : BigInt = {
     if (0 == n % k) k
     else if (n.compare(k*k) < 0) n
     else divisor((k+1), n)
     }

 val (bigOne, bigTwo) = (BigInt(1), BigInt(2))

 def factors(n : BigInt) : List[BigInt] = n match {
     case `bigOne` => Nil;
     case _ => {
       val p = divisor(bigTwo, n)
       p :: factors(n / p)
   }
}

val r = new util.Random()
val range = 2
// val bigints = (for(i <- 1 to range) yield i) toArray
val bigints = (for(i <- 1 to range) yield BigInt(64, r)) toArray

def ser_factors = for (i <- 0 to range-1) yield factors(bigints(i))
def par_factors = for (i <- (0 to range-1).par) yield {
    // println(i)
    factors(bigints(i))
}

def time[A](f: => A) = {
  val s = System.nanoTime
  val ret = f
  println("time: "+(System.nanoTime-s)/1e6+"ms")
  // ret
}

println(time(par_factors))
println(time(ser_factors))

}
