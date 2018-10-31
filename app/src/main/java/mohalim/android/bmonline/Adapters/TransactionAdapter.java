package mohalim.android.bmonline.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import mohalim.android.bmonline.Models.Transaction;
import mohalim.android.bmonline.R;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    List<Transaction> transactions = new ArrayList<>();

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.row_transaction_layout,viewGroup,false);

        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder transactionViewHolder, int i) {
        Transaction transaction = transactions.get(i);
        transactionViewHolder.description.setText(transaction.getDescription());
        transactionViewHolder.date.setText(transaction.getDate());
        transactionViewHolder.valueDate.setText(transaction.getValueDate());
        transactionViewHolder.debit.setText(transaction.getDebit());
        transactionViewHolder.credit.setText(transaction.getCredit());
        transactionViewHolder.balance.setText(transaction.getBalance());

    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public class TransactionViewHolder extends RecyclerView.ViewHolder{
        TextView description, date, valueDate, debit, credit, balance;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.description_tv);
            date = itemView.findViewById(R.id.date_tv);
            valueDate = itemView.findViewById(R.id.value_date_tv);
            debit = itemView.findViewById(R.id.debit_tv);
            credit = itemView.findViewById(R.id.credit_tv);
            balance = itemView.findViewById(R.id.balance_tv);

        }
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}
