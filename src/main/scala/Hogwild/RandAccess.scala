package main.scala.Hogwild

import scala.collection.parallel.ForkJoinTaskSupport
import scala.util.Random
import scala.math.{exp, sqrt, pow, abs}


// object BigDataProcessor extends App {
object BigDataProcessor extends testing.Benchmark {

	val par = sys.props("par").toInt

	var a = 0.0
	var aOld = 1.0
	var n = 0
	val eta = 0.05

	// def run = {
	// 	for (i <- 0 until 5) fit(SGDdata.training)
	// }

	val tr = DataSet("training")
	val te = DataSet("test")
	init(tr) // initialize the weights in the training set to zero

	// timing start
	// val now = System.nanoTime

	def run() = {
		for (i <- 0 until 1) {
		// while (abs(a - aOld) > 0.0001) {
			// aOld = a // old rmse
			fit(tr) // fit weights to training data
			// a = predict(te) // make a prediction and update rmse
		  // println(a) // print rmse
			// println(SGDfunc.l2norm(SGDfunc.w)) // print weights norm
		}
	}

	// timing stop
	// val micros = (System.nanoTime - now) / 1000000000
  // println("%d seconds".format(micros))


	def init(dataSet: DataSet): Unit = {
		for (l <- dataSet.allData) SGDfunc.initWeights(l.tokens)
	}

	def fit(dataSet: DataSet): Unit = {
		val ind = collection.parallel.mutable.ParArray.fill(dataSet.numOfLines){
	    Random.nextInt(dataSet.numOfLines)
		}
		// val ind = (0 until dataSet.numOfLines).par
		ind.tasksupport = new ForkJoinTaskSupport(
			new scala.concurrent.forkjoin.ForkJoinPool(par))

		println("Starting SGD now")
		ind.foreach(i => gradient(i, dataSet))
	}

	def gradient(i: Int, dataSet: DataSet) {
		// val r = Random.nextInt(dataSet.numOfLines)
		val l = dataSet.allData(i)
		val x = l.tokens

		// The actual label, clicked or not clicked
		val y: Int = l.clicked

		// The predicted label P(Y=1|X)
		// this is a double, not binary
		val yHat: Double = SGDfunc.predictLabel(x)

		// Calculate the new weights using SGD
		SGDfunc.updateWeights(y, yHat, x, eta)

		// if (n % 100000 == 0) println(predict(te))
		// n = n + 1
	}

	// def predict(dataSet: DataSet): (Double, Double) = {
	def predict(dataSet: DataSet): Double = {
		val baseLine = 0.03365528
		val labels = TestLabels.label
		val n = labels.length

		var rmse = 0.0
		var rmseBaseLine = 0.0

		for ((line, label) <- dataSet.allData zip labels) {
			val l = line
			val x = l.tokens

			val yHat: Double = SGDfunc.predictLabel(x)

			rmse += pow(label - yHat, 2)
		}
		sqrt(rmse / n)
	}

}

object SGDfunc {
	// A static weights map.
	// Maps a token key to its weight.
	@scala.volatile
	var w = collection.mutable.Map[Int, Double]().withDefaultValue(0.0)

	def initWeights(x: Array[Int]): Unit = {
		for (i <- x) w.put(i, 0.0)
	}

	// Make a prediction for the label based on tokens.
	def predictLabel(x: Array[Int]): Double = {
		var dot = 0.0
		for (i <- x) dot += w(i) // Each token key 'x' has value 1.0
		exp(dot) / (1 + exp(dot)) // logistic regression probablity
	}

	// Update the weight vector
	def updateWeights(
		y: Double, // actual label
		yHat: Double, // predicted label
		x: Array[Int], // token keys
		eta: Double): Unit = {
			val grad: Double = (y - yHat) * eta // gradient
			for (i <- x) w(i) += grad // update weights
	}

	// The l2norm of the weights
	def l2norm(x: collection.mutable.Map[Int, Double]): Double = {
		var norm = 0.0
		for (v <- x.values) norm += v * v
		sqrt(norm)
	}
}
