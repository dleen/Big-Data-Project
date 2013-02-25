package main.scala.Hogwild

import scala.collection.parallel.ForkJoinTaskSupport
import scala.util.Random
import scala.math.{exp, sqrt, pow, abs}


object BigDataProcessor extends App {
// object BigDataProcessor extends testing.Benchmark {

	// val par = 2
	val par = sys.props("par").toInt
		// // init(training)
		var a = 0.0
		var aOld = 1.0
	// val training = DataSet("training")
	// val test = DataSet("test")

	// def run = {
		// for (i <- 0 until 5) fit(SGDdata.training)
	// }
	val tr = DataSet("training")
	val te = DataSet("test")
	init(tr)
	// init(te)

		val now = System.nanoTime

	// for (i <- 0 until 20) {
		while (abs(a - aOld) > 0.0001) {
		aOld = a
		fit(tr)
		a = predict(te)
	  println(a)
		println(SGDfunc.l2norm(SGDfunc.w))
	}

	val micros = (System.nanoTime - now) / 1000000000
     println("%d seconds".format(micros))

		//     // println(SGDfunc.l2norm(SGDfunc.w))

		    // a = predict(test)
		//     // println(a)
		// }

		def init(dataSet: DataSet): Unit = {
			for (l <- dataSet.allData) SGDfunc.initWeights(l.tokens)
		}

		def fit(dataSet: DataSet): Unit = {
				val eta = 0.05
				// var n = 0

				// val ind = collection.parallel.ParSeq.fill(dataSet.numOfLines){
				//     Random.nextInt(dataSet.numOfLines)
				// }
				val ind = collection.parallel.mutable.ParArray.fill(dataSet.numOfLines){
				    Random.nextInt(dataSet.numOfLines)
				}
				ind.tasksupport = new ForkJoinTaskSupport(
					new scala.concurrent.forkjoin.ForkJoinPool(par))
				// dataSet.allData.tasksupport = new ForkJoinTaskSupport(
				// 	new scala.concurrent.forkjoin.ForkJoinPool(par))
				// val ind = Array.fill(dataSet.numOfLines){
				//     Random.nextInt(dataSet.numOfLines)
				// }
				println("Starting SGD now")
				ind.foreach(gradient)

				// for (line <- dataSet.allData) {
				// for (i <- (0 until dataSet.numOfLines).par) {
				def gradient(i: Int) {
						// val l = SGDfunc.parseLine(line, dataSet.datatype)
						// val r = Random.nextInt(dataSet.numOfLines)
						// val l = SGDfunc.parseLine(dataSet.allData(i), dataSet.datatype)
						val l = dataSet.allData(i)
						// val l = line
						val x = l.tokens

						// The actual label, clicked or not clicked
						// 0 or 1 variable
						val y: Int = l.clicked

						// // The predicted label P(Y=1|X)
						// // this is a double, not binary
						val yHat: Double = SGDfunc.predictLabel(x)

						// // Calculate the new weights using SGD
						SGDfunc.updateWeights(y, yHat, x, eta)
						// println(n)
						// n = n + 1
				}
		}

		// def predict(dataSet: DataSet): (Double, Double) = {
		def predict(dataSet: DataSet): Double = {
				val baseLine = 0.03365528
				val labels = TestLabels.label
				val n = labels.length

				var rmse = 0.0
				var rmseBaseLine = 0.0

				for ((line, label) <- dataSet.allData zip labels) {
						// val l = SGDfunc.parseLine(line, dataSet.datatype)
						val l = line
						val x = l.tokens

						val yHat: Double = SGDfunc.predictLabel(x)

						rmse += pow(label - yHat, 2)
						rmseBaseLine += pow(label - baseLine, 2)
				}
				// (sqrt(rmse / n), sqrt(rmseBaseLine / n))
				sqrt(rmse / n)
		}

}

object SGDfunc {
	// Use java's concurrent hash map to make use of the
	// atomic operations replace and putIfAbsent.
	// Automatically convent it to use with scala notation.
	// val w = new ConcurrentHashMap[Int, Double]().asScala
	val w = collection.mutable.Map[Int, Double]().withDefaultValue(0.0)

	def initWeights(x: Array[Int]): Unit = {
		for (i <- x) w.put(i, 0.0)
	}

	// Make a prediction for the label based on tokens.
	def predictLabel(x: Array[Int]): Double = {
		var dot = 0.0
		for (i <- x) {
			// w putIfAbsent(i, 0.0) // If key does not exist use 0.0
			dot += w(i) // Each token key 'x' has value 1.0
		}
		exp(dot) / (1 + exp(dot)) // logistic regression probablity
	}

	// Update the weight vector
	def updateWeights(
		y: Double, // actual label
		yHat: Double, // predicted label
		x: Array[Int], // token keys
		eta: Double): Unit = {
			val grad: Double = (y - yHat) * eta // gradient
			for (i <- x) {
				// w putIfAbsent(i, 0.0) // If key does not exist use 0.0
				// w replace(i, w(i) + grad) // add to the weights (token value = 1.0)
				// w.put(i, w(i) + grad)
				// w(i) += grad
				w(i) += grad
			}
	}

	def l2norm(x: collection.mutable.Map[Int, Double]): Double = {
		var norm = 0.0
		for (v <- x.values) {
			norm += v * v
		}
		sqrt(norm)
	}
}

// object SGDdata {
// 	val training = DataSet("training")
// 	val test = DataSet("test")
// 	println("Done loading data.")
// }
