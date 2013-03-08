package main

import(
    "fmt"
    "runtime"
    "testing"
    "./sgd"
)


func BenchmarkSGD(b *testing.B) {
    b.StopTimer()
    cd := new(sgd.ClickData)

    cd.LoadData("../resources/train.txt")
    cd.InitWeights()

    NCPU := runtime.GOMAXPROCS(-1)
    fmt.Println("Using :", NCPU)

    b.StartTimer()
    cd.Fit(ETA, NCPU)
}
