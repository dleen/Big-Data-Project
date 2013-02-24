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
    val allData = dataIterator.toArray

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
}

case object TestLabels {
    val url = getClass.getResource("/" + "test_label.txt")
    val dataFile = Source.fromURL(url)
    val label = dataFile.getLines.map(_.toDouble).toArray

    def closeData = {
        dataFile.close
    }
}
