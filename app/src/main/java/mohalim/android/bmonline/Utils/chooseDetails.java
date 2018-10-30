package mohalim.android.bmonline.Utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import mohalim.android.bmonline.MainActivity;
import mohalim.android.bmonline.R;

public class chooseDetails extends Dialog implements View.OnClickListener {
    Button dateFrom, dateTo, showDetails;
    OnClickShowDetails onClickShowDetails;
    FragmentManager fm;



    public chooseDetails(@NonNull Context context, OnClickShowDetails listener) {
        super(context);
        onClickShowDetails = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_details);
        dateFrom = findViewById(R.id.date_from);
        dateTo = findViewById(R.id.date_to);

        showDetails = findViewById(R.id.show_details);

        dateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.setType(datePickerFragment.DATE_FROM);
                datePickerFragment.show(fm,"fm");
            }
        });

        dateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.setType(datePickerFragment.DATE_TO);
                datePickerFragment.show(fm,"fm");
            }
        });

        showDetails.setOnClickListener(this);

    }

    public void setFm(FragmentManager fm) {
        this.fm = fm;
    }

    @Override
    public void onClick(View v) {
        onClickShowDetails.onClickShowDetails();
    }

    public interface OnClickShowDetails{
        void onClickShowDetails();
    }

}
