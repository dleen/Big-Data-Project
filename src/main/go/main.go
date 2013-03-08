package main

import(
    "fmt"
    "runtime"
    "./sgd"
)

var NCPU int = 4 // number of processors to use
var ETA float64 = 0.01 // step size

func main() {
    cd := new(sgd.ClickData)

    cd.LoadData("../resources/train.txt")
    cd.InitWeights()
    fmt.Println("Done loading, parsing, and initializing.")

    sgd.PrintResults(cd.W)

    runtime.GOMAXPROCS(NCPU)

    cd.Fit(ETA, NCPU)

    sgd.PrintResults(cd.W)

}
