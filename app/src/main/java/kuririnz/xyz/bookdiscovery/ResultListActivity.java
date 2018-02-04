package kuririnz.xyz.bookdiscovery;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

public class ResultListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    // xmlファイルのコンポーネントと関連付ける要素
    ListView resultListView;
    // ListViewの表示内容を管理するクラス
    ResultListAdapter adapter;
    // OkHttp通信クライアント
    OkHttpClient okHttpClient;
    // メインスレッドに戻ってくるためのHandler
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_list);

        // Handlerをインスタンス化
        handler = new Handler();
        // xmlファイルのコンポーネントと関連付け
        resultListView = findViewById(R.id.ResultListView);

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
                    // メインスレッドで実行する処理をインスタンス化
                    ReflectResult reflectResult = new ReflectResult(items);
                    // Handlerにてメインスレッドに処理を戻し、ReflectResultのrunメソッドを実行する
                    handler.post(reflectResult);
                } catch (JSONException e) {
                    // Jsonパースの時にエラーが発生したらログに出力する
                    e.printStackTrace();
                }
            }
        };
        // 非同期処理でAPI通信を実行
        okHttpClient.newCall(request).enqueue(callBack);
    }

    // ListViewの各行をクリックした時の命令を実装
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        // クリックした行番号をToastで表示する
        Toast.makeText(ResultListActivity.this
                , (i + 1) + "行目をクリックしました"
                , Toast.LENGTH_SHORT).show();
    }

    // 検索結果をListViewに反映するメインスレッドの処理クラス
    class ReflectResult implements Runnable {
        // 蔵書一覧タイトルデータリスト
        List<String> titleList;
        // 蔵書一覧概要データリスト
        List<String> summaryList;

        // コンストラクタ
        public ReflectResult(JSONArray items) {
            // リストデータを初期化
            titleList = new ArrayList<>();
            summaryList = new ArrayList<>();
            // Jsonのパースエラーが発生した時に備えてtry~catchする
            try{
                // 蔵書リストの件数分繰り返しタイトルをログ出力する
                for (int i = 0; i < items.length(); i ++) {
                    // 蔵書リストから i番目のデータを取得
                    JSONObject item = items.getJSONObject(i);
                    // 蔵書のi番目データから蔵書情報のグループを取得
                    JSONObject volumeInfo = item.getJSONObject("volumeInfo");
                    // タイトルデータをリストに追加
                    titleList.add(volumeInfo.getString("title"));
                    // 概要データをリストに追加
                    summaryList.add(volumeInfo.getString("description"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Handlerから実行されるメソッド
        @Override
        public void run() {
            // ListViewに表示する情報をまとめるAdapterをインスタンス化
            adapter = new ResultListAdapter(ResultListActivity.this, titleList, summaryList);
            // ListViewに表示情報をまとめたAdapterをセット
            resultListView.setAdapter(adapter);
            // ListViewに行をクリックした時のイベントを登録
            resultListView.setOnItemClickListener(ResultListActivity.this);
        }
    }
}
