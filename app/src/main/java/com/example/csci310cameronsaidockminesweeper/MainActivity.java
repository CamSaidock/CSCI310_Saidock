package com.example.csci310cameronsaidockminesweeper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;
import java.util.Random;
import android.util.Log;
import android.os.Bundle;
import android.os.Handler;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<TextView> gridText;
    private boolean isFlagging = false;
    private Button toggle;
    private int NumFlags = 4;
    private TextView count;
    private ArrayList<Integer> mineLocations;
    private int timeCount = 0;

    private TextView TimeTook;
    private TextView Decision;
    private TextView Encouragement;

    private Handler handler = new Handler();
    private Runnable timerRunnable;
    private boolean didFail = false;

    private ArrayList<Integer> initialNumbers = new ArrayList<>();

    private int dpToPixel(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toggle = findViewById(R.id.mode);
        toggle.setOnClickListener(this::onClickButton);

        count = findViewById(R.id.counter);
        count.setText(String.valueOf(NumFlags));


        gridText = new ArrayList<TextView>();

        GridLayout grid = (GridLayout) findViewById(R.id.grid);
        for (int row = 0; row <= 11; row++) {
            for (int col = 0; col <= 9; col++) {
                TextView tv = new TextView(this);
                tv.setHeight( dpToPixel(32) );
                tv.setWidth( dpToPixel(32) );
                tv.setTextSize( 16 );
                tv.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                tv.setTextColor(Color.GREEN);
                tv.setBackgroundColor(Color.GREEN);
                tv.setOnClickListener(this::onClickTV);

                GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
                lp.setMargins(dpToPixel(2), dpToPixel(2), dpToPixel(2), dpToPixel(2));
                lp.rowSpec = GridLayout.spec(row);
                lp.columnSpec = GridLayout.spec(col);

                grid.addView(tv, lp);

                gridText.add(tv);
            }
        }

        mineLocations = new ArrayList<Integer>();
        randomizeMines();

        startTime();

        for (int i = 0; i < gridText.size(); i++) {
            int proximity = findProximity(i);
            initialNumbers.add(proximity);
            if(!isMine(i)) {
                gridText.get(i).setText(String.valueOf(proximity));
            }
        }
    }

    private void restartGame(View view) {
        setContentView(R.layout.activity_main);
        handler.removeCallbacks(timerRunnable);

        toggle = findViewById(R.id.mode);
        toggle.setOnClickListener(this::onClickButton);

        count = findViewById(R.id.counter);
        count.setText(String.valueOf(4));

        isFlagging = false;
        timeCount = 0;
        NumFlags = 4;
        gridText.clear();
        mineLocations.clear();
        didFail = false;

        GridLayout grid = (GridLayout) findViewById(R.id.grid);
        for (int row = 0; row <= 11; row++) {
            for (int col = 0; col <= 9; col++) {
                TextView tv = new TextView(this);
                tv.setHeight( dpToPixel(32) );
                tv.setWidth( dpToPixel(32) );
                tv.setTextSize( 16 );
                tv.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                tv.setTextColor(Color.GREEN);
                tv.setBackgroundColor(Color.GREEN);
                tv.setOnClickListener(this::onClickTV);

                GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
                lp.setMargins(dpToPixel(2), dpToPixel(2), dpToPixel(2), dpToPixel(2));
                lp.rowSpec = GridLayout.spec(row);
                lp.columnSpec = GridLayout.spec(col);

                grid.addView(tv, lp);

                gridText.add(tv);
            }
        }

        mineLocations = new ArrayList<Integer>();
        randomizeMines();

        startTime();

        for (int i = 0; i < gridText.size(); i++) {
            if(!isMine(i)) {
                gridText.get(i).setText(String.valueOf(findProximity(i)));
            }
        }
    }

    private int findIndexOfCellTextView(TextView tv) {
        for (int n = 0; n < gridText.size(); n++) {
            if (gridText.get(n) == tv)
                return n;
        }
        return -1;
    }

    private boolean isMine(int n) {
        for (int itr : mineLocations) {
            if (n == itr) {
                return true;
            }
        }

        return false;
    }

}