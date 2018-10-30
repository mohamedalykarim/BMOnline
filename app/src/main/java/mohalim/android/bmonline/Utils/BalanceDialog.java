package mohalim.android.bmonline.Utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Window;
import android.widget.TextView;

import mohalim.android.bmonline.R;

public class BalanceDialog extends Dialog {
    TextView balanceTV, currencyTV, accountNumberTV;

    public BalanceDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.balance_dialog);
        balanceTV = findViewById(R.id.balance_tv);
        currencyTV = findViewById(R.id.currency_tv);
        accountNumberTV = findViewById(R.id.account_number_tv);

    }


    public void setItems(String balance, String currency, String account) {
        balanceTV.setText(balance);
        currencyTV.setText(currency);
        accountNumberTV.setText(account);
    }
}
