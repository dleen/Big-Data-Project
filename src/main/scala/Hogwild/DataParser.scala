package main.scala.Hogwild

import scala.io.Source


// Object to store the results of parsing one line
case class DataLine(clicked: Int,
    depth: Int,
    position: Int,
    userid: Int,
    gender: Int,
    age: Int,
    tokens: Set[Int])


// Object representing one dataset
case class DataSet(datatype: String) {
    val filename: String = datatype match {
        case "training" => "train.txt"
        case "test" => "test.txt"
    }
    // Open resource, either training or test data
    val url = getClass.getResource("/" + filename)
    val dataIterator = Source.fromURL(url).getLines
    val allData = dataIterator.toVector

    println(allData.length)

    val tokensLength: Int = datatype match {
        case "training" => 141063
        case "test" => 109459
    }
    val maxTokenValue: Int = datatype match {
        case "training" => 1070659
        case "test" => 1070634
    }
    val numOfLines: Int = datatype match {
        case "training" => 2335860
        case "test" => 1016553
    }
    val offset: Int = 5

    def parseLine(line: String): DataLine = {
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
}

case object TestLabels {
    val url = getClass.getResource("/" + "test_label.txt")
    val dataFile = Source.fromURL(url)
    val label = dataFile.getLines.map(_.toDouble).toArray

    def closeData = {
        dataFile.close
    }
}
