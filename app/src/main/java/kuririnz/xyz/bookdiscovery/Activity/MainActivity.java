package kuririnz.xyz.bookdiscovery.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import kuririnz.xyz.bookdiscovery.Model.SearchHistoryModel;
import kuririnz.xyz.bookdiscovery.R;

public class MainActivity extends AppCompatActivity {

    // 最後の検索文言を保持/取得するキー定数
    private final static String PREF_KEY = "LAST_TERM";

    // レイアウトxmlと関連付けるWidget
    private Button bookSearchBtn;
    private Button historyBtn;
    private EditText bookSearchEditor;
    // Timerクラス
    private Timer timer;
    // メインスレッドに帰って来るためのハンドラー
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ハンドラーオブジェクトをMainThreadでインスタンス化
        handler = new Handler();
        // 蔵書検索ボタンをjavaプログラムで操作できるように名前をつける
        bookSearchBtn = findViewById(R.id.BookSearchBtn);
        // 検索履歴ボタンを関連付ける
        historyBtn = findViewById(R.id.HistoryBtn);
        // 蔵書検索する文字を入力するEditTextをjavaプログラムで操作できるように名前をつける
        bookSearchEditor = findViewById(R.id.BookSearchEdit);
        // 蔵書検索ボタンをクリックした時の処理を実装
        View.OnClickListener bookSearchEvent = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // コンソールログにボタンが押されたことを出力(表示)
                Log.d("BookSearchBtn", "onClick: BookSearch Button");
                // EditTextの文字列を取得
                String termString = bookSearchEditor.getText().toString();
                // Timerスレッドを止める
                timer.cancel();
                // Realmインスタンスを生成
                Realm realm = Realm.getDefaultInstance();
                try {
                    // 検索履歴テーブルへのアクセスを開始
                    realm.beginTransaction();
                    // 新規検索履歴データを作成
                    SearchHistoryModel history = realm.createObject(SearchHistoryModel.class);
                    // 検索文字列カラムにデータを登録
                    history.setSearchTerm(termString);
                    // 現在時刻を文字列で取得する
                    Date now = new Date();
                    // 現在時刻を定まった形式で文字列に変換
                    String dateStr = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(now);
                    // 現在日時の文字列をカラムデータに登録
                    history.setSearchDate(dateStr);
                    // 検索履歴テーブルへのアクセスを終了
                    realm.commitTransaction();
                } finally {
                    // Realmインスタンスがちゃんとクローズされること
                    realm.close();
                }

                // SharedPreferenceに保存
                SharedPreferences.Editor editor = MainActivity.this.getPreferences(Context.MODE_PRIVATE).edit();
                editor.putString(PREF_KEY, termString).apply();

                // 検索結果画面へ遷移するためのIntentをインスタンス化
                Intent intent = new Intent(MainActivity.this, ResultListActivity.class);
                // EditTextに入力された文字列を"KeyValuePair"でResultListActivityに渡す
                intent.putExtra("terms", termString);
                // 画面遷移アクションを実行
                startActivity(intent);
            }
        };
        // 蔵書検索ボタンが押された時に実行するプログラムをボタンに登録
        bookSearchBtn.setOnClickListener(bookSearchEvent);

        // 検索履歴ボタンをクリックした時の処理を実装
        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 検索履歴画面へ遷移するためのIntentをインスタンス化
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                // 画面遷移アクションを実行
                startActivity(intent);
            }
        });

        // SharedPreferenceに最後の検索条件が残っていたら登録する
        // SharedPreferenceにデータがない場合は空文字を設定する
        String lastTerm = this.getPreferences(Context.MODE_PRIVATE).getString(PREF_KEY, "");
        bookSearchEditor.setText(lastTerm);

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
