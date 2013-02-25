package main.scala.Hogwild


import scala.math.{exp, sqrt, pow, abs}

import java.util.concurrent.ConcurrentHashMap
import scala.collection.JavaConverters._

import scala.util.Random

// import scala.collection.mutable.Map


object BigDataProcessor extends App {
    val training = DataSet("training")

    println(training.allData(0))
    println(training.allData(100000))
    // val test = DataSet("test")
    // println("Done loading data.")

    // // init(training)
    // var a = 0.0
    // var aOld = 1.0

    for (i <- 0 until 5) {
        fit(training)
    //     // println(SGDfunctions.l2norm(SGDfunctions.w))

    //     // a = predict(test)
    //     // println(a)
    }

    // println(SGDfunctions.w.size)
    // println(SGDfunctions.l2norm(SGDfunctions.w))

    // println(b)


    def fit(dataSet: DataSet): Unit = {
        val eta = 0.05
        var n = 0

        // val ind = collection.parallel.ParSeq.fill(dataSet.numOfLines){
        //     Random.nextInt(dataSet.numOfLines)
        // }
        // val ind = collection.parallel.mutable.ParArray.fill(dataSet.numOfLines){
        //     Random.nextInt(dataSet.numOfLines)
        // }
        // val ind = Array.fill(dataSet.numOfLines){
        //     Random.nextInt(dataSet.numOfLines)
        // }
        println("Starting SGD now")
        // ind.foreach(gradient)

        for (line <- dataSet.allData) {
        // for (i <- (0 until dataSet.numOfLines).par) {
        // def gradient(i: Int) {
            // val l = SGDfunctions.parseLine(line, dataSet.datatype)
            // val r = Random.nextInt(dataSet.numOfLines)
            // val l = SGDfunctions.parseLine(dataSet.allData(i), dataSet.datatype)
            // val l = dataSet.allData(i)
            val l = line
            val x = l.tokens

            // The actual label, clicked or not clicked
            // 0 or 1 variable
            val y: Int = l.clicked

            // // The predicted label P(Y=1|X)
            // // this is a double, not binary
            val yHat: Double = SGDfunctions.predictLabel(x)

            // // Calculate the new weights using SGD
            SGDfunctions.updateWeights(y, yHat, x, eta)
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
            // val l = SGDfunctions.parseLine(line, dataSet.datatype)
            val l = line
            val x = l.tokens

            val yHat: Double = SGDfunctions.predictLabel(x)

            rmse += pow(label - yHat, 2)
            rmseBaseLine += pow(label - baseLine, 2)
        }
        // (sqrt(rmse / n), sqrt(rmseBaseLine / n))
        sqrt(rmse / n)
    }

}

object SGDfunctions {
    // var w = collection.mutable.Map[Int, Double]()
    println("Should only see this once.")
    var w = new ConcurrentHashMap[Int, Double]().asScala

    // Make a prediction for the label
    def predictLabel(x: Array[Int]): Double = {
        var dot = 0.0
        for (i <- x) {
            w putIfAbsent(i, 0.0)
            dot += w(i)
        }
        exp(dot) / (1 + exp(dot)) // logistic regression function
    }

    // Update the weight vector
    def updateWeights(y: Double, yHat: Double,
            x: Array[Int],
            eta: Double): Unit = {
        val grad: Double = (y - yHat) * eta // gradient
        for (i <- x) {
            w putIfAbsent(i, 0.0)
            w replace(i, w(i) + grad) // add to the weights
        }
    }

    def l2norm(x: collection.concurrent.Map[Int, Double]): Double = {
        var norm = 0.0
        for (v <- x.values) {
            norm += v * v
        }
        sqrt(norm)
    }

    // def parseLine(line: String, datatype: String): DataLine = {
    //     def parseTokens(tokenString: String): Set[Int] = {
    //         val splitOnComma = tokenString.split(',').map(_.toInt)
    //         splitOnComma.toSet
    //     }
    //     val splitOnPipe = line.split('|')
    //     // The first n - 1 elements are not tokens
    //     val nonToken = splitOnPipe.init.map(_.toInt)
    //     // The last element is a token
    //     // We parse the token into a set of ints
    //     val tokens = parseTokens(splitOnPipe.last)

    //     datatype match {
    //         case "training" => DataLine(nonToken(0),
    //             nonToken(1), nonToken(2), nonToken(3),
    //             nonToken(4), nonToken(5), tokens)
    //         case "test" => DataLine(-1,
    //             nonToken(0), nonToken(1), nonToken(2),
    //             nonToken(3), nonToken(4), tokens)
    //     }
    // }
    def parseLine(line: String, datatype: String): DataLine = {
        def parseTokens(tokenString: String): Array[Int] = {
            val splitOnComma = tokenString.split(',').map(_.toInt)
            splitOnComma.toArray
        }
        val splitOnPipe = line.split('|')
        // The first n - 1 elements are not tokens
        // val nonToken = splitOnPipe.init.map(_.toInt)
        // The last element is a token
        // We parse the token into a set of ints
        val tokens = parseTokens(splitOnPipe.last)

        datatype match {
            case "training" => DataLine(splitOnPipe(0).toInt, tokens)
            case "test" => DataLine(-1, tokens)
        }
    }
}
