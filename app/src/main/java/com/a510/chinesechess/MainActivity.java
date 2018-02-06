package com.a510.chinesechess;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.a510.chinesechess.View.PVEChessBoardView;

public class MainActivity extends AppCompatActivity {
    private PVEChessBoardView chessBoardView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chessBoardView = (PVEChessBoardView) findViewById(R.id.chessboard);
        chessBoardView.setAIFirst(false);
        chessBoardView.restart();

    }
}
