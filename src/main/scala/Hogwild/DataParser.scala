package main.scala.Hogwild

import scala.io.Source


// Object to store the results of parsing one line
case class DataLine(clicked: Int, tokens: Array[Int])


// Object representing one dataset
case class DataSet(datatype: String) {
    val filename: String = datatype match {
        case "training" => "train.txt"
        case "test" => "test.txt"
    }
    // Open resource, either training or test data
    val url = getClass.getResource("/" + filename)
    val dataIterator = Source.fromURL(url).getLines.map(parseLine)

    var allData = dataIterator.toArray
    // var allData = dataIterator.toVector

    println(allData.length)

    val numOfLines: Int = datatype match {
        case "training" => 2335859
        case "test" => 1016552
    }

    def parseLine(line: String): DataLine = {
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

case object TestLabels {
    val url = getClass.getResource("/" + "test_label.txt")
    val dataFile = Source.fromURL(url)
    val label = dataFile.getLines.map(_.toDouble).toArray

    def closeData = {
        dataFile.close
    }
}
