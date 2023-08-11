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

function inverseVandermonde(xValues: number[], n: number, DEBUG: boolean): number[][] {
	let p : number[] = Array(n + 1).fill(0);
	let pt: number[] = Array(n + 1).fill(0);
	let C : number[] = Array(n).fill(0);
	let Cp: number[] = Array(n).fill(0);

	p[n] = 1;
	p[n - 1] = -xValues[0];
	C[0] = 1;

	if (DEBUG) {
		console.log('[*] Initializing arrays:');
		console.log(`[*] p  : { ${p } }`);
		console.log(`[*] pt : { ${pt} }`);
		console.log(`[*] C  : { ${C } }`);
		console.log(`[*] Cp : { ${Cp} }`);

		console.log();

		console.log(`[*] Starting loop i(1...${(n - 1)})`);
	}

	for (let i = 1; i < n; i++) {
		pt = p;

		if (DEBUG) {
			console.log(`[*] Loop i(${i}):`);

			console.log(`[*]     Assigning pt = p({ ${p} })`);
			console.log();

			console.log(`[*]     Starting loop j(0...${i})`);
		}

		for (let j = 0; j < i + 1; j++) {
			if (DEBUG) {
				console.log(`[*]     Loop j(${j}):`);

				console.log(`[*]         Assigning p[${(n - i + j - 1)}] = pt[${(n - i + j - 1)}] - xValues[${i}] * pt[${(n - i + j)}]`);

				console.log(`[*]         p: { ${p} }`);

				console.log();
			}

			p[n - i + j - 1] = pt[n - i + j - 1] - xValues[i] * pt[n - i + j];
		}

		Cp = C;

		if (DEBUG) {
			console.log(`[*]     Assigning Cp = C({ ${C} })`);
			console.log();

			console.log(`[*]     Starting loop j(0...${(i - 1)})`);
		}

		for (let j = 0; j < i; j++) {
			if (DEBUG) {
				console.log(`[*]     Loop j(${j}):`);

				console.log(`[*]         Assigning C[${j}] = Cp[${j}] / (xValues[${j}] - xValues[${i}])`);
				console.log(`[*]         Assigning C[${i}] = C[${i}] - C[${j}]`);

				console.log(`[*]         C: { ${C} }`);

				console.log();
			}

			C[j] = Cp[j] / (xValues[j] - xValues[i]);
			C[i] -= C[j];
		}
	}

	let B : number[][] = [];
	for (let i = 0; i < n; i++) {
		B.push(Array(n).fill(0));
	}

	let c : number[] = Array(n).fill(0);

	if (DEBUG) {
		console.log('[*] Initializing arrays:');
		console.log(`[*] B  : { ${B} }`);
		console.log(`[*] c  : { ${c} }`);

		console.log();

		console.log(`[*] Starting loop i(1...${(n - 1)})`);
	}

	for (let i = 0; i < n; i++) {
		c[n - 1] = 1;

		if (DEBUG) {
			console.log(`[*] Loop i(${i}):`);

			console.log(`[*]     c  : { ${c} }`);
			console.log();

			console.log(`[*]     Starting loop j(${(n - 2)}...0)`);
		}

		for (let j = n - 2; j >= 0; j--) {
			if (DEBUG) {
				console.log(`[*]     Loop j(${j}):`);

				console.log(`[*]         Assigning c[${j}] = p[${(j + 1)}] + xValues[${i}] * c[${(j + 1)}]`);

				console.log(`[*]         c: { ${c} }`);

				console.log();
			}

			c[j] = p[j + 1] + xValues[i] * c[j + 1];
		}

		if (DEBUG) {
			console.log(`[*]     Starting loop j(0...${n - 1})`);
		}

		for (let j = 0; j < n; j++) {
			if (DEBUG) {
				console.log('[*]     Loop j(' + j + '):');

				console.log(`[*]         Assigning B[${i}][${j}] = c[${j}] * C[${i}]`);

				console.log(`[*]         B  : { ${B} }`);

				console.log();
			}

			B[i][j] = c[j] * C[i];
		}
	}

	return B;
}

export default function interpolate(xValues: number[], yValues:number[], n: number, DEBUG: boolean): number[] {
	let result: number[] = Array(n).fill(0);

	if (DEBUG) {
		console.log(`[*] xValues: { ${xValues} }`);
		console.log(`[*] yValues: { ${yValues} }`);
		console.log();
	}

	let B: number[][] = inverseVandermonde(xValues, n, DEBUG);

	for (let i = 0; i < n; i++) {
		for (let j = 0; j < n; j++) {
			if (DEBUG) {
				console.log(`[*] Assigning result[${i}] += yValues[${j}] * B[${j}][${i}]`);
			}

			result[i] += yValues[j] * B[j][i];
		}
	}

	if (DEBUG) {
		console.log();
	}

	return result;
}
