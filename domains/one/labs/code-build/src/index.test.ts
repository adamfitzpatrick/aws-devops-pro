import { handler } from './index';
import type { LambdaEvent } from './index';

const TEST_LABEL = process.env['TEST_LABEL'] || 'Value';
const TEST_DATA_POINT = process.env['TEST_DATA_POINT'] || '10';
const TEST_COEFFICIENT_STRING = process.env['TEST_COEFFICIENT_STRING'] || '5,3,1,2';
const TEST_EXPECTED_RESULT = process.env['TEST_EXPECTED_RESULT'] || '5312';

describe('lambda handler', () => {
  let event: LambdaEvent;

  beforeEach(() => {
    const x = parseInt(TEST_DATA_POINT, 10);
    const coefficients = TEST_COEFFICIENT_STRING.split(',').map(coeffStr => parseInt(coeffStr, 10));
    const degree = coefficients.length - 1;
    event = {
      degree,
      coefficients,
      x,
      label: TEST_LABEL
    };
  });

  test('should evaluate a polynomial', () => {
    const actualResult = handler(event);
    expect(actualResult).toBe(`${TEST_LABEL} equals ${TEST_EXPECTED_RESULT}.`);
    console.log(`Label: ${TEST_LABEL}`);
    console.log(`Coefficients: ${TEST_COEFFICIENT_STRING}`);
    console.log(`x: ${TEST_DATA_POINT}`);
    console.log(`Expected result: ${TEST_EXPECTED_RESULT}`);
    console.log(`Actual result: ${actualResult}`);
  });

  test('should throw if the coefficient array is incorrect for the polynomial degree', () => {
    event.degree = event.degree - 1;
    expect(() => handler(event)).toThrow();
  });
});
