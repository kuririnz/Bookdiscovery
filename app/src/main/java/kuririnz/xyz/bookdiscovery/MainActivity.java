package kuririnz.xyz.bookdiscovery;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    // レイアウトxmlと関連付けるWidget
    Button bookSearchBtn;
    EditText bookSearchEditor;
    // Timerクラス
    Timer timer;
    // メインスレッドに帰って来るためのハンドラー
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ハンドラーオブジェクトをMainThreadでインスタンス化
        handler = new Handler();
        // 蔵書検索ボタンをjavaプログラムで操作できるように名前をつける
        bookSearchBtn = findViewById(R.id.BookSearchBtn);
        // 蔵書検索する文字を入力するEditTextをjavaプログラムで操作できるように名前をつける
        bookSearchEditor = findViewById(R.id.BookSearchEdit);
        // 蔵書検索ボタンが押された時の処理を実装
        View.OnClickListener bookSearchEvent = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // コンソールログにボタンが押されたことを出力(表示)
                Log.d("BookSearchBtn", "onClick: BookSearch Button");
                // 入力された文字をToast(トースト)に表示
                Toast.makeText(getBaseContext()
                        , "入力された文字は [" + bookSearchEditor.getText().toString() + "]です。"
                        , Toast.LENGTH_LONG).show();
                // Timerスレッドを止める
                timer.cancel();
                // 画面遷移するためのIntentをインスタンス化
                Intent intent = new Intent(MainActivity.this, ResultListActivity.class);
                // EditTextに入力された文字列を"KeyValuePair"でResultListActivityに渡す
                intent.putExtra("terms", bookSearchEditor.getText().toString());
                // 画面遷移アクションを実行
                startActivity(intent);
            }
        };
        // 蔵書検索ボタンが押された時に実行するプログラムをボタンに登録
        bookSearchBtn.setOnClickListener(bookSearchEvent);

        // 準備されているTimerスレッドをインスタンス化
        timer = new Timer();
        // ３秒ごとに実行するタスク(TimerTask)をインスタンス化
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                // コンソールログに更新された内容を出力
                Log.d("SubThread Process"
                        , "「" +  bookSearchEditor.getText().toString() + "」に更新されました。");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // 更新された内容をトーストに表示
                        Toast.makeText(getBaseContext()
                                , "「" +  bookSearchEditor.getText().toString() + "」に更新されました。"
                                , Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        // Timerスレッドの実行スケジュールを設定
        // 3秒毎にtimerTaskのプログラムを実行
        timer.schedule(timerTask, 3000, 3000);
    }
}
