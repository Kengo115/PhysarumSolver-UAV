package server;
import java.util.ArrayList;
import java.util.Arrays;

public class BiCGSTAB {

    public static double dot(ArrayList<Double> A, ArrayList<Double> B) {
        double result = 0.0;
        for (int i = 0; i < A.size(); i++) {
            result += A.get(i) * B.get(i);
        }
        return result;
    }

    public static void matVecMult(ArrayList<ArrayList<Double>> A, ArrayList<Double> x, ArrayList<Double> result) {
        int n = A.size();
        for (int i = 0; i < n; i++) {
            result.set(i, dot(A.get(i), x));
        }
    }

    public static int BiCGSTAB(ArrayList<ArrayList<Double>> pressCoeff, ArrayList<Double> dataAll, ArrayList<Double> output, int node, int maxIter, double eps) {
        int n = node;
        ArrayList<Double> r = new ArrayList<>(n);
        ArrayList<Double> p = new ArrayList<>(n);
        ArrayList<Double> v = new ArrayList<>(n);
        ArrayList<Double> s = new ArrayList<>(n);
        ArrayList<Double> t = new ArrayList<>(n);
        output.clear();

        // 初期化
        for (int i = 0; i < n; i++) {
            output.add(0.0);  // outputの初期化
            r.add(0.0);       // rの初期化
            p.add(0.0);       // pの初期化
            v.add(0.0);       // vの初期化
            s.add(0.0);       // sの初期化
            t.add(0.0);       // tの初期化
        }

        BiCGSTABRes(pressCoeff, output, dataAll, r, n);
        ArrayList<Double> r0 = new ArrayList<>(r);

        double rho = 1.0;
        double alpha = 1.0;
        double omega = 1.0;

        double res0 = Math.sqrt(dot(r, r));
        if (res0 < eps) {
            return 0;
        }

        for (int k = 0; k < maxIter; k++) {
            double rho1 = dot(r0, r);
            double beta = (rho1 / rho) * (alpha / omega);
            rho = rho1;

            for (int i = 0; i < n; i++) {
                p.set(i, r.get(i) + beta * (p.get(i) - omega * v.get(i)));
            }

            matVecMult(pressCoeff, p, v);
            alpha = rho / dot(r0, v);

            for (int i = 0; i < n; i++) {
                s.set(i, r.get(i) - alpha * v.get(i));
            }

            double res1 = Math.sqrt(dot(s, s));
            if (res1 < eps) {
                for (int i = 0; i < n; i++) {
                    output.set(i, output.get(i) + alpha * p.get(i));
                }
                return k + 1;
            }

            matVecMult(pressCoeff, s, t);
            omega = dot(t, s) / dot(t, t);

            for (int i = 0; i < n; i++) {
                output.set(i, output.get(i) + alpha * p.get(i) + omega * s.get(i));
                r.set(i, s.get(i) - omega * t.get(i));
            }

            double res2 = Math.sqrt(dot(r, r));
            if (res2 < eps) {
                return k + 1;
            }
        }

        return -1;  // 収束しなかった場合
    }

    public static void BiCGSTABRes(ArrayList<ArrayList<Double>> A, ArrayList<Double> x, ArrayList<Double> b, ArrayList<Double> r, int n) {
        ArrayList<Double> Ax = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            Ax.add(0.0);  // Axの初期化
        }
        matVecMult(A, x, Ax);
        for (int i = 0; i < n; i++) {
            r.set(i, b.get(i) - Ax.get(i));
        }
    }

    // テスト用のmainメソッド
    public static void main(String[] args) {
        ArrayList<ArrayList<Double>> pressCoeff = new ArrayList<>();
        pressCoeff.add(new ArrayList<>(Arrays.asList(4.0, 1.0, 0.0)));
        pressCoeff.add(new ArrayList<>(Arrays.asList(1.0, 3.0, 1.0)));
        pressCoeff.add(new ArrayList<>(Arrays.asList(0.0, 1.0, 2.0)));

        ArrayList<Double> dataAll = new ArrayList<>(Arrays.asList(1.0, 2.0, 3.0));
        ArrayList<Double> output = new ArrayList<>();

        int maxIter = 100;
        double eps = 1e-6;

        int result = BiCGSTAB(pressCoeff, dataAll, output, 3, maxIter, eps);

        if (result >= 0) {
            System.out.println("BiCGSTAB成功:");
            System.out.println("出力: " + output);
            System.out.println("反復回数: " + result);
        } else {
            System.out.println("BiCGSTAB失敗");
        }
    }
}
