package in.gtux.tictactoe;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class GameActivity extends AppCompatActivity {

    public TicTacToe game;
    protected AlertDialog dialog;
    protected int[] btnlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        init_stuff();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void init_stuff() {
        game = new TicTacToe();
        dialog = new AlertDialog.Builder(this).create();
        btnlist = new int[] {
                R.id.pos00, R.id.pos01, R.id.pos02,
                R.id.pos10, R.id.pos11, R.id.pos12,
                R.id.pos20, R.id.pos21, R.id.pos22,
        };
    }

    public void startNewGame(View view) {
        init_stuff();
        finish();
        startActivity(getIntent());
    }

    public void lockBoard() {
        for(int id : this.btnlist) {
            findViewById(id).setClickable(false);
        }
    }

    public void markMove(View view) {
        Button curbtn = (Button) findViewById(view.getId());
        curbtn.setClickable(false);

        String pos = view.getTag().toString();
        int x = Character.getNumericValue(pos.charAt(3));
        int y = Character.getNumericValue(pos.charAt(4));

        if (this.game.getCurrentPlayer() == TicTacToe.EX)
            curbtn.setText("X");
        else
            curbtn.setText("O");

        int state = this.game.makeMove(x, y);

        if (state == TicTacToe.CONT)
            return;

        if (state == TicTacToe.DRAW) {
            dialog.setTitle("Game Draw!");
            dialog.setMessage("Draw!!");
            dialog.show();
            return;
        }

        char winner = game.getWinner() == TicTacToe.EX ? 'X' : 'O';
        lockBoard();
        dialog.setTitle("Winner!");
        dialog.setMessage("Player " + winner + " wins!!");
        dialog.show();
    }
}

class TicTacToe {
    public static final int EX = 1;
    public static final int OH = -1;

    //States
    public static final int CONT = 1;
    public static final int DRAW = 0;
    public static final int WIN_COL = 2;
    public static final int WIN_CROSS = 3;
    public static final int WIN_ACROSS = 4;
    public static final int WIN_ROW = 5;


    protected int[][] board;
    protected int lastx, lasty;
    protected int move_count;
    private int current_player;
    private int state;
    protected boolean isopen;

    public TicTacToe() {
        board = new int[][] {
                {0, 0, 0},
                {0, 0, 0},
                {0, 0, 0}
        };

        current_player = EX;
        move_count = 0;
        isopen = true;
        state = CONT;
    }

    public int makeMove(int x, int y) {
        //Check for state
        if(!this.isopen) {
            throw new GameOverException("Game Over");
        }

        //Check if the place is already marked
        if(this.board[x][y] != 0) {
            throw new AlreadyMarkedException(String.format("Already marked at %d, %d", x, y));
        }

        this.board[x][y] = this.current_player;
        this.current_player *= -1; // this.togglePlayer();
        System.out.println("Player: " + current_player);
        this.lastx = x;
        this.lasty = y;

        this.move_count++;

        int state = boardState();
        this.state = state;
        this.updateState(state);

        return state;
    }

    protected void updateState(int state) {
        this.isopen = !(state != CONT);
    }

    protected int boardState() {
        int player = this.togglePlayer();
        int i, j;
        boolean rowfail = false, colfail = false, crossfail = false, acrossfail = false;

        //Row-wise check
        for(i = 0 ; i < 3 ; i++) {
            if(this.board[lastx][i] != player)
                rowfail = true;
            if(this.board[i][lasty] != player)
                colfail = true;
        }
        if(!rowfail)
            return WIN_ROW;
        if(!colfail)
            return WIN_COL;

        //Do we need to check criss cross pattern
        if((lastx == 1 && lasty == 1) || (lastx % 2 == 0 && lasty %2 == 0)) {
            for(i = 0, j = 2 ; i < 3 ; i++, j--) {
                if(this.board[i][i] != player)
                    crossfail = true;
                if(this.board[i][j] != player)
                    acrossfail = true;
            }

            if(!crossfail)
                return WIN_CROSS;
            else if(!acrossfail)
                return WIN_ACROSS;
        }

        //Check if there are no moves available
        if(this.move_count == 9)
            return DRAW;
        else
            return CONT ;
    }

    protected int togglePlayer() {
        return this.current_player * -1;
    }

    public int getCurrentPlayer() {
        return this.current_player;
    }

    public int getWinner() {
        if(this.state >= 2)
            return this.getCurrentPlayer() * -1;
        else
            return 0;
    }

    public void printBoard() {
        char marker;
        for(int i = 0 ; i < 3 ; i++) {
            for(int j = 0 ; j < 3 ; j++) {
                if(this.board[i][j] == EX)
                    marker = 'X';
                else if(this.board[i][j]  == OH)
                    marker = 'O';
                else
                    marker = ' ';
                System.out.print(marker + " ");
            }
            System.out.println();
        }
        System.out.println("\n\n");
    }
}


class AlreadyMarkedException extends RuntimeException {
    public AlreadyMarkedException(String message) {
        super(message);
    }
}

class GameOverException extends RuntimeException {
    public GameOverException(String message) {
        super(message);
    }
}
