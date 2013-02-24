package main.scala.LR_SGD

import java.io.RandomAccessFile
import scala.io.Source

object BigDataProcessor extends App {
    val filename = "train.txt"
    val url = getClass.getResource("/" + filename)
    val f = Source.fromURL(url).getLines

    f.toList


}
