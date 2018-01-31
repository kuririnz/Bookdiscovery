package kuririnz.xyz.bookdiscovery;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResultListActivity extends AppCompatActivity {

    // xmlファイルのコンポーネントと関連付ける要素
    ListView resultListView;
    // 検証用コレクションデータ
    List<String> listData = Arrays.asList("Android アプリ開発の環境構築"
            , "Android OS とは"
            , "Androidの概念"
            , "Androidアプリ開発を始める"
            , "検索画面レイアウト作成"
            , "ボタンイベントの実装"
            , "検索結果画面への遷移実装"
            , "非同期処理、REST API通信の実装"
            , "検索履歴機能"
            , "Firebase導入");
    // ListViewの表示内容を管理するクラス
    ResultListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_list);

        // xmlファイルのコンポーネントと関連付け
        resultListView = findViewById(R.id.ResultListView);
        // ListViewに表示する情報をまとめるAdapterをインスタンス化
        adapter = new ResultListAdapter(ResultListActivity.this, listData);
        // ListViewに表示情報をまとめたAdapterをセット
        resultListView.setAdapter(adapter);

        // ListViewの各行をクリックした時の命令を実装
        AdapterView.OnItemClickListener listItemClickEvent = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // クリックした行番号をToastで表示する
                Toast.makeText(ResultListActivity.this
                        , (i + 1) + "行目をクリックしました"
                        , Toast.LENGTH_SHORT).show();
            }
        };
        // ListViewに行をクリックした時のイベントを登録
        resultListView.setOnItemClickListener(listItemClickEvent);
    }
}
