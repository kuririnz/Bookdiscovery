package kuririnz.xyz.bookdiscovery;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {

    // 定数
    // データ渡しのキー情報
    private final static String BUNDLE_KEY = "BUNDLE_ISBN";

    // xmlファイルのコンポーネントと関連付ける要素
    private TextView titleText;
    private TextView subTitleText;
    private TextView authorText;
    private TextView descriptText;
    private TextView pageText;
    private TextView publishDateText;
    // APIの検索に使うISBNコード
    private String isbn;
    // APIのデータ取得後処理を行うためのHandler
    private Handler handler;
    // OkHttp通信クライアント
    private OkHttpClient okHttpClient;

    // スタティックコンストラクタ
    public static DetailFragment getInstance(String isbn) {
        // DetailFragmentインスタンスを生成
        DetailFragment fragment = new DetailFragment();
        // DetailFragmentに渡すデータ格納クラスを生成
        Bundle args = new Bundle();
        // 検索文字列データを連携データにセット
        args.putString(BUNDLE_KEY, isbn);
        // データ格納クラスをDetailFragmentインスタンスにセット
        fragment.setArguments(args);
        // 生成したResultListFragmentを返却
        return fragment;
    }

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Handlerをインスタンス化
        handler = new Handler();
        // 連携データが存在するか確認
        if (getArguments() != null) {
            // 連携データ内から"term"キーのデータを代入、なければ"Android"と文字列を代入
            isbn = getArguments().getString(BUNDLE_KEY, "");
        }

        // isbnが空の場合は検索結果一覧画面に強制バック
        if (TextUtils.isEmpty(isbn)) {
            getFragmentManager().popBackStack();
        }

        // xmlファイルのコンポーネントと関連付け
        titleText = getView().findViewById(R.id.DetailTitle);
        subTitleText = getView().findViewById(R.id.DetailSubTitle);
        authorText = getView().findViewById(R.id.DetailAuthor);
        descriptText = getView().findViewById(R.id.DetailDescription);
        pageText = getView().findViewById(R.id.DetailPageText);
        publishDateText = getView().findViewById(R.id.DetailPublishDateText);

        // OkHttp通信クライアントをインスタンス化
        okHttpClient = new OkHttpClient();
        // 通信するための情報
        // ResultListFragmentから取得したISBNを検索条件に設定
        Request request = new Request.Builder().url("https://www.googleapis.com/books/v1/volumes?q=isbn" +  isbn).build();
        // データの取得後の命令を実装
        Callback callBack = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 通信に失敗した原因をログに出力
                Log.e("failure API Response", e.getLocalizedMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Gson gson = new Gson();

                DetailDataModel detailData = gson.fromJson(response.body().string(), DetailDataModel.class);
                Log.d("DetailFragment parse", detailData.items.get(0).volumeInfo.title);
            }
        };
        // 非同期処理でAPI通信を実行
        okHttpClient.newCall(request).enqueue(callBack);
    }
}
