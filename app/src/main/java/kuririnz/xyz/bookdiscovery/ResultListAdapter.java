package kuririnz.xyz.bookdiscovery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by keisukekuribayashi on 2018/01/30.
 */

public class ResultListAdapter extends BaseAdapter {

    // ListViewの描画に必要な変数を宣言
    private List<String> titleList;
    private List<String> summaryList;
    private LayoutInflater layoutInflater;

    // コンストラクタ(インスタンス時に呼び出されるメソッドのようなもの)
    public ResultListAdapter(Context context, List<String> titleList, List<String> summaryList) {
        this.titleList = titleList;
        this.summaryList = summaryList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        // 一覧表示する要素数を返却する
        return titleList.size();
    }

    @Override
    public Object getItem(int i) {
        // indexやオブジェクト情報などを返却する
        // 一旦nullのまま
        return null;
    }

    @Override
    public long getItemId(int i) {
        // 行で表示しているLayoutIdやindex、特別なIDを返却する
        // 一旦nullのまま
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // 各行の表示レイアウト読み込みや、描画情報の設定を実装する
        // getViewで返却されたViewがListViewに表示される

        // viewの中身が空かチェック
        if (view == null) {
            // viewがレイアウトを読み込んでいない場合は"row_result_list"を読み込む
            view = layoutInflater.inflate(R.layout.row_result_list, viewGroup, false);
        }

        // row_result_listのTitleとSummaryに文言を代入
        TextView titleView = view.findViewById(R.id.RowListTitle);
        TextView summaryView = view.findViewById(R.id.RowListSummary);

        titleView.setText(titleList.get(i));
        summaryView.setText(summaryList.get(i));

        // 文字情報を代入されたviewを返却
        return view;
    }
}
