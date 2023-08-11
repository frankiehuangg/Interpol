"""
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
"""

import sys

def interpolate(points, n, DEBUG):
    """
    Calculate the function constants from the given points

    points  : Array of points (x, y)
    n       : Number of points
    DEBUG   : Enable debug option
    output  : Array of constants
    """
    x_values = [points[i][0] for i in range(n)]
    y_values = [points[i][1] for i in range(n)]

    if (DEBUG):
        print(f"[*] x values: {str(x_values)}")
        print(f"[*] y values: {str(y_values)}")
        print()

    B = matrix.vandermonde(x_values).inverse()

    result = [0 for _ in range(n)]
    
    for i in range(n):
        for j in range(n):
    	    result[i] += B[i][j] * y_values[j]
    
    return result

def print_help():
    """
    Prints the help screen
    """

    print("Usage:")
    print("interpol [options] -i [INPUT] -o [OUTPUT]")
    print("  -h\t Shows this help screen.")
    print("  -d\t Show the calculations.")
    print()
    print("Example:")
    print("  interpol -i input.txt -o output.txt")
    print("  interpol -d -i input.txt -o output.txt")

def main(argc=len(sys.argv), argv=sys.argv):
    if ("-h" in argv or argc == 1):
        print_help()

    elif ("-i" in argv and "-o" in argv):
        DEBUG = False

        if ("-d" in argv):
            DEBUG = True

        assert argc >= 5

        INPUT = argv[argv.index("-i") + 1]
        OUTPUT = argv[argv.index("-o") + 1]

        if (DEBUG):
            print(f"[*] Reading from input file {INPUT}.")

        try:
            with open(INPUT, "r") as f:
                n = int(f.readline())

                points = [point.split(' ') for point in f.readlines()]
                points = [[int(points[i][0]), int(points[i][1])] for i in range(len(points))]

                points.sort()

        except FileNotFoundError:
            print(f"File {INPUT} not found.")

        if (DEBUG):
            print("[*] Points:")
            for i in range(len(points)):
                print(f"[*] {points[i]}.")
            print()

        result = interpolate(points, n+1, DEBUG)

        if (DEBUG):
            print(f"[*] Writing to output file {OUTPUT}.")

        with open(OUTPUT, "w") as f:
            for i in range(n):
                f.write(str(result[i]) + ' ')
            f.write(str(result[n]))

    else:
        print("Unable to parse arguments.")
        print()
        print_help()

if __name__ == "__main__":
    main()
