package kuririnz.xyz.bookdiscovery;


import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * 検索結果一覧画面 Fragment
 */
public class ResultListFragment extends Fragment implements AdapterView.OnItemClickListener{

    // xmlファイルのコンポーネントと関連付ける要素
    private ListView resultListView;
    // ListViewの表示内容を管理するクラス
    private ResultListAdapter adapter;
    // OkHttp通信クライアント
    private OkHttpClient okHttpClient;
    // メインスレッドに戻ってくるためのHandler
    private Handler handler;
    // MainActivityから渡されたデータを保持する
    private String term;

    // ファクトリーコンストラクタ
    public static ResultListFragment getInstance(String term) {
        // ResultListFragmentインスタンスを生成
        ResultListFragment fragment = new ResultListFragment();
        // ResultListFragmentに渡すデータ格納クラスを生成
        Bundle args = new Bundle();
        // 検索文字列データを連携データにセット
        args.putString("term", term);
        // データ格納クラスをResultListFragmentインスタンスにセット
        fragment.setArguments(args);
        // 生成したResultListFragmentを返却
        return fragment;
    }

    // コンストラクタ
    public ResultListFragment() {
        // Required empty public constructor
    }

    // Fragmentが表示するレイアウトを指定するライフサイクルメソッド
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_result_list, container, false);
    }

    // 親となるActivityが生成された後に実行されるライフサイクルメソッド
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Handlerをインスタンス化
        handler = new Handler();
        // 検索文字列変数を初期化
        term = "Android";
        // 連携データが存在するか確認
        if (getArguments() != null) {
            // 連携データ内から"term"キーのデータを代入、なければ"Android"と文字列を代入
            term = getArguments().getString("term", "Android");
        }

        // xmlファイルのコンポーネントと関連付け
        resultListView =  getView().findViewById(R.id.FragmentResultListView);
        // OkHttp通信クライアントをインスタンス化
        okHttpClient = new OkHttpClient();
        // 通信するための情報
        // MainActivityで入力された文字列で検索する様修正
        Request request = new Request.Builder().url("https://www.googleapis.com/books/v1/volumes?q=" + term).build();
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
        Toast.makeText(getContext()
                , (i + 1) + "行目をクリックしました"
                , Toast.LENGTH_SHORT).show();
    }

    // 検索結果をListViewに反映するメインスレッドの処理クラス
    private class ReflectResult implements Runnable {
        // 蔵書一覧タイトルデータリスト
        private List<String> titleList;
        // 蔵書一覧概要データリスト
        private List<String> summaryList;

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
            adapter = new ResultListAdapter(getContext(), titleList, summaryList);
            // ListViewに表示情報をまとめたAdapterをセット
            resultListView.setAdapter(adapter);
            // ListViewに行をクリックした時のイベントを登録
            resultListView.setOnItemClickListener(ResultListFragment.this);
        }
    }
}
