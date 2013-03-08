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

    cd.LoadData("../resources/train.txt", "train")
    cd.InitWeights()
    fmt.Println("Done loading, parsing, and initializing.")

    runtime.GOMAXPROCS(NCPU) // number of processors to use

    for i := 0; i < 6; i++ {
        cd.Fit(ETA, NCPU)
    }
    sgd.PrintResults(cd.W)

    td := new(sgd.ClickData)
    td.LoadData("../resources/test.txt", "test")

    rmse := td.Predict(cd.W)
    fmt.Println("RMSE: ", rmse)

}
