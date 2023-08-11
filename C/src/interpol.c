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

#include <argp.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

struct Arguments {
	char* INPUT_FILE;
	char* OUTPUT_FILE;
	int debug;
	int help;
};

static struct argp_option options[] = {
	{ "input",  'i', "INPUT_FILE",  0, "Specify the input file",  0},
	{ "output", 'o', "OUTPUT_FILE", 0, "Specify the output file", 0},
	{ "debug",  'd', 0, 0, "Show the calculations.",  0},
	{ "help",   'h', 0, 0, "Shows this help screen.", 0},
	{ 0 },
};

error_t parse_opt(int key, char* argv, struct argp_state *state)
{
	struct Arguments *arguments = state->input;

	switch (key) 
	{
		case 'i':
			arguments->INPUT_FILE = argv;
			break;
		case 'o':
			arguments->OUTPUT_FILE = argv;
			break;
		case 'd':
			arguments->debug = 1;
			break;
		case 'h':
			arguments->help = 1;
			break;
		default:
			return ARGP_ERR_UNKNOWN;
	}

	return 0;
}

static struct argp argp = { options, parse_opt, "[options] -i [INPUT] -o [OUTPUT]", 0 };

double **inverseVandermonde(double *x_values, int n, int printDebug)
{
	double *p  = (double *) malloc((n+1) * sizeof(double));
	double *pt = (double *) malloc((n+1) * sizeof(double));
	double *C  = (double *) malloc(n * sizeof(double));
	double *Cp = (double *) malloc(n * sizeof(double));

	p[n] = 1;
	p[n - 1] = -x_values[0];
	C[0] = 1;

	if (printDebug) {
		printf("[*] Initializing arrays:\n");

		printf("[*] p  : { %lf", p[0]);
		for (int i = 1; i < n+1; i++)
			printf(", %lf", p[i]);
		printf(" }\n");

		printf("[*] pt : { %lf", pt[0]);
		for (int i = 1; i < n+1; i++)
			printf(", %lf", pt[i]);
		printf(" }\n");

		printf("[*] C  : { %lf", C[0]);
		for (int i = 1; i < n; i++)
			printf(", %lf", C[i]);
		printf(" }\n");

		printf("[*] Cp : { %lf", Cp[0]);
		for (int i = 1; i < n; i++)
			printf(", %lf", Cp[i]);
		printf(" }\n");

		printf("\n");

		printf("[*] Starting loop i(1...%d)\n", n - 1);
	}

	for (int i = 1; i < n; i++) {
		memcpy(pt, p, sizeof(double) * (n + 1));

		if (printDebug) {
			printf("[*]     Loop i(%d):\n", i);

			printf("[*]     Assigning pt = p({ %lf", p[0]);
			for (int j = 1; j < n+1; j++)
				printf(", %lf", p[j]);
			printf(" })\n");

			printf("[*]     pt : { %lf", pt[0]);
			for (int j = 1; j < n+1; j++)
				printf(", %lf", pt[j]);
			printf(" }\n");

			printf("\n");

			printf("[*]     Starting loop j(0...%d)\n", i);
		}

		for (int j = 0; j < i + 1; j++) {
			if (printDebug) {
				printf("[*]         Loop j(%d):\n", j);

				printf("[*]         Assigning p[%d] = pt[%d] - x_values[%d] * pt[%d]\n", n - i + j - 1, n - i + j - 1, i, n - i + j); 

				printf("[*]         p : { %lf", p[0]);
				for (int k = 1; k < n+1; k++)
					printf(", %lf", p[k]);
				printf(" }\n");

				printf("\n");
			}

			p[n - i + j - 1] = pt[n - i + j - 1] - x_values[i] * pt[n - i + j];
		}
		
		memcpy(Cp, C, sizeof(double) * n);

		if (printDebug) {
			printf("[*]     Assigning Cp = C({ %lf", C[0]);
			for (int j = 1; j < n; j++)
				printf(", %lf", C[j]);
			printf(" })\n");

			printf("[*]     Cp : { %lf", Cp[0]);
			for (int j = 1; j < n; j++)
				printf(", %lf", Cp[j]);
			printf(" }\n");

			printf("\n");

			printf("[*]     Starting loop j(0...%d)\n", i-1);
		}

		for (int j = 0; j < i; j++) {
			if (printDebug) {
				printf("[*]         Loop j(%d):\n", j);

				printf("[*]         Assigning C[%d] = Cp[%d] / (x_values[%d] - x_values[%d])\n", j, j, j, i);

				printf("[*]         Assigning C[%d] = C[%d] - C[%d]\n", i, i, j);

				printf("[*]         C : { %lf", C[0]);
				for (int k = 1; k < n; k++)
					printf(", %lf", C[k]);
				printf(" }\n");

				printf("\n");
			}

			C[j] = Cp[j] / (x_values[j] - x_values[i]);
			C[i] -= C[j];
		}
	}

	double **B = (double **) malloc(n * sizeof(double *));
	for (int i = 0; i <	n; i++) {
		B[i] = (double *) malloc(n * sizeof(double));
	}

	double *c = (double *) malloc(n * sizeof(double));

	if (printDebug) {
		printf("[*]     Initializing arrays:\n");

		printf("[*] B  : {\n");
		for (int i = 0; i < n; i++) {
			printf("      { %lf", B[i][0]); 

			for (int j = 1; j < n; j++)
				printf(", %lf", B[i][j]);

			printf(" }");

			if (i != n-1) {
				printf(",");
			}

			printf("\n");
		}
		printf("    }\n");

		printf("[*] c  : { %lf", c[0]);
		for (int i = 1; i < n; i++)
			printf(", %lf", c[i]);
		printf(" }\n");

		printf("\n");

		printf("[*] Starting loop i(1...%d)\n", n-1);
	}

	for (int i = 0; i < n; i++) {
		c[n - 1] = 1;

		if (printDebug) {
			printf("[*]     Loop i(%d):\n", i);

			printf("[*]     c  : { %lf", c[0]);
			for (int j = 1; j < n; j++)
				printf(", %lf", c[i]);
			printf(" }\n");

			printf("\n");

			printf("[*]     Starting loop j(%d...0)\n", n-2);
		}

		for (int j = n - 2; j >= 0; j--) {
			if (printDebug) {
				printf("[*]         Loop j(%d):\n", j);

				printf("[*]         Assigning c[%d] = p[%d] + x_values[%d] * c[%d]\n", j, j+1, i, j+1);

				printf("[*]         c  : { %lf", c[0]);
				for (int k = 1; k < n; k++)
					printf(", %lf", c[k]);
				printf(" }\n");

				printf("\n");
			}

			c[j] = p[j + 1] + x_values[i] * c[j + 1];
		}

		if (printDebug) {
			printf("[*]     Starting loop j(0...%d)\n", n-1);
		}

		for (int j = 0; j < n; j++) {
			if (printDebug) {
				printf("[*]         Loop j(%d):\n", j);

				printf("[*]         Assigning B[%d][%d] = c[%d] * C[%d]\n", i, j, j, i);

				printf("[*]         B  : {\n");
				for (int k = 0; k < n; k++) {
					printf("              { %lf", B[k][0]); 

					for (int l = 1; l < n; l++)
						printf(", %lf", B[k][l]);

					printf(" }");

					if (k != n-1) {
						printf(",");
					}

					printf("\n");
				}
				printf("            }\n");

				printf("\n");
			}

			B[i][j] = c[j] * C[i];
		}
	}

	return B;
}

double * interpolate(double* x_values, double *y_values, int n, int printDebug)
{
	double *result = (double *) malloc(n * sizeof(double));

	if (printDebug) {
		printf("[*] x values: { %lf", x_values[0]);
		for (int i = 1; i < n; i++)
			printf(", %lf", x_values[i]);
		printf(" }\n");

		printf("[*] y values: { %lf", y_values[0]);
		for (int i = 1; i < n; i++)
			printf(", %lf", y_values[i]);
		printf(" }\n");

		printf("\n");
	}

	double **B = inverseVandermonde(x_values, n, printDebug);

	for (int i = 0; i < n; i++) {
		for (int j = 0; j < n; j++) {
			if (printDebug) {
				printf("[*] Assigning result[%d] += y_values[%d] * B[%d][%d]\n", i, j, j, i);
			}
			result[i] += y_values[j] * B[j][i];
		}
	}

	if (printDebug) {
		printf("\n");
	}

	return result;
}

void printHelp()
{
	printf("Usage:\n");
	printf("./interpol [options] -i [INPUT] -o [OUTPUT]\n");
	printf("  -h\t Shows this help screen.\n");
	printf("  -d\t Show the calculations.\n");
	printf("\n");
	printf("Example:\n");
	printf("  ./interpol -i input.txt -o output.txt\n");
	printf("  ./interpol -d -i input.txt -o output.txt\n");
}

int main(int argc, char *argv[])
{
	struct Arguments arguments;

	// Set the default values for the arguments
	arguments.INPUT_FILE  = NULL;
	arguments.OUTPUT_FILE = NULL;
	arguments.debug = 0;
	arguments.help  = 0;

	// Parse the arguments
	argp_parse(&argp, argc, argv, 0, 0, &arguments);

	int HELP = arguments.help;
	int INVALID_ARGS = (arguments.INPUT_FILE == NULL || arguments.OUTPUT_FILE == NULL);

	// Print help and exit if -h is provided
	if (HELP) {
		printHelp();
	}
	else if (INVALID_ARGS) {
		printf("Unable to parse arguments.\n\n");
		printHelp();
		exit(EXIT_FAILURE);
	}
	else {
		int DEBUG = arguments.debug;
		int printDebug = 0;

		if (DEBUG) {
			printDebug = 1;
		}

		if (printDebug) {
			printf("[*] Reading from input file %s.\n", arguments.INPUT_FILE);
		}

		FILE *read_fptr;

		read_fptr = fopen(arguments.INPUT_FILE, "r");

		if (read_fptr == NULL) {
			fprintf(stderr, "File %s not found.", arguments.INPUT_FILE);
			exit(EXIT_FAILURE);
		}

		int n;
		char line[100];

		if (fgets(line, sizeof(line), read_fptr) == NULL) {
			fprintf(stderr, "Error reading file.\n");
			fclose(read_fptr);
			exit(EXIT_FAILURE);
		}

		if (sscanf(line, "%d", &n) != 1) {
			fprintf(stderr, "Invalid format on the first line.\n");
			fclose(read_fptr);
			exit(EXIT_FAILURE);
		}

		n += 1;

		double *x_values = (double *) malloc(n * sizeof(double));
		double *y_values = (double *) malloc(n * sizeof(double));

		for (int i = 0; i < n; i++) {
			if (fgets(line, sizeof(line), read_fptr) == NULL) {
				fprintf(stderr, "Error reading file on line %d.\n", i+2);
				fclose(read_fptr);
				exit(EXIT_FAILURE);
			}

			double x, y;
			if (sscanf(line, "%lf %lf", &x, &y) != 2) {
				fprintf(stderr, "Invalid format in line %d\n", i+2);
				fclose(read_fptr);
				exit(EXIT_FAILURE);
			}

			x_values[i] = x;
			y_values[i] = y;
		}

		fclose(read_fptr);

		if (printDebug) {
			printf("[*] Points:\n");

			for (int i = 0; i < n; i++) 
				printf("[*] (%lf, %lf)\n", x_values[i], y_values[i]);
			printf("\n");
		}

		double *result = interpolate(x_values, y_values, n, printDebug);

		FILE *write_fptr;

		write_fptr = fopen(arguments.OUTPUT_FILE, "w+");

		if (write_fptr == NULL) {
			fprintf(stderr, "File %s not found.", arguments.OUTPUT_FILE);
			exit(EXIT_FAILURE);
		}

		if (printDebug) {
			printf("[*] Writing to output file %s.\n", arguments.OUTPUT_FILE);
		}

		for (int i = 0; i < n; i++) {
			fprintf(write_fptr, "%lf",result[i]);
			if (i != n-1) {
				fprintf(write_fptr, " ");
			}
		}

		fclose(write_fptr);
	}

	return 0;
}
