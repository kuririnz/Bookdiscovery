package kuririnz.xyz.bookdiscovery;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
    // OkHttp通信クライアント
    OkHttpClient okHttpClient;

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

        // OkHttp通信クライアントをインスタンス化
        okHttpClient = new OkHttpClient();
        // 通信するための情報
        Request request = new Request.Builder().url("https://www.googleapis.com/books/v1/volumes?q=ほんきで学ぶAndroidアプリ開発入門").build();
        // データの取得後の命令を実装
        Callback callBack = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 失敗した時の命令
                // 通信に失敗した原因をログに出力
                Log.e("failure API Response", e.getLocalizedMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 成功した時の命令
                // Google Books APIから取得したデータをログに出力
                // Jsonのパースが失敗してアプリの強制終了を回避する機能
                try {
                    // JsonデータをJSONObjectに変換
                    JSONObject rootJson = new JSONObject(response.body().string());
                    // Jsonデータから蔵書リストデータ"items"を取得
                    JSONArray items = rootJson.getJSONArray("items");
                    Log.d("Success API Response", "APIから取得したデータの件数:" +
                            items.length());
                    // 蔵書リストの件数分繰り返しタイトルをログ出力する
                    for (int i = 0; i < items.length(); i ++) {
                        // 蔵書リストから i番目のデータを取得
                        JSONObject item = items.getJSONObject(i);
                        // 蔵書のi番目データから蔵書情報のグループを取得
                        JSONObject volumeInfo = item.getJSONObject("volumeInfo");
                        // 繰り返しの番号と蔵書のタイトルをログに出力
                        Log.d("Response Item Title", (i + 1) + "番目のデータタイトル：" + volumeInfo.get("title"));
                    }
                } catch (JSONException e) {
                    // Jsonパースの時にエラーが発生したらログに出力する
                    e.printStackTrace();
                }
            }
        };
        // 非同期処理でAPI通信を実行
        okHttpClient.newCall(request).enqueue(callBack);
    }
}
