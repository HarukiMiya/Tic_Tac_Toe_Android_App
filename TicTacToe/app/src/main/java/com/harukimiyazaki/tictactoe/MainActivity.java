//
// This file is the main activity class of a tic tac toe game app for android.
// (I am using Android Studio)
//
// Implemented Minimax Algorithm, and used SharedPreferences to store the data
// to check night mode and a score counter which user can observe how many wins
// Human or AI earned.
//
// This is a file that I'm still working on, so it is not completed file.
//

package com.harukimiyazaki.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.gridlayout.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    //0 = 'X', 1 = 'O', 2 = 'EMPTY'

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    MediaPlayer mplayer1;
    MediaPlayer mplayer01;
    MediaPlayer mplayer2;
    boolean mplayer1IsPlaying = false;
    boolean mplayer01IsPlaying = false;
    boolean mplayer2IsPlaying = false;
    MediaPlayer mplayerButtonMenu;
    MediaPlayer mplayerButtonEffect;
    MediaPlayer mplayerButtonPlayAgain;

    int count = 0;

    int player;
    int[] board = {2,2,2,2,2,2,2,2,2};
    int[][] winningPositions = {{0,1,2},{3,4,5},{6,7,8},{0,3,6},{1,4,7},{2,5,8},{0,4,8},{2,4,6}};
    boolean gameIsActive = false;
    boolean musicChange = false;
    boolean isTie = true; // 0 : tie, otherwise, not tie.

    public ImageView setImage(int move, ImageView img){
        switch (move) {
            case 0:
                img = findViewById(R.id.imageView0);
                break;
            case 1:
                img = findViewById(R.id.imageView1);
                break;
            case 2:
                img = findViewById(R.id.imageView2);
                break;
            case 3:
                img = findViewById(R.id.imageView3);
                break;
            case 4:
                img = findViewById(R.id.imageView4);
                break;
            case 5:
                img = findViewById(R.id.imageView5);
                break;
            case 6:
                img = findViewById(R.id.imageView6);
                break;
            case 7:
                img = findViewById(R.id.imageView7);
                break;
            case 8:
                img = findViewById(R.id.imageView8);
                break;
        }
        return img;
    }

    public void menu(View view){ //First screen
        gameIsActive = true;
        mplayerButtonMenu.start();
        if(mplayer1IsPlaying && count == 1) {
            mplayer1.stop();
            mplayer1.release();
            mplayer1 = null;
            mplayer1IsPlaying = false;
        }
        if(mplayer01IsPlaying && count == 2) {
            mplayer01.pause();
            mplayer01.release();
            mplayer01 = null;
            mplayer01IsPlaying = false;
        }
        if(!mplayer2IsPlaying){
            mplayer2 = MediaPlayer.create(this, R.raw.music2);
            mplayer2.setLooping(true);
            mplayer2.start();
            mplayer2IsPlaying = true;
        }

        if(view.getId() == R.id.button1){
            // Human Goes First
            player = 0;
        }else if(view.getId() == R.id.button2){
            // AI Goes First
            player = 1;
            ImageView img = findViewById(R.id.imageView0);
            Random rn = new Random();
            int move = rn.nextInt(9);
            setImage(move,img).setImageResource(R.drawable.cross);
            board[move] = 0;
        }else{
            // Random
            Random random = new Random();
            player = random.nextInt(2);
            if(player != 0){
                ImageView img = findViewById(R.id.imageView0);
                Random rn = new Random();
                int move = rn.nextInt(9);
                setImage(move,img).setImageResource(R.drawable.cross);
                board[move] = 0;
            }
        }
        LinearLayout selectLayout = findViewById(R.id.selectLayout);
        selectLayout.setVisibility(View.INVISIBLE);
    }

    public void dropIn(View view){ // Click Function
        isTie = true;
        mplayerButtonEffect.start();

        ImageView counter = (ImageView) view;
        int tappedCounter = Integer.parseInt(counter.getTag().toString());
        if (board[tappedCounter] == 2 && gameIsActive) {
            board[tappedCounter] = player;
            //counter.setTranslationY(-1000f); // flavor motion
            if (player == 0) {
                counter.setImageResource(R.drawable.cross);
                bestMoveHumFirst();
            }  else {
                counter.setImageResource(R.drawable.circle);
                bestMoveAIFirst();
            }

            //counter.animate().translationYBy(1000f).rotation(720f).setDuration(300);
            for (int[] winningPosition : winningPositions) {
                if (board[winningPosition[0]] == board[winningPosition[1]] &&
                        board[winningPosition[1]] == board[winningPosition[2]] &&
                        board[winningPosition[0]] != 2) {
                    gameIsActive = false;
                    musicChange = true;
                    String winner = "O";
                    if(board[winningPosition[0]] == 1 && player == 0 ||
                            board[winningPosition[0]] == 0 && player ==1) {
                        // winner = "O", Human First
                        winner = "AI";
                        // we no longer need to cast since findViewById returns T (it used to return View)
                        TextView ai = findViewById(R.id.ai);
                        sharedPreferences = getSharedPreferences("TicTacToe", Context.MODE_PRIVATE);
                        int AIScore = sharedPreferences.getInt("AI", 0);
                        AIScore+=1;
                        //String AIScoreStr = String.valueOf(AIScore);
                        String AIText = "AI: " + AIScore;
                        // this syntax below is not good, so I used string AIText
                        //ai.setText("AI: " + AIScore);
                        ai.setText(AIText);

                        editor=sharedPreferences.edit();
                        editor.putInt("AI",AIScore);
                        editor.apply();
                    }
                    else if(board[winningPosition[0]] == 1 && player == 1 ||
                                board[winningPosition[0]] == 0 && player ==0) {
                        // winner = "O", AI first
                        winner = "Human";
                        TextView human = findViewById(R.id.human);
                        sharedPreferences = getSharedPreferences("TicTacToe", Context.MODE_PRIVATE);
                        int humanScore = sharedPreferences.getInt("Human", 0);
                        humanScore+=1;
                        String humanScoreText = "Human: " + humanScore;
                        human.setText(humanScoreText);

                        editor = sharedPreferences.edit();
                        editor.putInt("Human",humanScore);
                        editor.apply();

                    }
                    TextView winnerMessage = findViewById(R.id.winnerMessage);
                    String winnerMessageText = winner + " has won!";
                    winnerMessage.setText(winnerMessageText);
                    isTie = false;
                    LinearLayout layout = findViewById(R.id.playAgainLayout);
                    // message will be faded in
                    layout.setAlpha(0.0f);
                    layout.setVisibility(View.VISIBLE);
                    layout.animate().alpha(1.0f).setDuration(1200);

                } else {
                    boolean gameIsOver = true;

                    for (int counterState : board) {
                        if (counterState == 2) {
                            isTie = false;
                            gameIsOver = false;
                            break;
                        }
                    }
                    if (gameIsOver) {
                        gameIsActive = false;
                        musicChange = true;
                        TextView winnerMessage = findViewById(R.id.winnerMessage);
                        String itsADraw = "It's a draw";
                        winnerMessage.setText(itsADraw);

                        for (int[] winning : winningPositions){
                            if (board[winning[0]] == board[winning[1]] &&
                                    board[winning[1]] == board[winning[2]] &&
                                    board[winning[0]] != 2) {
                                if(board[winning[0]] == 0){ // "X" won
                                    isTie = false;
                                    // if Human first, winner = "Human"; otherwise, "AI"
                                    String winner = (player==0) ? "Human" : "AI";
                                    winnerMessage = findViewById(R.id.winnerMessage);
                                    String winnerMessageText = winner + " has won!";
                                    //winnerMessage.setText(winner + " has won!");
                                    winnerMessage.setText(winnerMessageText);
                                }else if(board[winning[0]] == 1){ // "O" won
                                    isTie = false;
                                    // if AI first, winner = "Human"; otherwise, "AI"
                                    String winner = (player==1) ? "Human" : "AI";
                                    winnerMessage = findViewById(R.id.winnerMessage);
                                    String winnerMessageText = winner + " has won!";
                                    //winnerMessage.setText(winner + " has won!");
                                    winnerMessage.setText(winnerMessageText);
                                }
                            }
                        }

                        LinearLayout layout = findViewById(R.id.playAgainLayout);
                        // play again message will be faded in
                        layout.setAlpha(0.0f);
                        layout.setVisibility(View.VISIBLE);
                        layout.animate().alpha(1.0f).setDuration(1200);
                    }
                }
            }
            if(isTie){
                TextView tie = findViewById(R.id.tie);
                sharedPreferences = getSharedPreferences("TicTacToe", Context.MODE_PRIVATE);
                int tieScore = sharedPreferences.getInt("Tie", 0);
                tieScore+=1;
                String tieScoreText = "Tie: " + tieScore;
                //tie.setText("Tie: "+ tieScore);
                tie.setText(tieScoreText);

                editor=sharedPreferences.edit();
                editor.putInt("Tie",tieScore);
                editor.apply();

            }
        }
        if(musicChange){
            if(mplayer2IsPlaying) {
                mplayer2.pause();
                mplayer2.release();
                mplayer2 = null;
                mplayer2IsPlaying = false;
            }
            if(!mplayer01IsPlaying){
                mplayer01 = MediaPlayer.create(this, R.raw.music1);
                mplayer01.setLooping(true);
                mplayer01.start();
                mplayer01IsPlaying = true;
            }
            count = 2;
        }
        // checking
//        for(int k=0; k<9; k++) {
//            System.out.println("board ["+k+"]: "+board[k]);
//        }
    }

    public void playAgain(View view){
        mplayerButtonPlayAgain.start();

        isTie = true;
        gameIsActive = false;
        musicChange = false;

        LinearLayout layout = findViewById(R.id.playAgainLayout);
        layout.setVisibility(View.INVISIBLE);
        player = 0;
        Arrays.fill(board, 2);
        GridLayout gridLayout = findViewById(R.id.gridLayout);
        for(int i = 0; i < gridLayout.getChildCount(); i++){
            ((ImageView) gridLayout.getChildAt(i)).setImageResource(0);
        }
        LinearLayout selectLayout = findViewById(R.id.selectLayout);
        selectLayout.setVisibility(View.VISIBLE);
    }

    public void bestMoveHumFirst() {
        int bestScore = Integer.MIN_VALUE;
        int move = 0;
        for (int i = 0; i < 9; i++) {
            if (board[i] == 2) {
                board[i] = 1; // 'O'
                int score = minimax(board, 8, false);
                board[i] = 2; // empty
                if (score > bestScore) {
                    bestScore = score;
                    move = i;
                }
//                System.out.println("Move: "+move);
            }
        }
        ImageView img = new ImageView(this);

        if(board[move]==2 && gameIsActive) {
            setImage(move,img).setImageResource(R.drawable.circle);
//            System.out.println("move: "+move);
            board[move] = 1;
        }
    }

    public void bestMoveAIFirst() {
        int bestScore = Integer.MIN_VALUE;
        int move = 0;
        for (int i = 0; i < 9; i++) {
            if (board[i] == 2) {
                board[i] = 0; // 'X'
                int score = minimax(board, 7,false);
                board[i] = 2; // empty
                if (score > bestScore) {
                    bestScore = score;
                    move = i;
                }
            }
        }
        ImageView img = new ImageView(this);

        if(board[move] == 2) {
            setImage(move,img).setImageResource(R.drawable.cross);
            board[move] = 0;
        }
    }

    public int minimax(int[] board, int depth, boolean isMaximising){
        for (int[] winningPosition : winningPositions) {
            if(board[winningPosition[0]] == board[winningPosition[1]] &&
                    board[winningPosition[1]] == board[winningPosition[2]] &&
                    board[winningPosition[0]] == 0 && player == 0){
                return -10 * depth;
            } else if(board[winningPosition[0]] == board[winningPosition[1]] &&
                    board[winningPosition[1]] == board[winningPosition[2]] &&
                    board[winningPosition[0]] == 0 && player == 1) {
                return 10 * depth;
            }
            if(board[winningPosition[0]] == board[winningPosition[1]] &&
                    board[winningPosition[1]] == board[winningPosition[2]] &&
                    board[winningPosition[0]] == 1 && player == 0){
                return 10 * depth;
            } else if(board[winningPosition[0]] == board[winningPosition[1]] &&
                    board[winningPosition[1]] == board[winningPosition[2]] &&
                    board[winningPosition[0]] == 1 && player == 1){
                return -10 * depth;
            }
        }
        if(board[0] != 2 && board[1] != 2 && board[2] != 2 &&board[3] != 2 && board[4] != 2 &&
                board[5] != 2 && board[6] != 2 && board[7] != 2 && board[8] != 2){
            return 0;
        }

        if(isMaximising){ // Player's turn
            int maxScore = Integer.MIN_VALUE;
            for(int i=0; i < 9; i++){
                if(board[i] == 2){
                    if(player == 1){ // if AI First
                        board[i] = 0; // 'X'
                    }else{ // if Human First
                        board[i] = 1; // 'O'
                    }
                    int score = minimax(board, depth-1, false);
                    board[i] = 2;
                    if (score > maxScore) {
                        maxScore = score;
                    }
                }
            }
            return maxScore;
        } else{ // AI's turn
            int minScore = Integer.MAX_VALUE;
            for(int i=0; i < 9; i++){
                if(board[i] == 2){
                    if(player == 0){ // if Human First
                        board[i] = 0; // 'X'
                    }else{ // if AI First
                        board[i] = 1; // 'O'
                    }
                    int score = minimax(board, depth-1, true);
                    board[i] = 2;
                    if (score < minScore) {
                        minScore = score;
                    }
                }
            }
            return minScore;
        }
    }

    public void nightMode(View view){
        sharedPreferences = getSharedPreferences("TicTacToe", Context.MODE_PRIVATE);
        boolean isNightMode = sharedPreferences.getBoolean("NightMode",false);
        if(isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            editor = sharedPreferences.edit();
            editor.putBoolean("NightMode",false);
            editor.apply();
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            editor = sharedPreferences.edit();
            editor.putBoolean("NightMode",true);
            editor.apply();
        }
        if(mplayer1IsPlaying) {
            mplayer1.stop();
            mplayer1.release();
            mplayer1 = null;
            mplayer1IsPlaying = false;
        }
        if(mplayer01IsPlaying) {
            mplayer01.stop();
            mplayer01.release();
            mplayer01 = null;
            mplayer01IsPlaying = false;
        }
        if(mplayer2IsPlaying){
            mplayer2.stop();
            mplayer2.release();
            mplayer2 = null;
            mplayer2IsPlaying = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mplayerButtonMenu = MediaPlayer.create(this, R.raw.buttonmenu);
        mplayerButtonEffect = MediaPlayer.create(this, R.raw.buttoneffect);
        mplayerButtonPlayAgain = MediaPlayer.create(this, R.raw.buttonplayagain);
        mplayer1 = MediaPlayer.create(this, R.raw.music1);
        mplayer1.setLooping(true);
        mplayer1.start();
        mplayer1IsPlaying = true;

        count = 1;

        sharedPreferences = getSharedPreferences("TicTacToe", Context.MODE_PRIVATE);
        boolean isNightMode = sharedPreferences.getBoolean("NightMode",false);
        if(isNightMode) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        editor = sharedPreferences.edit();
        editor.putBoolean("NightMode",isNightMode);
        editor.apply();

        TextView tie = findViewById(R.id.tie);
        int tieScore = sharedPreferences.getInt("Tie", 0);
        String tieScoreText = "Tie: " + tieScore;
        tie.setText(tieScoreText);
        editor=sharedPreferences.edit();
        editor.putInt("Tie",tieScore);
        editor.apply();

        TextView human = findViewById(R.id.human);
        int humanScore = sharedPreferences.getInt("Human", 0);
        String humanScoreText = "Human: " + humanScore;
        human.setText(humanScoreText);
        editor=sharedPreferences.edit();
        editor.putInt("Human",humanScore);
        editor.apply();

        TextView ai = findViewById(R.id.ai);
        int AIScore = sharedPreferences.getInt("AI", 0);
        String AIScoreText = "AI: " + AIScore;
        ai.setText(AIScoreText);
        editor=sharedPreferences.edit();
        editor.putInt("AI",AIScore);
        editor.apply();
    }
}