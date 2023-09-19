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

    private int findProximity(int n) {
        int proximity = 0;

        int i = n / 10;
        int j = n % 10;

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0) {
                    continue;
                }

                int i2 = i + x;
                int j2 = j + y;

                if (i2 >= 0 && i2 < 12 && j2 >= 0 && j2 < 10) {
                    if (isMine(i2 * 10 + j2)) {
                        proximity++;
                    }
                }
            }
        }

        return proximity;
    }

    private void findNearbyZeros(int n) {
        if (n < 0 || n >= 120 || isMine(n) || gridText.get(n).getCurrentTextColor() == Color.LTGRAY) {
            return;
        }

        int proximity = Integer.parseInt((String) gridText.get(n).getText());

        if (proximity == 0) {
            gridText.get(n).setBackgroundColor(Color.GRAY);
            gridText.get(n).setTextColor(Color.LTGRAY);

            if (n % 10 != 0) { //Make sure it isn't left-most border
                findNearbyZeros(n - 1);
            }

            if (n % 10 != 9) { //Make sure it isn't right-most border
                findNearbyZeros(n + 1);
            }

            findNearbyZeros(n + 10);
            findNearbyZeros(n - 10);

            //Edge cases to make sure it doesn't go into next area

            if (n % 10 != 0 && n >= 11) {
                findNearbyZeros(n - 11);
            }

            if (n % 10 != 9 && n >= 11) {
                findNearbyZeros(n - 9);
            }

            if (n % 10 != 0 && n <= 108) {
                findNearbyZeros(n + 9);
            }

            if (n % 10 != 9 && n <= 108) {
                findNearbyZeros(n + 11);
            }
        }
    }

    private int countTotal() {
        int total = 0;
        for (int n = 0; n < gridText.size(); n++) {
            if (gridText.get(n).getCurrentTextColor() == Color.LTGRAY) {
                total += 1;
            }
        }

        return total;
    }

    public void onClickTV(View view){
        TextView tv = (TextView) view;
        int n = findIndexOfCellTextView(tv);
        if(didFail) {
            tv.setTextColor(Color.WHITE);
            tv.setBackgroundColor(Color.RED);
            setContentView(R.layout.activity_result);

            TimeTook = findViewById(R.id.TimeTook);
            TimeTook.setText("Used " + String.valueOf(timeCount) + " seconds.");

            Decision = findViewById(R.id.Decision);
            Decision.setText("You lost.");

            Encouragement = findViewById(R.id.Encouragement);
            Encouragement.setText("Better luck next time!");

            Button playAgainButton = findViewById(R.id.playAgain);
            playAgainButton.setOnClickListener(this::restartGame);
        }
        if(!isFlagging) {
            if (!isMine(n)) {
                int proximity = findProximity(n);
                if(proximity == 0) {
                    findNearbyZeros(n);
                }
                tv.setText(String.valueOf(proximity));
                if (tv.getCurrentTextColor() == Color.GREEN || tv.getCurrentTextColor() == Color.BLUE) {
                    if(tv.getCurrentTextColor() == Color.BLUE) {
                        NumFlags += 1;
                        count.setText(String.valueOf(NumFlags));
                    }
                    tv.setTextColor(Color.LTGRAY);
                    tv.setBackgroundColor(Color.GRAY);
                }
                if (countTotal() == 116) { //Win condition
                    setContentView(R.layout.activity_result);

                    TimeTook = findViewById(R.id.TimeTook);
                    TimeTook.setText("Used " + String.valueOf(timeCount) + " seconds.");

                    Decision = findViewById(R.id.Decision);
                    Decision.setText("You won.");

                    Encouragement = findViewById(R.id.Encouragement);
                    Encouragement.setText("Good job!");

                    Button playAgainButton = findViewById(R.id.playAgain);
                    playAgainButton.setOnClickListener(this::restartGame);
                }
            } else if (isMine(n)) {
                tv.setText(R.string.mine);
                didFail = true;
                handler.removeCallbacks(timerRunnable);
            }
        }

        else if(isFlagging) {
            if(tv.getCurrentTextColor() == Color.GREEN) {
                if(NumFlags != 0) {
                    tv.setText(R.string.flag);
                    tv.setTextColor(Color.BLUE);
                    NumFlags--;
                }
            } else if(tv.getText().equals(getString(R.string.flag))) {
                tv.setText(String.valueOf(initialNumbers.get(n)));
                tv.setTextColor(Color.GREEN);
                NumFlags++;
            }
            count.setText(String.valueOf(NumFlags));
        }
    }

    public void onClickButton(View view) {
        isFlagging = !isFlagging;
        toggle.setText(isFlagging ? R.string.flag : R.string.pick);
    }

    public void randomizeMines() {
        Random random = new Random();
        while(mineLocations.size() < 4) {
            int minePlacement = random.nextInt(121);
            if(!isMine(minePlacement)) {
                mineLocations.add(minePlacement);
            }
        }
        Log.d("Minesweeper", "Mine locations: " + mineLocations.toString());
    }



    private void startTime() {
        final TextView timeView = (TextView) findViewById(R.id.timer);

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                int seconds = timeCount;
                String time = String.format("%02d", seconds);
                timeView.setText(time);

                timeCount++;
                handler.postDelayed(this, 1000);
            }
        };

        handler.post(timerRunnable);
    }

}