package sgd

import (
    "bufio" // reading
    "os" // handle file opening
    "strings"
    "strconv"
    "fmt" // print to screen
    "math"
    "runtime" // says how many cores to use
    "math/rand" // to randomly permute data
)

var eta float64 = 0.01 // step size
const NCPU int = 4 // number of processors to use

// contains a line of data
type DataLine struct {
    clicked, depth, position, userid, gender, age int
    tokens []int
}

func main() {
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
    for i := 0; i < 4; i++ {
	    DoAll(dl, w, NCPU)
	}

    fmt.Println("Intercept", w[0])
    fmt.Println("Depth", w[1])
    fmt.Println("Position", w[2])
    fmt.Println("Gender", w[3])
    fmt.Println("Age", w[4])

    fmt.Println("l2 norm", l2norm(w))

    file.Close()
}


func parseLine(s string, dl []DataLine, i int) {
    split := strings.SplitN(s, "|", 7)

    dl[i].clicked, _ = strconv.Atoi(split[0])
    dl[i].depth, _ = strconv.Atoi(split[1])
    dl[i].position, _ = strconv.Atoi(split[2])
    dl[i].userid, _ = strconv.Atoi(split[3])
    dl[i].gender, _ = strconv.Atoi(split[4])
    dl[i].age, _ = strconv.Atoi(split[5])

    tokenSplit := strings.Split(split[6], ",")

    size := len(tokenSplit)
    dl[i].tokens = make([]int, size)

    for k, v := range tokenSplit {
        dl[i].tokens[k], _ = strconv.Atoi(strings.TrimSpace(v))
    }
}


func predictLabel(dl *DataLine, w map[int]float64) float64 {
	offset := 5
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


// logistic regression gradient
func gradient(y int, yhat float64) float64 {
    return (float64(y) - yhat) * eta
}


func updateWeights(dl *DataLine, w map[int]float64, grad float64) {
	offset := 5
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
func sgd(dl *DataLine, w map[int]float64) {
	yhat := predictLabel(dl, w)
	grad := gradient(dl.clicked, yhat)
	updateWeights(dl, w, grad)
}


// calculate the l2norm of the weight vector
func l2norm(w map[int]float64) float64 {
	sum := float64(0)
	for _, v := range w {
		sum += v * v
	}
	return math.Sqrt(sum)
}


// zero all the token entries in the map
// this is to prevent errors when accessing unassigned keys
func initialize(dl *DataLine, w map[int]float64) {
	offset := 5 // offset from the age/gender/position keys
	for _, v := range dl.tokens {
		w[v + offset] = 0.0 // zero data
	}
}


func DoAllSGD(dl []DataLine, w map[int]float64, NCPU int) {
	// make a channel for communicating when chunk is finished
	c := make(chan int, NCPU)

	// randomly permute the numbers [0, n)
	perm := rand.Perm(len(dl))

	// assign a chunk of data for each thread
	for i := 0; i < NCPU; i++ {
		// run each chunk in own thread
		go DoChunkSGD(dl, w, perm, c, i, len(dl) / NCPU)
	}
	// wait until each thread returns finished
	for i := 0; i < NCPU; i++ {
		<- c
	}
}

func DoChunkSGD(dl []DataLine, w map[int]float64, perm []int, c chan int, i int, n int) {
	for j := i * n; j < (i + 1) * n; j++ {
		sgd(&dl[perm[j]], w)
	}
	c <- 1
}
