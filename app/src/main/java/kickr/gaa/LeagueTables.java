package kickr.gaa;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class LeagueTables extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.league_table_layout);

        System.out.print(Fixtures.matchObjList);

    }
}
