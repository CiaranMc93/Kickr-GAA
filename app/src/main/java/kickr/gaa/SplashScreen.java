package kickr.gaa;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    private Handler mWaitHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        mWaitHandler.postDelayed(new Runnable() {

            @Override
            public void run() {

                try {

                    Intent intent = new Intent(getApplicationContext(), Fixtures.class);
                    startActivity(intent);
                    finish();  // Finish splash activity since it is no more needed
                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
            }
        }, 2300);  // 2.3 seconds
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWaitHandler.removeCallbacksAndMessages(null);
    }
}
