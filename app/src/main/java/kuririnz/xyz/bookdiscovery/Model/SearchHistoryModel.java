package kuririnz.xyz.bookdiscovery.Model;

import io.realm.RealmObject;

/**
 * 検索履歴テーブル(RealmObject)
 */

public class SearchHistoryModel extends RealmObject {

    // 検索日時カラム
    private String searchDate;
    // 検索文字列カラム
    private String searchTerm;

    public String getSearchDate() {
        return searchDate;
    }

    public void setSearchDate(String searchDate) {
        this.searchDate = searchDate;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
}
