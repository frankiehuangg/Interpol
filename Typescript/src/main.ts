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

import { Command } from 'commander';
import fs from 'fs';

import interpolate from './interpol';

const program = new Command();

program
	.usage('npx ts-node src/main.ts [options] -i [input] -o [output]')
	.description("calculates polynomial interpolation")
	.requiredOption('-i, --input <input>', 'specify the input file path')
	.requiredOption('-o, --output <output>','specify the output file path')
	.option('-d, --debug', 'enable debug mode');

program.on('--help', () => {
  console.log('\nExamples:');
  console.log('  $ npx ts-node src/main.ts -i input.txt -o output.txt');
  console.log('  $ npx ts-node src/main.ts -d -i input.txt -o output.txt');
});

program.parse(process.argv);

const flags = program.opts();

const INPUT_FILE : string = flags.input;
const OUTPUT_FILE: string = flags.output;
const DEBUG: boolean = flags.debug || false;

if (DEBUG) {
	console.log("[*] Reading from input file: " + INPUT_FILE + ".");
}

fs.readFile(INPUT_FILE, 'utf8', (err, data) => {
	if (err) {
		console.error('File ' + INPUT_FILE + ' not found: ' + err.message);
		return;
	}

	const lines = data.trim().split('\n');

	const n = parseInt(lines[0], 10) + 1;

	let xValues = [];
	let yValues = [];
	for (let i = 1; i <= n; i++) {
		let values = lines[i].split(' ');
		xValues.push(parseFloat(values[0]));
		yValues.push(parseFloat(values[1]));
	}

	if (DEBUG) {
		console.log('[*] Points:');

		for (let i = 0; i < n; i++)
			console.log('[*] (' + xValues[i] + ', ' + yValues[i] + ')');

		console.log();
	}

	let result = interpolate(xValues, yValues, n, DEBUG);

	let writeData = result.join(' ');

	if (DEBUG) {
		console.log(`[*] Writing to output file: ${OUTPUT_FILE}.`);
	}

	fs.writeFile(OUTPUT_FILE, writeData, (err) => {
		if (err) {
			console.error(`Error writing to ${OUTPUT_FILE} file: ${err.message}`);
			return;
		}
	})
})
