package main.scala.Hogwild


import scala.math.{exp, sqrt, pow}

// import scala.collection.mutable.Map


object BigDataProcessor extends App {
    val training = DataSet("training")
    println("Done loading data.")

    val w = fit(training)
    println(w.size)
    println(SGDfunctions.l2norm(w))

    val test = DataSet("test")
    val (a, b) = predict(test, w)
    println(a)
    println(b)

    def fit(dataSet: DataSet): collection.mutable.Map[Int, Double] = {
        var w = collection.mutable.Map[Int, Double]().withDefaultValue(0.0)
        val eta = 0.05

        for (line <- dataSet.allData) {
            val l = SGDfunctions.parseLine(line, dataSet.datatype)
            val x = l.tokens

            // The actual label, clicked or not clicked
            // 0 or 1 variable
            val y: Int = l.clicked

            // // The predicted label P(Y=1|X)
            // // this is a double, not binary
            val yHat: Double = SGDfunctions.predictLabel(x, w)

            // // Calculate the new weights using SGD
            w = SGDfunctions.updateWeights(y, yHat, x, w, eta)
        }
    w
    }

    def predict(dataSet: DataSet,
        w: collection.mutable.Map[Int, Double]): (Double, Double) = {
        val baseLine = 0.03365528
        val labels = TestLabels.label
        val n = labels.length

        var rmse = 0.0
        var rmseBaseLine = 0.0

        for ((line, label) <- dataSet.allData zip labels) {
            val l = SGDfunctions.parseLine(line, dataSet.datatype)
            val x = l.tokens

            val yHat: Double = SGDfunctions.predictLabel(x, w)

            rmse += pow(label - yHat, 2)
            rmseBaseLine += pow(label - baseLine, 2)
        }
        (sqrt(rmse / n), sqrt(rmseBaseLine / n))
    }

}

object SGDfunctions {
    // Make a prediction for the label
    def predictLabel(x: Set[Int],
            w: collection.mutable.Map[Int, Double]): Double = {
        var dot = 0.0
        for (i <- x) {
            dot += w(i)
        }
        exp(dot) / (1 + exp(dot)) // logistic regression function
    }

    // Update the weight vector
    def updateWeights(y: Double, yHat: Double,
            x: Set[Int],
            w: collection.mutable.Map[Int, Double],
            eta: Double): collection.mutable.Map[Int, Double] = {
        val grad: Double = (y - yHat) * eta // gradient
        for (i <- x) {
            w(i) += grad // add to the weights
        }
        w
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
