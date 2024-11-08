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
            System.out.println("タイマーが開始されました。");
        }
    }

    // タイマー終了と飛行時間の表示
    public void stop() {
        if (isTiming) {
            this.endTime = System.currentTimeMillis();
            long flightTime = getFlightTime();
            System.out.println("タイマーが停止されました。飛行時間: " + flightTime + " ms");
            this.isTiming = false;
        }
    }

    // 飛行時間の取得
    // 飛行時間の取得 (秒単位)
    public long getFlightTime() {
        return (endTime - startTime) / 1000;
    }


    // タイマーのリセット
    public void reset() {
        this.startTime = 0;
        this.endTime = 0;
        this.isTiming = false;
        System.out.println("タイマーがリセットされました。");
    }

    // タイマーのキャンセル
    public void cancel() {
        this.timer.cancel();
        System.out.println("タイマーがキャンセルされました。");
    }

    // デモ用の飛行シミュレーション
    public void simulateFlight(ArrayList<Integer> path) {
        System.out.println("UAVが経路 " + path + " を飛行中...");
        start();

        // 3秒後に飛行を終了
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                stop();
                timer.cancel(); // タイマーを停止
            }
        }, 3000);
    }
}
