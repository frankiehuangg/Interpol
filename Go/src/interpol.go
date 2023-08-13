/*
MIT License

Copyright (c) 2023 Frankie Huang

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package main

import (
	"bufio"
	"flag"
	"fmt"
	"os"
	"strconv"
	"strings"
)

type Arguments struct {
	INPUT_FILE string
	OUTPUT_FILE string
	debug bool
	help bool
}

func inverseVandermonde(x_values []float64, n int, DEBUG bool) ([][]float64) {
	p  := make([]float64, n+1)
	pt := make([]float64, n+1)
	C  := make([]float64, n)
	Cp := make([]float64, n)
	
	p[n] = 1
	p[n - 1] = -x_values[0]
	C[0] = 1

	if DEBUG {
		fmt.Println("[*] Initializing arrays:")

		fmt.Println("[*] p:", p)

		fmt.Println("[*] pt:", pt)

		fmt.Println("[*] C:", C)

		fmt.Println("[*] Cp:", Cp)

		fmt.Println("")

		fmt.Printf("Starting loop i(1...%d)\n", n - 1)
	}

	for i := 1; i < n; i++ {
		copy(pt, p)

		if DEBUG {
			fmt.Printf("[*]     Loop i(%d):\n", i)

			fmt.Printf("[*]     Assigning pt = p(%v)\n", p)

			fmt.Println("[*]     pt:", pt)

			fmt.Println("")

			fmt.Printf("[*]     Starting loop j(0...%d)\n", i)
		}

		for j := 0; j < i + 1; j++ {
			if DEBUG {
				fmt.Printf("[*]         Loop j(%d):\n", j)

				fmt.Printf("[*]         Assigning p[%d] = pt[%d] - x_values[%d] * pt[%d]\n", n - i + j - 1, n - i + j - 1, i, n - i + j)

				fmt.Println("[*]         p:", p)

				fmt.Println("")
			}

			p[n - i + j - 1] = pt[n - i + j - 1] - x_values[i] * pt[n - i + j]
		}

		copy(Cp, C)

		if DEBUG {
			fmt.Printf("[*]     Assigning Cp = C(%v)\n", C)

			fmt.Println("[*]     Cp:", Cp)

			fmt.Println("")

			fmt.Printf("[*]     Starting loop j(0...%d)\n", i-1)
		}

		for j := 0; j < i; j++ {
			if DEBUG {
				fmt.Printf("[*]         Loop j(%d):\n", j)

				fmt.Printf("[*]         Assigning C[%d] = Cp[%d] / (x_values[%d] - x_values[%d])\n", j, j, j, i)

				fmt.Printf("[*]         Assigning C[%d] = C[%d] - C[%d]\n", i, i, j)

				fmt.Println("[*]         C:", C)

				fmt.Println("")
			}

			C[j] = Cp[j] / (x_values[j] - x_values[i])
			C[i] -= C[j]
		}
	}

	B := make([][]float64, n)
	for i := 0; i < n; i++ {
		B[i] = make([]float64, n)
	}

	c := make([]float64, n)

	if DEBUG {
		fmt.Println("[*]     Initializing arrays:")

		fmt.Println("[*]     B:", B)

		fmt.Println("[*]     c:", c)

		fmt.Println("")

		fmt.Printf("[*] Starting loop i(1...%d)\n", n-1)
	}

	for i := 0; i < n; i++ {
		c[n - 1] = 1

		if DEBUG {
			fmt.Printf("[*]     Loop i(%d):\n", i)

			fmt.Println("[*]     c:", c)

			fmt.Println("")

			fmt.Printf("[*]     Starting loop j(0...%d)\n", n-2)
		}

		for j := n - 2; j >= 0; j-- {
			if DEBUG {
				fmt.Printf("[*]         Loop j(%d):\n", j)

				fmt.Printf("[*]         Assigning c[%d] = p[%d] + x_values[%d] * c[%d]\n", j, j + 1, i, j + 1)

				fmt.Println("[*]         c:", c)

				fmt.Println("")
			}

			c[j] = p[j + 1] + x_values[i] * c[j + 1]
		}

		if DEBUG {
			fmt.Printf("[*]     Starting loop j(0...%d)\n", n - 1)
		}

		for j := 0; j < n; j++ {
			if DEBUG {
				fmt.Printf("[*]         Loop j(%d):\n", j)

				fmt.Printf("[*]         Assigning B[%d][%d] = c[%d] * C[%d]\n", i, j, j, i)

				fmt.Println("[*]         B:", B)

				fmt.Println("")
			}

			B[i][j] = c[j] * C[i]
		}
	}

	return B
}

func interpolate(x_values []float64, y_values []float64, n int, DEBUG bool) ([]float64) {
	result := make([]float64, n)

	if DEBUG {
		fmt.Println("[*] x values:", x_values)
		fmt.Println("[*] y values:", y_values)
		fmt.Println("")
	}

	B := inverseVandermonde(x_values, n, DEBUG)

	for i := 0; i < n; i++ {
		for j := 0; j < n; j++ {
			if DEBUG {
				fmt.Printf("[*] Assigning result[%d] += y_values[%d] * B[%d][%d]\n", i, j, j, i)
			}

			result[i] += y_values[j] * B[j][i]
		}
	}

	if DEBUG {
		fmt.Println("")
	}

	return result
}

func printHelp() {
	fmt.Println("Usage:")
	fmt.Println("go run interpol.go [options] -i [INPUT] -o [OUTPUT]")
	fmt.Println("  -h\t Shows this help screen.")
	fmt.Println("  -d\t Show the calculations.")
	fmt.Println("")
	fmt.Println("Example:")
	fmt.Println("  go run interpol.go -i input.txt -o output.txt")
	fmt.Println("  go run interpol.go -d -i input.txt -o output.txt")
}

func main() {
	var arguments Arguments

	// Parse the arguments
	flag.StringVar(&arguments.INPUT_FILE, "i", "", "The input file.")
	flag.StringVar(&arguments.OUTPUT_FILE, "o", "", "The output file.")
	flag.BoolVar(&arguments.debug, "d", false, "Show the calculations.")
	flag.BoolVar(&arguments.help, "h", false, "Shows this help screen.")

	flag.Parse()

	var HELP bool = arguments.help
	var INVALID_ARGS bool = (arguments.INPUT_FILE == "" || arguments.OUTPUT_FILE == "")

	if HELP {
		printHelp()
	} else if INVALID_ARGS {
		fmt.Println("Unable to parse arguments.")
		fmt.Println("")

		printHelp()

		os.Exit(1)
	} else {
		var INPUT_FILE string = arguments.INPUT_FILE
		var OUTPUT_FILE string = arguments.OUTPUT_FILE
		var DEBUG bool = arguments.debug

		if DEBUG {
			fmt.Print("[*] Reading from input file ", INPUT_FILE, ".\n")
		}

		read_f, err := os.Open(INPUT_FILE)
		if err!= nil {
			fmt.Printf("File %s not found.\n", INPUT_FILE)
			os.Exit(1)
		}
		defer read_f.Close()

		read_scanner := bufio.NewScanner(read_f)
		
		read_scanner.Scan()
		n, err := strconv.Atoi(read_scanner.Text())
		if err != nil {
			fmt.Println("Invalid format on the first line.")
			os.Exit(1)
		}

		n += 1

		fmt.Println(n)

		x_values := make([]float64, n)
		y_values := make([]float64, n) 

		for i := 0; i < n && read_scanner.Scan(); i++ {
			line := read_scanner.Text()
			coords := strings.Fields(line)

			if len(coords) != 2 {
				fmt.Println("Invalid format on line", i+2)
				os.Exit(1)
			}

			x, err := strconv.ParseFloat(coords[0], 64)
			if err != nil {
				fmt.Println("Unable to parse the first value on line", i+2)
				os.Exit(1)
			}

			y, err := strconv.ParseFloat(coords[1], 64)
			if err != nil {
				fmt.Println("Unable to parse the second value on line", i+2)
				os.Exit(1)
			}

			x_values[i] = x
			y_values[i] = y
		}

		if DEBUG {
			fmt.Println("[*] Points:")

			for i := 0; i < n; i++ {
				fmt.Printf("[*] (%f, %f)\n", x_values[i], y_values[i])
			}

			fmt.Println("")
		}

		result := interpolate(x_values, y_values, n, DEBUG)

		if DEBUG {
			fmt.Print("[*] Writing to input file ", OUTPUT_FILE, ".\n")
		}

		write_f, err := os.Create(OUTPUT_FILE)
		if err!= nil {
			fmt.Printf("File %s not found.\n", OUTPUT_FILE)
			os.Exit(1)
		}
		defer write_f.Close()

		for i := 0; i < n; i++ {
			myString := strconv.FormatFloat(result[i], 'f', -1, 64)

			_, _ = write_f.WriteString(myString)
			
			if i != n-1 {
				_, _ = write_f.WriteString(" ")
			}
		}
	}
}
