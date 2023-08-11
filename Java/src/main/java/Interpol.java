// MIT License
// 
// Copyright (c) 2023 Frankie Huang
// 
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
// 
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

import java.util.Arrays;

public class Interpol
{
    private static double[][] inverseVandermonde(double[] xValues, int n, boolean DEBUG)
    {
        double[] p  = new double[n+1];
        double[] pt = new double[n+1];
        double[] C  = new double[n];
        double[] Cp = new double[n];

        p[n] = 1.0;
        p[n - 1] = -xValues[0];
        C[0] = 1.0;

        if (DEBUG) {
            System.out.println("[*] Initializing arrays:");

            System.out.print("[*] p  : { " + p[0]);
            for (int i = 1; i < n + 1; i++)
                System.out.print(", " + p[i]);
            System.out.println(" }");

            System.out.print("[*] pt : { " + pt[0]);
            for (int i = 1; i < n + 1; i++)
                System.out.print(", " + pt[i]);
            System.out.println(" }");

            System.out.print("[*] C  : { " + C[0]);
            for (int i = 1; i < n; i++)
                System.out.print(", " + C[i]);
            System.out.println(" }");

            System.out.print("[*] Cp : { " + Cp[0]);
            for (int i = 1; i < n; i++)
                System.out.print(", " + Cp[i]);
            System.out.println(" }");

            System.out.println();

            System.out.println("[*] Starting loop i(1..." + (n - 1) + ")");
        }

        for (int i = 1; i < n; i++) {
            pt = Arrays.copyOf(p, n+1);

            if (DEBUG) {
                System.out.printf("[*]     Loop i(%d):\n", i);

                System.out.printf("[*]     Assigning pt = p({ %f", p[0]);
                for (int j = 1; j < n+1; j++)
                    System.out.printf(", %f", p[j]);
                System.out.println(" })");

                System.out.println();

                System.out.printf("[*]     Starting loop j(0...%d)\n", i);
            }

            for (int j = 0; j < i + 1; j++) {
                if (DEBUG) {
                    System.out.printf("[*]         Loop j(%d):\n", j);

                    System.out.printf("[*]         Assigning p[%d] = pt[%d] - xValues[%d] * pt[%d]\n",
                            n - i + j - 1,
                            n - i + j - 1,
                            i,
                            n - i + j
                    );

                    System.out.printf("[*]         p : { %f", p[0]);
                    for (int k = 1; k < n+1; k++)
                        System.out.printf(", %f", p[k]);
                    System.out.println(" }");

                    System.out.println();
                }

                p[n - i + j - 1] = pt[n - i + j - 1] - xValues[i] * pt[n - i + j];
            }

            Cp = Arrays.copyOf(C, n);

            if (DEBUG) {
                System.out.printf("[*]     Assigning Cp = C({ %f", C[0]);
                for (int j = 1; j < n; j++)
                    System.out.printf(", %f", C[j]);
                System.out.println(" })");

                System.out.println();

                System.out.printf("[*]     Starting loop j(0...%d)\n", i-1);
            }

            for (int j = 0; j < i; j++) {
                if (DEBUG) {
                    System.out.printf("[*]         Loop j(%d):\n", j);

                    System.out.printf("[*]         Assigning C[%d] = Cp[%d] / (xValues[%d] - xValues[%d])\n", j, j, j, i);

                    System.out.printf("[*]         Assigning C[%d] = C[%d] - C[%d]\n", i, i, j);

                    System.out.printf("[*]         C : { %f", C[0]);
                    for (int k = 1; k < n; k++)
                        System.out.printf(", %f", C[k]);
                    System.out.println(" }");

                    System.out.println();
                }

                C[j] = Cp[j] / (xValues[j] - xValues[i]);
                C[i] -= C[j];
            }
        }

        double[][] B = new double[n][n];
        for (int i = 0; i < n; i++)
            B[i] = new double[n];

        double[] c = new double[n];

        if (DEBUG) {
            System.out.println("[*] Initializing arrays:");

            System.out.println("[*] B  : {");
            for (int i = 0; i < n; i++) {
                System.out.printf("      { %f", B[i][0]);

                for (int j = 1; j < n; j++)
                    System.out.printf(", %f", B[i][j]);

                System.out.print(" }");

                if (i != n-1)
                    System.out.print(",");

                System.out.println();
            }
            System.out.println("    }");

            System.out.printf("[*] c  : { %f", c[0]);
            for (int i = 1; i < n; i++)
                System.out.printf(", %f", c[i]);
            System.out.println(" }");

            System.out.println();

            System.out.printf("[*] Starting loop i(1...%d)\n", n-1);
        }

        for (int i = 0; i < n; i++) {
            c[n - 1] = 1.0;

            if (DEBUG) {
                System.out.printf("[*]     Loop i(%d):\n", i);

                System.out.printf("[*]     c  : { %f", c[0]);
                for (int j = 1; j < n; j++)
                    System.out.printf(", %f", c[i]);
                System.out.println(" }");

                System.out.println();

                System.out.printf("[*]     Starting loop j(%d...0)\n", n-2);
            }

            for (int j = n - 2; j >= 0; j--) {
                if (DEBUG) {
                    System.out.printf("[*]         Loop j(%d):\n", j);

                    System.out.printf("[*]         Assigning c[%d] = p[%d] + xValues[%d] * c[%d]\n", j, j+1, i, j+1);

                    System.out.printf("[*]         c  : { %f", c[0]);
                    for (int k = 1; k < n; k++)
                        System.out.printf(", %f", c[k]);
                    System.out.println(" }");

                    System.out.println();
                }

                c[j] = p[j + 1] + xValues[i] * c[j + 1];
            }

            if (DEBUG) {
                System.out.printf("[*]     Starting loop j(0...%d)\n", n-1);
            }

            for (int j = 0; j < n; j++) {
                if (DEBUG) {
                    System.out.printf("[*]         Loop j(%d):\n", j);

                    System.out.printf("[*]         Assigning B[%d][%d] = c[%d] * C[%d]\n", i, j, j, i);

                    System.out.println("[*]         B  : {");
                    for (int k = 0; k < n; k++) {
                        System.out.printf("              { %f", B[k][0]);

                        for (int l = 1; l < n; l++)
                            System.out.printf(", %f", B[k][l]);

                        System.out.print(" }");

                        if (k != n-1)
                            System.out.print(",");

                        System.out.println();
                    }
                    System.out.println("            }");

                    System.out.println();
                }

                B[i][j] = c[j] * C[i];
            }
        }

        return B;
    }
    public static double[] interpolate(double[] xValues, double[] yValues, int n, boolean DEBUG)
	{
        double[] result = new double[n];

        if (DEBUG) {
            System.out.printf("[*] x values: { %f", xValues[0]);
            for (int i = 1; i < n; i++)
                System.out.printf(", %f", xValues[i]);
            System.out.println(" }");

            System.out.printf("[*] y values: { %f", yValues[0]);
            for (int i = 1; i < n; i++)
                System.out.printf(", %f", yValues[i]);
            System.out.println(" }");

            System.out.println();
        }

        double[][] B = inverseVandermonde(xValues, n, DEBUG);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (DEBUG) {
                    System.out.printf("[*] Assigning result[%d] += y_values[%d] * B[%d][%d]\n", i, j, j, i);
                }

                result[i] += yValues[j] * B[j][i];
            }
        }

        if (DEBUG) {
            System.out.println();
        }

        return result;
    }
}
