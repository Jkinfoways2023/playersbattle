package com.tournaments.grindbattles.bottomsheet;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import com.tournaments.grindbattles.R;

@SuppressLint({"ValidFragment"})
public class BottomSheetPricePool extends BottomSheetDialogFragment {
    Button button;
    ImageView closeButton;
    LinearLayout mainLL;
    TextView matchIDTV;
    String matchTitle = "";
    String prizePool = "";
    TextView textView;

    public BottomSheetPricePool(String str, String str2) {
        this.prizePool = str2;
        this.matchTitle = str;
    }

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.bottom_sheet_prizepool, viewGroup, false);

        this.textView = (TextView) inflate.findViewById(R.id.prizePoolText);
        this.mainLL = (LinearLayout) inflate.findViewById(R.id.mainLL);
        this.closeButton = (ImageView) inflate.findViewById(R.id.closeBtn);
        this.matchIDTV = (TextView) inflate.findViewById(R.id.matchID);

        this.closeButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                BottomSheetPricePool.this.dismiss();
            }
        });

        this.matchIDTV.setText(this.matchTitle);
        if (this.prizePool != null){
            if (  android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
            {
                this.textView.setText(Html.fromHtml(this.prizePool ,Html.FROM_HTML_MODE_LEGACY));
            }
            else {
                this.textView.setText(Html.fromHtml(this.prizePool));
            }
        }


        return inflate;
    }
}
