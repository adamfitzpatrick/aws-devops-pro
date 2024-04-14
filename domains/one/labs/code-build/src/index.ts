export interface LambdaEvent {
  degree: number;
  coefficients: number[];
  x: number;
  label: string
}

export function handler({ degree, coefficients, x, label }: LambdaEvent) {
  if (degree + 1 !== coefficients.length) {
    throw new Error('Degree does not match coefficient count');
  }

  const value = coefficients.reduce((val, coeff, index) => {
    const power = degree - index;
    return val + coeff * (x ** power);
  }, 0);

  return `${label} equals ${value}.`;
}