package main.scala.Hogwild


import scala.math.{exp, sqrt, pow}

// import scala.collection.mutable.Map


object BigDataProcessor extends App {
    val training = DataSet("training")
    println("Done loading data.")

    init(training)
    fit(training)
    println(SGDfunctions.w.size)
    println(SGDfunctions.l2norm(SGDfunctions.w))

    val test = DataSet("test")
    val (a, b) = predict(test)
    println(a)
    println(b)

    def init(dataSet: DataSet): Unit = {
        for (line <- dataSet.allData) {
            val l = SGDfunctions.parseLine(line, dataSet.datatype)
            SGDfunctions.initWeights(l.tokens)
        }
    }

    def fit(dataSet: DataSet): Unit = {
        val eta = 0.05
        var n = 0

        for (line <- dataSet.allData) {
            val l = SGDfunctions.parseLine(line, dataSet.datatype)
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
            n = n + 1
        }
    }

    def predict(dataSet: DataSet): (Double, Double) = {
        val baseLine = 0.03365528
        val labels = TestLabels.label
        val n = labels.length

        var rmse = 0.0
        var rmseBaseLine = 0.0

        for ((line, label) <- dataSet.allData zip labels) {
            val l = SGDfunctions.parseLine(line, dataSet.datatype)
            val x = l.tokens

            val yHat: Double = SGDfunctions.predictLabel(x)

            rmse += pow(label - yHat, 2)
            rmseBaseLine += pow(label - baseLine, 2)
        }
        (sqrt(rmse / n), sqrt(rmseBaseLine / n))
    }

}

object SGDfunctions {
    var w = collection.mutable.Map[Int, Double]()

    def initWeights(x: Set[Int]): Unit = {
        for (i <- x) w.getOrElseUpdate(i, 0.0)
    }

    // Make a prediction for the label
    def predictLabel(x: Set[Int]): Double = {
        var dot = 0.0
        for (i <- x) {
            dot += w.getOrElseUpdate(i, 0.0)
        }
        exp(dot) / (1 + exp(dot)) // logistic regression function
    }

    // Update the weight vector
    def updateWeights(y: Double, yHat: Double,
            x: Set[Int],
            eta: Double): Unit = {
        val grad: Double = (y - yHat) * eta // gradient
        for (i <- x) {
            w(i) = w.getOrElseUpdate(i, 0.0) + grad // add to the weights
        }
    }

    def parseLine(line: String, datatype: String): DataLine = {
        def parseTokens(tokenString: String): Set[Int] = {
            val splitOnComma = tokenString.split(',').map(_.toInt)
            splitOnComma.toSet
        }
        val splitOnPipe = line.split('|')
        // The first n - 1 elements are not tokens
        val nonToken = splitOnPipe.init.map(_.toInt)
        // The last element is a token
        // We parse the token into a set of ints
        val tokens = parseTokens(splitOnPipe.last)

        datatype match {
            case "training" => DataLine(nonToken(0),
                nonToken(1), nonToken(2), nonToken(3),
                nonToken(4), nonToken(5), tokens)
            case "test" => DataLine(-1,
                nonToken(0), nonToken(1), nonToken(2),
                nonToken(3), nonToken(4), tokens)
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
