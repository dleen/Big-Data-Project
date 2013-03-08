package sgd

import (
    "math"
)

/*
    Contains the functions for making predictions
*/


func (td *ClickData) Predict(w map[int]float64) float64 {
    return predictionRmse(td.data, w)
}


func predictLabel(dl *DataLine, w map[int]float64) float64 {
    sum :=  w[0] +
            w[1] * float64(dl.depth) +
            w[2] * float64(dl.position) +
            w[3] * float64(dl.gender) +
            w[4] * float64(dl.age)
    for _, v := range dl.tokens {
        sum += w[v + offset]
    }
    numer := math.Exp(sum)
    return numer / (1.0 + numer)
}


func predictionRmse(td []DataLine, w map[int]float64) float64 {
    y := 0.0; yhat := 0.0; rmse := 0.0
    tl := new(TestLabels)
    tl.LoadData("../resources/test_label.txt")

    for i := 0; i < 1016552; i++ {
        yhat = predictLabel(&td[i], w)
        y = tl.Labels[i]

        rmse += (yhat - y) * (yhat - y)
    }
    return math.Sqrt(rmse / float64(1016552))
}
