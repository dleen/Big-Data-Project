package sgd

import (
    "bufio"
    "os"
    "testing"
    "fmt"
    "runtime"
)

func BenchmarkSGD(b *testing.B) {
    // b.N = 4
    b.StopTimer()

    file, _ := os.Open("../resources/train.txt")
    r := bufio.NewReader(file)
    line := ""

    dl := make([]DataLine, 2335859)
    w := make(map[int]float64)

    w[0] = 0.0
    w[1] = 0.0
    w[2] = 0.0
    w[3] = 0.0
    w[4] = 0.0

    for i := 0; i < 2335859; i++ {
        line, _ = r.ReadString('\n')
        parseLine(line, dl, i)
        initialize(&dl[i], w)
        i++
    }
    fmt.Println("Done loading and parsing.")

    runtime.GOMAXPROCS(NCPU)
    b.StartTimer()
    for i := 0; i < b.N; i++ {
        DoAllSGD(dl, w, NCPU)
    }

    file.Close()
}
