package server;

import java.util.ArrayList;
import java.util.Arrays;


public class ICCG {

    public static boolean iccg(ArrayList<ArrayList<Double>> pressCoeff, ArrayList<Double> dataAll, ArrayList<Double> output, int n, int[] maxIter, double[] eps) {
        if (n <= 0) return false;

        ArrayList<Double> r = new ArrayList<>(n);
        ArrayList<Double> p = new ArrayList<>(n);
        ArrayList<Double> y = new ArrayList<>(n);
        ArrayList<Double> r2 = new ArrayList<>(n); // r2をArrayListに変更

        output.clear();  // output をクリア
        for (int i = 0; i < n; i++) {
            output.add(0.0);  // 出力の初期化
            r.add(0.0);       // r の初期化
            p.add(0.0);       // p の初期化
            y.add(0.0);       // y の初期化
            r2.add(0.0);      // r2 の初期化
        }

        double[] d = new double[n];
        double[][] L = new double[n][n];
        incompleteCholeskyDecomp2(pressCoeff, L, d, n);

        // r の初期化
        for (int i = 0; i < n; i++) {
            double ax = 0.0;
            for (int j = 0; j < n; j++) {
                ax += pressCoeff.get(i).get(j) * output.get(j);
            }
            r.set(i, dataAll.get(i) - ax); // ArrayListに設定
        }

        // 初期値を p に設定
        icRes(L, d, r, p, n);

        double rr0 = dot(r, p, n); // ArrayListを直接使用
        double rr1;
        double alpha, beta;

        double e = 0.0;
        int k;
        for (k = 0; k < maxIter[0]; k++) {
            // pressCoeff.get(i) を double[] に変換し、dotを計算
            for (int i = 0; i < n; i++) {
                ArrayList<Double> row = new ArrayList<>(n);
                for (int j = 0; j < n; j++) {
                    row.add(pressCoeff.get(i).get(j));
                }
                y.set(i, dot(row, p, n)); // ArrayListを使用
            }

            alpha = rr0 / dot(p, y, n); // ArrayListを使用

            for (int i = 0; i < n; i++) {
                output.set(i, output.get(i) + alpha * p.get(i));  // 出力の更新
                r.set(i, r.get(i) - alpha * y.get(i));        // r の更新
            }

            icRes(L, d, r, r2, n); // r2もArrayListなのでそのまま渡す
            rr1 = dot(r, r2, n); // dotメソッドにrとr2を渡す

            e = Math.sqrt(rr1);
            if (e < eps[0]) {
                k++;
                break;
            }

            beta = rr1 / rr0;
            for (int i = 0; i < n; i++) {
                p.set(i, r2.get(i) + beta * p.get(i));  // p の更新
            }

            rr0 = rr1;
        }

        maxIter[0] = k;
        eps[0] = e;

        return true;
    }

    public static void icRes(double[][] L, double[] d, ArrayList<Double> r, ArrayList<Double> u, int n) {
        double[] y = new double[n];
        for (int i = 0; i < n; i++) {
            double rly = r.get(i);
            for (int j = 0; j < i; j++) {
                rly -= L[i][j] * y[j];
            }
            y[i] = rly / L[i][i];
        }

        for (int i = n - 1; i >= 0; i--) {
            double lu = 0.0;
            for (int j = i + 1; j < n; j++) {
                lu += L[j][i] * u.get(j); // uをArrayListから取得
            }
            u.set(i, y[i] - d[i] * lu); // ArrayListに設定
        }
    }

    public static boolean incompleteCholeskyDecomp2(ArrayList<ArrayList<Double>> A, double[][] L, double[] d, int n) {
        if (n <= 0) return false;

        L[0][0] = A.get(0).get(0);
        d[0] = 1.0 / L[0][0];

        for (int i = 1; i < n; i++) {
            for (int j = 0; j <= i; j++) {
                if (Math.abs(A.get(i).get(j)) < 1.0e-10) continue;

                double lld = A.get(i).get(j);
                for (int k = 0; k < j; k++) {
                    lld -= L[i][k] * L[j][k] * d[k];
                }
                L[i][j] = lld;
            }

            d[i] = 1.0 / L[i][i];
        }

        return true;
    }

    public static double dot(ArrayList<Double> A, ArrayList<Double> B, int n) {
        double sum = 0.0;
        for (int i = 0; i < n; i++) {
            sum += A.get(i) * B.get(i); // 修正
        }
        return sum;
    }

    public static ArrayList<Double> toArrayList(double[] array) {
        ArrayList<Double> list = new ArrayList<>();
        for (double value : array) {
            list.add(value);
        }
        return list;
    }

    public static void main(String[] args) {
        // テスト用のメイン関数
        ArrayList<ArrayList<Double>> pressCoeff = new ArrayList<>();
        pressCoeff.add(new ArrayList<>(Arrays.asList(4.0, 1.0, 0.0)));
        pressCoeff.add(new ArrayList<>(Arrays.asList(1.0, 3.0, 1.0)));
        pressCoeff.add(new ArrayList<>(Arrays.asList(0.0, 1.0, 2.0)));

        ArrayList<Double> dataAll = new ArrayList<>(Arrays.asList(1.0, 2.0, 3.0));
        ArrayList<Double> output = new ArrayList<>();
        int[] maxIter = {100};
        double[] eps = {1e-6};

        boolean result = iccg(pressCoeff, dataAll, output, 3, maxIter, eps);

        if (result) {
            System.out.println("ICCG成功:");
            System.out.println("出力: " + output);
            System.out.println("反復回数: " + maxIter[0]);
            System.out.println("誤差: " + eps[0]);
        } else {
            System.out.println("ICCG失敗");
        }
    }
}
