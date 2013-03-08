package sgd

import (
    "bufio" // reading
    "os" // handle file opening
    "strings"
    "strconv"
)

/*
    Contains all the data and parsing functions
*/


type DataLine struct {
    clicked, depth, position, userid, gender, age int
    tokens []int
}


type ClickData struct {
    data []DataLine
    W map[int]float64
}


type TestLabels struct {
    Labels []float64
}


func (cd *ClickData) LoadData(s string, set string) {
    cd.data = make([]DataLine, 2335859)

    file, _ := os.Open(s)
    r := bufio.NewReader(file)
    line := ""

    if set == "train" {
        for i := 0; i < 2335859; i++ {
            line, _ = r.ReadString('\n')
            parseLineTrain(line, cd.data, i)
        }
    } else if set == "test" {
        for i := 0; i < 1016552; i++ {
            line, _ = r.ReadString('\n')
            parseLineTest(line, cd.data, i)
        }
    }

    file.Close()
}

func (tl *TestLabels) LoadData(s string) {
    tl.Labels = make([]float64, 1016552)

    file, _ := os.Open(s)
    r := bufio.NewReader(file)
    line := ""

    for i := 0; i < 1016552; i++ {
        line, _ = r.ReadString('\n')
        tl.Labels[i], _ = strconv.ParseFloat(strings.TrimSpace(line), 64)
    }
}


// initialize all the weights to zero
// this prevents "bad stuff" from happening
func (cd *ClickData) InitWeights() {
    cd.W = make(map[int]float64)

    cd.W[0] = 0.0
    cd.W[1] = 0.0
    cd.W[2] = 0.0
    cd.W[3] = 0.0
    cd.W[4] = 0.0

    for i := 0; i < 2335859; i++ {
        initialize(cd.data, cd.W, i)
    }
}


func parseLineTrain(s string, dl []DataLine, i int) {
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


func parseLineTest(s string, dl []DataLine, i int) {
    split := strings.SplitN(s, "|", 6)

    dl[i].clicked = -1
    dl[i].depth, _ = strconv.Atoi(split[0])
    dl[i].position, _ = strconv.Atoi(split[1])
    dl[i].userid, _ = strconv.Atoi(split[2])
    dl[i].gender, _ = strconv.Atoi(split[3])
    dl[i].age, _ = strconv.Atoi(split[4])

    tokenSplit := strings.Split(split[5], ",")

    size := len(tokenSplit)
    dl[i].tokens = make([]int, size)

    for k, v := range tokenSplit {
        dl[i].tokens[k], _ = strconv.Atoi(strings.TrimSpace(v))
    }
}


// zero all the token entries in the map
// this is to prevent errors when accessing unassigned keys
func initialize(dl []DataLine, w map[int]float64, i int) {
    offset := 5 // offset from the age/gender/position keys
    for _, v := range dl[i].tokens {
        w[v + offset] = 0.0 // zero data
    }
}
