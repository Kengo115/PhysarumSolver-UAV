package item;
import java.util.Timer;
import java.util.TimerTask;

public class UavTimer {
    private Timer timer;
    private int seconds;
    private static final int TIMEOUT_SECONDS = 180;

    public UavTimer() {
        resetTimer();
    }
    public void startTimer(){
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                seconds--;
                if (seconds <= 0) {
                    System.out.println("時間超過しました");
                    stopTimer();
                }
            }
        }, 1000, 1000);
    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public void resetTimer() {
        stopTimer();
        seconds = TIMEOUT_SECONDS;
    }
}
