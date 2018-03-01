package kuririnz.xyz.bookdiscovery.Fragment;


import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;

import kuririnz.xyz.bookdiscovery.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProgressDialogFragment extends DialogFragment {


    public ProgressDialogFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // ダイアログ表示するレイアウトを生成
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_progress_dialog, null, false);

        // アラートダイアログビルダーを使ってボタン付きのダイアログを生成
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton("キャンセル", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getActivity().finish();
                    }
                })
                .setCancelable(false);
        // 表示するダイアログを生成して返却
        return builder.create();
    }
}
