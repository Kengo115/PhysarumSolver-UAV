package server;

import java.util.Arrays;

public class ICCG {

    public static boolean iccg(double[][] pressCoeff, double[] dataAll, double[] output, int n, int[] maxIter, double[] eps) {
        if (n <= 0) return false;

        double[] r = new double[n];
        double[] p = new double[n];
        double[] y = new double[n];
        double[] r2 = new double[n];
        Arrays.fill(output, 0.0);

        double[] d = new double[n];
        double[][] L = new double[n][n];
        incompleteCholeskyDecomp2(pressCoeff, L, d, n);

        for (int i = 0; i < n; i++) {
            double ax = 0.0;
            for (int j = 0; j < n; j++) {
                ax += pressCoeff[i][j] * output[j];
            }
            r[i] = dataAll[i] - ax;
        }

        icRes(L, d, r, p, n);

        double rr0 = dot(r, p, n);
        double rr1;
        double alpha, beta;

        double e = 0.0;
        int k;
        for (k = 0; k < maxIter[0]; k++) {
            for (int i = 0; i < n; i++) {
                y[i] = dot(pressCoeff[i], p, n);
            }

            alpha = rr0 / dot(p, y, n);

            for (int i = 0; i < n; i++) {
                output[i] += alpha * p[i];
                r[i] -= alpha * y[i];
            }

            icRes(L, d, r, r2, n);
            rr1 = dot(r, r2, n);

            e = Math.sqrt(rr1);
            if (e < eps[0]) {
                k++;
                break;
            }

            beta = rr1 / rr0;
            for (int i = 0; i < n; i++) {
                p[i] = r2[i] + beta * p[i];
            }

            rr0 = rr1;
        }

        maxIter[0] = k;
        eps[0] = e;

        return true;
    }

    public static void icRes(double[][] L, double[] d, double[] r, double[] u, int n) {
        double[] y = new double[n];
        for (int i = 0; i < n; i++) {
            double rly = r[i];
            for (int j = 0; j < i; j++) {
                rly -= L[i][j] * y[j];
            }
            y[i] = rly / L[i][i];
        }

        for (int i = n - 1; i >= 0; i--) {
            double lu = 0.0;
            for (int j = i + 1; j < n; j++) {
                lu += L[j][i] * u[j];
            }
            u[i] = y[i] - d[i] * lu;
        }
    }

    public static boolean incompleteCholeskyDecomp2(double[][] A, double[][] L, double[] d, int n) {
        if (n <= 0) return false;

        L[0][0] = A[0][0];
        d[0] = 1.0 / L[0][0];

        for (int i = 1; i < n; i++) {
            for (int j = 0; j <= i; j++) {
                if (Math.abs(A[i][j]) < 1.0e-10) continue;

                double lld = A[i][j];
                for (int k = 0; k < j; k++) {
                    lld -= L[i][k] * L[j][k] * d[k];
                }
                L[i][j] = lld;
            }

            d[i] = 1.0 / L[i][i];
        }

        return true;
    }

    public static double dot(double[] A, double[] B, int n) {
        double sum = 0.0;
        for (int i = 0; i < n; i++) {
            sum += A[i] * B[i];
        }
        return sum;
    }

    public static void main(String[] args) {
        // テスト用のメイン関数
        double[][] pressCoeff = {
                {4.0, 1.0, 0.0},
                {1.0, 3.0, 1.0},
                {0.0, 1.0, 2.0}
        };
        double[] dataAll = {1.0, 2.0, 3.0};
        double[] output = new double[3];
        int[] maxIter = {100};
        double[] eps = {1e-6};

        boolean result = iccg(pressCoeff, dataAll, output, 3, maxIter, eps);

        if (result) {
            System.out.println("ICCG成功:");
            System.out.println("出力: " + Arrays.toString(output));
            System.out.println("反復回数: " + maxIter[0]);
            System.out.println("誤差: " + eps[0]);
        } else {
            System.out.println("ICCG失敗");
        }
    }
}
