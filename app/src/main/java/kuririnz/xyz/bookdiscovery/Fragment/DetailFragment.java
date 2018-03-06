package kuririnz.xyz.bookdiscovery.Fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import java.io.IOException;

import kuririnz.xyz.bookdiscovery.Model.DetailDataModel;
import kuririnz.xyz.bookdiscovery.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment implements View.OnClickListener {

    // 定数
    // データ渡しのキー情報
    private final static String BUNDLE_KEY = "BUNDLE_SELFLINK";

    // xmlファイルのコンポーネントと関連付ける要素
    private TextView titleText;
    private TextView subTitleText;
    private TextView authorText;
    private TextView descriptText;
    private TextView pageText;
    private TextView publishDateText;
    private ImageView detailImage;
    private Button transWebviewBtn;
    // Play Store リンクURL
    private String infoLink;
    // 個体リンクのURL
    private String selfLink;
    // APIのデータ取得後処理を行うためのHandler
    private Handler handler;
    // OkHttp通信クライアント
    private OkHttpClient okHttpClient;

    // スタティックコンストラクタ
    public static DetailFragment getInstance(String selfLink) {
        // DetailFragmentインスタンスを生成
        DetailFragment fragment = new DetailFragment();
        // DetailFragmentに渡すデータ格納クラスを生成
        Bundle args = new Bundle();
        // 検索文字列データを連携データにセット
        args.putString(BUNDLE_KEY, selfLink);
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
            // 連携データ内から"BUNDLE_SELFLINK"キーのデータを代入、なければ"Android"と文字列を代入
            selfLink = getArguments().getString(BUNDLE_KEY, "");
        }

        // selfLinkが空の場合は検索結果一覧画面に強制バック
        if (TextUtils.isEmpty(selfLink)) {
            getFragmentManager().popBackStack();
        }

        // xmlファイルのコンポーネントと関連付け
        titleText = getView().findViewById(R.id.DetailTitle);
        subTitleText = getView().findViewById(R.id.DetailSubTitle);
        authorText = getView().findViewById(R.id.DetailAuthor);
        descriptText = getView().findViewById(R.id.DetailDescription);
        pageText = getView().findViewById(R.id.DetailPageText);
        publishDateText = getView().findViewById(R.id.DetailPublishDateText);
        detailImage = getView().findViewById(R.id.DetailImage);
        transWebviewBtn = getView().findViewById(R.id.TransitionWebView);

        // WebViewFragmentへの遷移処理を実装
        transWebviewBtn.setOnClickListener(this);

        // OkHttp通信クライアントをインスタンス化
        okHttpClient = new OkHttpClient();
        // 通信するための情報
        // ResultListFragmentから取得したselfLinkURLにREST API通信を行う
        Request request = new Request.Builder().url(selfLink).build();
        // データの取得後の命令を実装
        Callback callBack = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 通信に失敗した原因をログに出力
                Log.e("failure API Response", e.getLocalizedMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // JsonパースライブラリGsonのインスタンス化
                Gson gson = new Gson();
                // 返却されたJson文字列を一旦変数に代入
                String jsonString = response.body().string();
                // DetailDataModelクラスに代入
                DetailDataModel detailData = gson.fromJson(jsonString, DetailDataModel.class);
                // Play Store へのリンクを代入
                infoLink = detailData.volumeInfo.infoLink;
                // パースが正常に行えたかLogcatに出力して確認。
                Log.d("DetailFragment parse", detailData.volumeInfo.title);
                // MainThreadに処理を渡し画面にデータを反映する
                handler.post(new ReflectDetail(detailData));
            }
        };
        // 非同期処理でREST API通信を実行
        okHttpClient.newCall(request).enqueue(callBack);
    }

    // ボタンクリック時のイベントを実装
    @Override
    public void onClick(View view) {
        // クリックされたボタンをIDで判定
        if (view.getId() == R.id.TransitionWebView) {
            // "WebViewで確認"ボタンをクリックした場合
            // BTWebViewFragmentをインスタンス化
            BTWebViewFragment fragment = BTWebViewFragment.getInstance(infoLink);
            // 別のFragmentに遷移するためのクラスをインスタンス化
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            // 現在、DetailFragmentを表示しているR.id.FragmentContainerをBTWebViewFragmentに置き換え
            ft.replace(R.id.FragmentContainer, fragment);
            // 表示していたFragmentをバックスタックに追加
            ft.addToBackStack(null);
            // 変更を反映
            ft.commit();
        }
    }

    // REST APIで取得したデータを画面に反映するためのクラス
    private class ReflectDetail implements Runnable {
        // 蔵書詳細データ
        DetailDataModel detailData;

        // コンストラクタ
        public ReflectDetail(DetailDataModel detailData) {
            this.detailData = detailData;
        }

        // Handlerから実行されるメソッド
        @Override
        public void run() {
            // タイトルを反映
            titleText.setText(detailData.volumeInfo.title);
            // サブタイトルが取得できていたら反映
            if (!TextUtils.isEmpty(detailData.volumeInfo.subTitle)) {
                subTitleText.setText(detailData.volumeInfo.subTitle);
            }
            // 概要が取得できていたら反映
            if (!TextUtils.isEmpty(detailData.volumeInfo.description)) {
                descriptText.setText(detailData.volumeInfo.description);
            }
            // 著作者名が取得できていたら反映
            if (detailData.volumeInfo.authors != null && detailData.volumeInfo.authors.size() > 0) {
                String authorString = new String();
                // 著作者名が複数設定されていう場合があるので繰り返し処理で全て表示する
                for (String author : detailData.volumeInfo.authors) {
                    authorString += author + ",";
                }
                authorText.setText(authorString);
            }
            // ページ数を反映
            pageText.setText(String.valueOf(detailData.volumeInfo.pageCount));
            // 発売日が取得できていたら反映
            if (!TextUtils.isEmpty(detailData.volumeInfo.publishedDate)) {
                publishDateText.setText(detailData.volumeInfo.publishedDate);
            }
            // Glideを使ってWeb上の画像をImageViewに表示させる
            if (detailData.volumeInfo.imageLinks != null) {
                Glide.with(DetailFragment.this)
                        .applyDefaultRequestOptions(RequestOptions.fitCenterTransform())
                        .load(detailData.volumeInfo.imageLinks.medium)
                        .into(detailImage);
            }
        }
    }
}
