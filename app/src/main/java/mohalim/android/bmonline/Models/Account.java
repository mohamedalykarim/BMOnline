package mohalim.android.bmonline.Models;

public class Account {
    String accountNumber, currency, detail, balance;

    public Account() {
    }

    public Account(String accountNumber, String currency, String detail, String balance) {
        this.accountNumber = accountNumber;
        this.currency = currency;
        this.detail = detail;
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
