package item;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class UAVTimer {
    private Timer timer;
    private long startTime;
    private long endTime;
    private boolean isTiming;

    public UAVTimer() {
        this.timer = new Timer();
        this.startTime = 0;
        this.endTime = 0;
        this.isTiming = false;
    }

    // タイマー開始
    public void start() {
        if (!isTiming) {
            this.startTime = System.currentTimeMillis();
            this.isTiming = true;
            System.out.println("UAVタイマーが開始されました。");
        }
    }

    // タイマー終了と飛行時間の表示
    public void stop() {
        if (isTiming) {
            this.endTime = System.currentTimeMillis();
            long flightTime = getFlightTime();
            System.out.println("UAVタイマーが停止されました。飛行時間: " + flightTime + " ms");
            this.isTiming = false;
        }
    }

    // 飛行時間の取得
    // 飛行時間の取得 (秒単位)
    public long getFlightTime() {
        long flightTime;
        if (isTiming) {
            flightTime = (System.currentTimeMillis() - startTime) / 1000;
            System.out.println("飛行時間: " + flightTime + "s");
            return flightTime; // タイマーが動作中の場合の経過時間
        } else {
            flightTime = (endTime - startTime) / 1000;
            System.out.println("最終飛行時間: " + flightTime + "s");
            return flightTime; // タイマー停止後の最終飛行時間
        }
    }



    // タイマーのリセット
    public void reset() {
        this.startTime = 0;
        this.endTime = 0;
        this.isTiming = false;
        System.out.println("UAVタイマーがリセットされました。");
    }

    // タイマーのキャンセル
    public void cancel() {
        this.stop();
        this.timer.cancel();
        System.out.println("UAVタイマーがキャンセルされました。");
    }
}
