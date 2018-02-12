package kuririnz.xyz.bookdiscovery;

import java.util.List;

/**
 * 蔵書詳細画面のモデルクラス
 */

public class DetailDataModel {

    public List<item> items;

    public class item {
        public VolumeInfo volumeInfo;
    }

    public class VolumeInfo {
        public String title;
        public String subTitle;
        public List<String> authors;
        public List<Identifiers> industryIdentifiers;
        public String publishedDate;
        public String description;
        public int pageCount;
        public List<ImageLinks> imageLinks;
    }

    public class Identifiers {
        public String type;
        public String identifier;
    }

    public class ImageLinks {
        public String smallThumbnail;
        public String thumbnail;
    }
}
