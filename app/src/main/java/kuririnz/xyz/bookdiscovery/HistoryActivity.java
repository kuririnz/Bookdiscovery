package kuririnz.xyz.bookdiscovery;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmResults;

public class HistoryActivity extends AppCompatActivity {

    // Realmインスタンスを宣言
    private Realm realm;
    // 画面紐付けコンポーネントを宣言
    private TextView historyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // 画面コンポーネント関連付け
        historyTextView = findViewById(R.id.HistoryText);
        // Realmクラスをインスタンス化
        realm = Realm.getDefaultInstance();
        // 検索履歴テーブルのデータを全て取得
        RealmResults<SearchHistoryModel> result = realm.where(SearchHistoryModel.class).findAll();
        // 検索履歴の件数が１件以上なら繰り返し処理でTextViewに表示する
        if (!result.isEmpty() && result.size() > 0) {
            // 検索履歴テーブルの行数分、繰り返し処理を実行する
            for (int i = 0; i < result.size(); i++) {
                // 検索履歴画面のtextViewに検索文字列を随時結合して表示する
                historyTextView.setText(historyTextView.getText() + result.get(i).getSearchDate() + result.get(i).getSearchTerm());
            }
        }
    }
}
