package kuririnz.xyz.bookdiscovery.Fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import kuririnz.xyz.bookdiscovery.R;

/**
 * 蔵書詳細情報のウェブページを表示するFragment
 */
public class BTWebViewFragment extends Fragment {

    // データ連携用定数
    public static final String BUNDLE_URL = "BUNDLE_URL";

    // バインドコンポーネント
    private WebView webview;
    // メンバ変数
    private String defaultUrl;

    // スタティックコンストラクタ
    public static BTWebViewFragment getInstance(String previewLink) {
        // BTWebViewFragmentインスタンスを生成
        BTWebViewFragment fragment = new BTWebViewFragment();
        // BTWebViewFragmentへデータを渡すためのBundleを初期化
        Bundle args = new Bundle();
        // Google Booksのウェブページリンクをデータ渡し
        args.putString(BUNDLE_URL, previewLink);
        // データ格納クラスをBTWebViewFragmentインスタンスにセット
        fragment.setArguments(args);
        // 生成したFragmentを返却
        return fragment;
    }

    public BTWebViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_btweb_view, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 遷移時の連携データを取得
        if (getArguments() != null) {
            // 遷移時に登録したKeyValueデータがない場合はGoogleページを表示
            defaultUrl = getArguments().getString(BUNDLE_URL, "https://www.google.co.jp/");
        }

        // レイアウトのコンポーネントをバインド
        webview = getView().findViewById(R.id.FragmentWebView);
        // 自身のWebViewで表示するためにWebViewClientをWebViewに設定
        webview.setWebViewClient(new WebViewClient());
        // URLの読み込み
        webview.loadUrl(defaultUrl);
    }
}
