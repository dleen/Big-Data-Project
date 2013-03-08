package sgd

import (
    "math/rand" // to randomly permute data
)

/*
	Contains all the functions needed to calculate
	the stochastic gradient descent for logistic regression
*/

var offset int = 5


// logistic regression gradient
func gradient(y int, yhat float64, eta float64) float64 {
    return (float64(y) - yhat) * eta
}


func updateWeights(dl *DataLine, w map[int]float64, grad float64) {
	// Update these weights in a random order?
	w[0] += grad
	w[1] += grad * float64(dl.depth)
	w[2] += grad * float64(dl.position)
	w[3] += grad * float64(dl.gender)
	w[4] += grad * float64(dl.age)
	for _, v := range dl.tokens {
		w[v + offset] += grad
	}
}


// the stochastic gradient descent process
func sgd(dl *DataLine, w map[int]float64, eta float64) {
	yhat := predictLabel(dl, w)
	grad := gradient(dl.clicked, yhat, eta)
	updateWeights(dl, w, grad)
}


/*
	The section for parallel execution
*/


func doAllData(dl []DataLine, w map[int]float64, eta float64, NCPU int) {
	// make a channel for communicating when chunk is finished
	c := make(chan int, NCPU)

	// randomly permute the numbers [0, n)
	perm := rand.Perm(len(dl))

	// assign a chunk of data for each thread
	for i := 0; i < NCPU; i++ {
		// run each chunk in own thread
		go doChunkOfData(dl, w, perm, c, i, len(dl) / NCPU, eta)
	}
	// wait until each thread returns finished
	for i := 0; i < NCPU; i++ {
		<- c
	}
}


// run sgd on a random chunk of the data
func doChunkOfData(dl []DataLine, w map[int]float64, perm []int, c chan int,
		i int, n int, eta float64) {
	for j := i * n; j < (i + 1) * n; j++ {
		sgd(&dl[perm[j]], w, eta)
	}
	c <- 1 // send response to channel to indicate finished
}


func (cd *ClickData) Fit(eta float64, NCPU int) {
	doAllData(cd.data, cd.W, eta, NCPU)
}
