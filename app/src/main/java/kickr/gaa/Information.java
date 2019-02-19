package kickr.gaa;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by cmcmanus on 11/25/2017.
 */

public class Information extends AppCompatActivity {


    ArrayList<String> match = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.match_info);

        final Bundle bundle = getIntent().getExtras();

        //display the data here
        TextView title = (TextView) findViewById(R.id.match);
        TextView comp = (TextView) findViewById(R.id.comp);
        TextView date = (TextView) findViewById(R.id.date);
        TextView time = (TextView) findViewById(R.id.time);
        TextView venue = (TextView) findViewById(R.id.venue);

        title.setText(bundle.getString("title"));
        title.setTextSize(20);
        title.setTextColor(Color.WHITE);

        comp.setText("Competition: " + bundle.getString("comp"));
        date.setText("Date: " + bundle.getString("date"));
        time.setText("Time: " + bundle.getString("time"));
        venue.setText("Venue: " + bundle.getString("venue"));
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
