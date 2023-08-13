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

def inverse_vandermonde(x_values, n, DEBUG):
    """
    Calculate the inverse of a vandermonde matrix from the given points

    x_values: Array of (x) points
    n       : Number of points
    DEBUG   : Enable debug option
    output  : Vandermonde matrix
    """
    p     = [0 for _ in range(n+1)]
    p[-1] = 1
    p[-2] = -x_values[0]

    pt = [0 for _ in range(n+1)]
    C  = [0 for _ in range(n)]
    Cp = [0 for _ in range(n)]

    C[0] = 1

    if (DEBUG):
        print "[*] Initializing arrays:"
        print "[*] p  : {}".format(str(p))
        print "[*] pt : {}".format(str(pt))
        print "[*] C  : {}".format(str(C))
        print "[*] Cp : {}".format(str(Cp))
        print 
        print "[*] Starting loop i(1...{})".format(n-1)

    for i in range(1, n):
        pt = p

        if (DEBUG):
            print "[*]     Loop i({}):".format(i)
            print "[*]     Assigning pt = p({})".format(p)
            print "[*]     pt : {}".format(str(pt))
            print 
            print "[*]     Starting loop j(0...{})".format(i)

        for j in range(i+1):
            if (DEBUG):
                print "[*]         Loop j({}):".format(j)
                print "[*]         Assigning p[{}] = pt[{}] - x_values[{}] * pt[{}]".format(n-i+j-1, n-i+j-1, i, n-i+j)
                print "[*]         p : {}".format(str(p))
                print 

            p[n-i+j-1] = pt[n-i+j-1] - x_values[i] * pt[n-i+j]

        Cp = C

        if (DEBUG):
            print "[*]     Assigning Cp = C({})".format(C)
            print "[*]     Cp : {}".format(str(Cp))
            print 
            print "[*]     Starting loop j(0...{})".format(i-1)

        for j in range(i):
            if (DEBUG):
                print "[*]         Loop j({}):".format(j)
                print "[*]         Assigning C[{}] = Cp[{}] / (x_values[{}] - x_values[{}])".format(j, j, j, i)
                print "[*]         Assigning C[{}] = C[{}] - C[{}]".format(i, i, j)
                print "[*]         C = {}".format(str(C))
                print 

            C[j] = Cp[j] / (x_values[j] - x_values[i])
            C[i] -= C[j]

    B = [[0 for _ in range(n)] for _ in range(n)]
    c = [0 for _ in range(n)]

    if (DEBUG):
        print "[*] Initializing arrays:"
        print "[*] B  : {}".format(str(B))
        print "[*] c  : {}".format(str(c))
        print 
        print "[*] Starting loop i(1...{})".format(n-1)

    for i in range(n):
        c[-1] = 1

        if (DEBUG):
            print "[*]     Loop i({}):".format(i)
            print "[*]     Assigning c = {}".format(c)
            print "[*]     c = {}".format(str(c))
            print 
            print "[*]     Starting loop j({}...0)".format(n-2)

        for j in range(n-2, -1, -1):
            if (DEBUG):
                print "[*]         Loop j({}):".format(j)
                print "[*]         Assigning c[{}] = p[{}] + x_values[{}] * c[{}]".format(j, j+1, i, j+1)
                print "[*]         c = {}".format(str(c))
                print 

            c[j] = p[j + 1] + x_values[i] * c[j + 1]

        if (DEBUG):
            print "[*]     Starting loop j(0...{})".format(n-1)

        for j in range(n):
            if (DEBUG):
                print "[*]         Loop j({}):".format(j)
                print "[*]         Assigning B[{}][{}] = c[{}] * C[{}]".format(j, i, j, i)
                print "[*]         B = {}".format(str(B))
                print 

            B[i][j] = c[j] * C[i]

    return B

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
        print "[*] x values: {}".format(str(x_values))
        print "[*] y values: {}".format(str(y_values))
        print 

    B = inverse_vandermonde(x_values, n, DEBUG)

    result = [0 for _ in range(n)]

    for i in range(n):
        for j in range(n):
            result[i] += y_values[j] * B[j][i]

    return result

def print_help():
    """
    Prints the help screen
    """

    print "Usage:"
    print "interpol [options] -i [INPUT] -o [OUTPUT]"
    print "  -h\t Shows this help screen."
    print "  -d\t Show the calculations."
    print 
    print "Example:"
    print "  interpol -i input.txt -o output.txt"
    print "  interpol -d -i input.txt -o output.txt"

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
            print "[*] Reading from input file {}.".format(INPUT)

        try:
            with open(INPUT, "r") as f:
                n = int(f.readline())

                points = [point.split(' ') for point in f.readlines()]
                points = [[int(points[i][0]), int(points[i][1])] for i in range(len(points))]

                points.sort()

        except FileNotFoundError:
            print "File {} not found.".format(INPUT)

        if (DEBUG):
            print "[*] Points:"
            for i in range(len(points)):
                print "[*] {}.".format(points[i])
            print 

        result = interpolate(points, n+1, DEBUG)

        if (DEBUG):
            print "[*] Writing to output file {}.".format(OUTPUT)

        with open(OUTPUT, "w") as f:
            for i in range(n):
                f.write(str(result[i]) + ' ')
            f.write(str(result[n]))

    else:
        print "Unable to parse arguments."
        print 
        print_help()

if __name__ == "__main__":
    main()
