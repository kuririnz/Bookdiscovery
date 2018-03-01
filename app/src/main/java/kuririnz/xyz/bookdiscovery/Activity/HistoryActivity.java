package kuririnz.xyz.bookdiscovery.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmResults;
import kuririnz.xyz.bookdiscovery.Adapter.HistoryRecyclerAdapter;
import kuririnz.xyz.bookdiscovery.R;
import kuririnz.xyz.bookdiscovery.Model.SearchHistoryModel;

public class HistoryActivity extends AppCompatActivity {

    // Realmインスタンスを宣言
    private Realm realm;
    // 履歴0件用TextView
    private TextView emptyRecyclerText;
    // 検索履歴RecyclerView
    private RecyclerView historyRecycler;
    // 検索履歴RecyclerAdapter
    private HistoryRecyclerAdapter historyAdapter;
    // 検索履歴抽出データ
    RealmResults<SearchHistoryModel> resultData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // 画面コンポーネント関連付け
        emptyRecyclerText = findViewById(R.id.EmptyRecyclerText);
        historyRecycler = findViewById(R.id.HistoryRecycler);
        // Realmクラスをインスタンス化
        realm = Realm.getDefaultInstance();
        // 検索履歴テーブルのデータを全て取得
        resultData = realm.where(SearchHistoryModel.class).findAll();
        // 検索履歴の件数が１件以上なら繰り返し処理でTextViewに表示する
        if (!resultData.isEmpty() && resultData.size() > 0) {
            // adapterクラスをインスタンス化
            historyAdapter = new HistoryRecyclerAdapter(this, resultData);
            // RecyclerViewの表示形式を決める
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            // RecyclerViewの初期設定
            historyRecycler.setAdapter(historyAdapter);
            historyRecycler.setLayoutManager(layoutManager);
        } else {
            // 検索履歴の件数が１件もない場合、履歴0件のメッセージを表示する
            // 検索履歴一覧を非表示に設定
            historyRecycler.setVisibility(View.GONE);
            emptyRecyclerText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Realmインスタンスをちゃんとクローズすること
        realm.close();
    }
}
