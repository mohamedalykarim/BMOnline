package mohalim.android.bmonline.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import mohalim.android.bmonline.Models.Account;
import mohalim.android.bmonline.R;

public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.AccountsViewHolder> {
    List<Account> accounts;

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

    class AccountsViewHolder extends RecyclerView.ViewHolder{
        TextView accountNumber, currency;
        Button balanceBtn, detailsBtn;

        public AccountsViewHolder(@NonNull View itemView) {
            super(itemView);
            accountNumber = itemView.findViewById(R.id.account_number_row);
            currency = itemView.findViewById(R.id.currency_row);
        }
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
}
