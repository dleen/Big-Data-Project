package sgd

import(
    "math"
    "fmt"
)

// calculate the l2norm of the weight vector
func l2norm(w map[int]float64) float64 {
    sum := float64(0)
    for _, v := range w {
        sum += v * v
    }
    return math.Sqrt(sum)
}


func PrintResults(w map[int]float64) {
    fmt.Println("Intercept", w[0])
    fmt.Println("Depth", w[1])
    fmt.Println("Position", w[2])
    fmt.Println("Gender", w[3])
    fmt.Println("Age", w[4])

    fmt.Println("l2 norm", l2norm(w))
}
