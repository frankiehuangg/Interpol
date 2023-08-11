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

import org.apache.commons.cli.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Scanner;

public class Main
{
	private static void printHelp(Options options)
	{
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(
				"java -jar interpol.jar [options] -i [INPUT] -o [OUTPUT]",
				"Options:",
				options,
				"""
               Example:
                java -jar interpol.jar -i input.txt -o output.txt
                java -jar interpol.jar -d -i input.txt -o output.txt
               """
		);
	}

	private static Options parseFlags()
	{
		Options options = new Options();

		Option inputFileOption = Option.builder("i")
				.longOpt("input")
				.desc("The input file.")
				.hasArg()
				.build();

		Option outputFileOption = Option.builder("o")
				.longOpt("output")
				.desc("The output file.")
				.hasArg()
				.build();

		Option debugOption = Option.builder("d")
				.longOpt("debug")
				.desc("Show the calculations.")
				.build();

		Option helpOption = Option.builder("h")
				.longOpt("help")
				.desc("Shows this help screen")
				.build();

		options.addOption(inputFileOption);
		options.addOption(outputFileOption);
		options.addOption(debugOption);
		options.addOption(helpOption);

		return options;
	}

	public static void main(String[] args)
	{
		Options options = parseFlags();

		CommandLineParser parser = new DefaultParser();

		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.err.println("Error parsing command-line arguments: " + e.getMessage());
			System.exit(1);
		}

		if (cmd.hasOption("h")) {
			printHelp(options);
		} else if (cmd.getOptionValue("i") == null || cmd.getOptionValue("o") == null) {
			System.out.println("Unable to parse arguments.");
			System.out.println();

			printHelp(options);

			System.exit(1);
		} else {
			final String INPUT_FILE = cmd.getOptionValue("i");
			final String OUTPUT_FILE = cmd.getOptionValue("o");
			final boolean DEBUG = cmd.hasOption("d");

			if (DEBUG) {
				System.out.println("[*] Reading from input file " + INPUT_FILE + ".");
			}

			File read_file;
			Scanner scanner = null;

			try {
				read_file = new File(INPUT_FILE);
				scanner = new Scanner(read_file);
			} catch (FileNotFoundException e) {
				System.err.println("File" + INPUT_FILE + " not found: " + e.getMessage());
				System.exit(1);
			}

			int n = 0;
			try {
				n = scanner.nextInt() + 1;
			} catch (Exception e) {
				System.err.println("Invalid format on the first line.");
				System.exit(1);
			}

			double[] x_values = new double[n];
			double[] y_values = new double[n];

			for (int i = 0; i < n; i++) {
				try {
					x_values[i] = scanner.nextDouble();
					y_values[i] = scanner.nextDouble();
				} catch (Exception e) {
					System.err.println("Invalid format in line " + i + 2);
					System.exit(1);
				}
			}

			scanner.close();

			if (DEBUG) {
				System.out.println("[*] Points:");

				for (int i = 0; i < n; i++)
					System.out.println("[*] (" + x_values[i] + ", " + y_values[i] + ")");

				System.out.println();
			}

			double[] result = Interpol.interpolate(x_values, y_values, n, DEBUG);

			try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE))) {
				for (int i = 0; i < n; i ++) {
					writer.write(Double.toString(result[i]));
					if (i != n - 1) {
						writer.write(" ");
					}
				}
			} catch (Exception e) {
				System.err.println("Error writing to the file: " + e.getMessage());
			}
		}
	}
}
