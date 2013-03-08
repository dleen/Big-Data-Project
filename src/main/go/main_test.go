package main

import(
    "fmt"
    "runtime"
    "testing"
    "./sgd"
)

/*
    Benchmarks the SGD part of the code
    Run using:
    go test -test.bench "BenchmarkSGD" -test.cpu 1,2,4
*/


func BenchmarkSGD(b *testing.B) {
    b.StopTimer()
    cd := new(sgd.ClickData)

    cd.LoadData("../resources/train.txt", "train")
    cd.InitWeights()

    NCPU := runtime.GOMAXPROCS(-1)
    fmt.Println("Using :", NCPU)

    b.N = 3 // number of iterations
    b.StartTimer()
    for i := 0; i < b.N; i++ {
        cd.Fit(ETA, NCPU)
    }
}
