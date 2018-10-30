package mohalim.android.bmonline.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import mohalim.android.bmonline.Models.Account;
import mohalim.android.bmonline.R;

import static android.content.ContentValues.TAG;

public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.AccountsViewHolder> {
    private List<Account> accounts;
    private AccountRecyclerClick accountRecyclerClick;

    public AccountsAdapter(AccountRecyclerClick listener) {
        accountRecyclerClick = listener;
    }

    @NonNull
    @Override
    public AccountsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.row_account_layout,viewGroup,false);
        return new AccountsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountsViewHolder accountsViewHolder, int i) {
        Account account = accounts.get(i);
        accountsViewHolder.accountNumber.setText(account.getAccountNumber());
        accountsViewHolder.currency.setText(account.getCurrency());
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    class AccountsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView accountNumber, currency;
        Button balanceBtn, detailsBtn;


        public AccountsViewHolder(@NonNull View itemView) {
            super(itemView);
            accountNumber = itemView.findViewById(R.id.account_number_row);
            currency = itemView.findViewById(R.id.currency_row);
            balanceBtn = itemView.findViewById(R.id.balance_btn);
            detailsBtn = itemView.findViewById(R.id.details_btn);

            balanceBtn.setOnClickListener(this);
            detailsBtn.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (v.getId() == R.id.balance_btn){
                accountRecyclerClick.onClick(accounts.get(position).getBalance(),1, position);
            }else if (v.getId() == R.id.details_btn){
                accountRecyclerClick.onClick(accounts.get(position).getDetail(),2, position);
            }
        }
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public interface AccountRecyclerClick{
        void onClick(String click, int type, int position);
    }
}
