package mohalim.android.bmonline.Utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Window;
import android.widget.TextView;

import mohalim.android.bmonline.R;

public class LoadingDialog extends Dialog {
    TextView messageTV;

    public LoadingDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.loading_dialog);
        messageTV = findViewById(R.id.loading_message_tv);
        messageTV.setText("جاري التحميل...");

    }


    public void setMessageTV(String message) {
        messageTV.setText(message);
    }
}
