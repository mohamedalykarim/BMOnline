package mohalim.android.bmonline;


import android.content.DialogInterface;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import mohalim.android.bmonline.Adapters.AccountsAdapter;
import mohalim.android.bmonline.Adapters.TransactionAdapter;
import mohalim.android.bmonline.Models.Account;
import mohalim.android.bmonline.Models.Transaction;
import mohalim.android.bmonline.Utils.BalanceDialog;
import mohalim.android.bmonline.Utils.LoadingDialog;
import mohalim.android.bmonline.Utils.chooseDetails;

public class MainActivity extends AppCompatActivity
        implements AccountsAdapter.AccountRecyclerClick,
        chooseDetails.OnClickShowDetails {

    private static final String ADD_FROM_DATE_DAY = "document.getElementById(\"ddlDay\").selectedIndex = ";
    private static final String ADD_FROM_DATE_MONTH = "document.getElementById(\"ddlMonth\").selectedIndex = ";
    private static final String ADD_FROM_DATE_YEAR = "document.getElementById(\"ddlYear\").selectedIndex = ";

    private static final String ADD_TO_DATE_DAY = "document.getElementById(\"ddlDayT\").selectedIndex = ";
    private static final String ADD_TO_DATE_MONTH = "document.getElementById(\"ddlMonthT\").selectedIndex = ";
    private static final String ADD_TO_DATE_YEAR = "document.getElementById(\"ddlYearT\").selectedIndex = ";

    private final String SUBMIT_DETAILS_FORM = "document.getElementById('btnStatment').click();";


    private static final String LOAD_NEXT_PAGE = "document.getElementById('LinkButtonNext').click();";
    private static final String LOAD_PREV_PAGE = "document.getElementById('LinkButtonPrev').click();";


    String usernameValue = "";
    String passwordValue = "";
    private final String BASE_URL= "https://www.banquemisr.com.eg/onlineservices/onlinebanking/";
    private final String LOGIN_URL= "https://www.banquemisr.com.eg/onlineservices/onlinebanking/Login.aspx";
    private final String CHANGE_USERNAME_VALUE_JS = "document.getElementById(\"txtId\").value = \"";
    private final String CHANGE_PASSWORD_VALUE_JS = "document.getElementById(\"txtpwd\").value = \"";
    private final String END_OF_VALUE= "\";";
    private final String SUBMIT_LOGIN_FORM = "document.getElementById('Button1').click();";
    private final String GET_HTML = "javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');";

    private final int LOGIN_STATUS = 1;
    private final int MAIN_PAGE_STATUS = 2;
    private final int BALANCE_STATUS = 3;
    private final int DETAILS_CHOOSE_STATUS = 4;
    private final int DETAILS_STATUS = 5;

    private int pageStatus = LOGIN_STATUS;

    public static chooseDetails chooseDetails;
    public static int dateFromDay = 0;
    public static int dateFromMonth = 0;
    public static int dateFromYear = 0;

    public static int dateToDay = 0;
    public static int dateToMonth = 0;
    public static int dateToYear = 0;

    ConstraintLayout loginConstraint,
            mainConstraint;

    LinearLayout detailsContainerConstraint;

    WebView myWebView;


    List<Account> accounts;
    LoadingDialog loadingDialog;
    ProgressBar loadingProgressBar;


    EditText usernameET, passwordET;
    Switch rememberSw;
    Button loginBtn, startArrow, endArrow;

    TransactionAdapter transactionAdapter;
    List<Transaction> transactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startArrow = findViewById(R.id.start_arrow);
        endArrow = findViewById(R.id.end_arrow);

        usernameET = findViewById(R.id.username_et);
        passwordET = findViewById(R.id.password_et);
        rememberSw = findViewById(R.id.remember_sw);
        loginBtn = findViewById(R.id.login);

        loginConstraint = findViewById(R.id.login_container);
        mainConstraint = findViewById(R.id.main_screen_container);
        detailsContainerConstraint = findViewById(R.id.detail_screen_container);

        loadingProgressBar = findViewById(R.id.login_progress_bar);

        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        loadingDialog.setMessageTV(getResources().getString(R.string.data_loading));



        makeLoginVisible();



        /**
         * Instintiate the web view
         */
        myWebView = (WebView) findViewById(R.id.bmonline_webview);
        myWebView.loadUrl(LOGIN_URL);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.getSettings().setDomStorageEnabled(true);

        /**
         * Do Login when finish
         */
        myWebView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(final WebView view, String url) {
                if (pageStatus == LOGIN_STATUS){
                    beforeLogin(view);
                }else if (pageStatus == BALANCE_STATUS){
                    getHtmlAfterBalance(view);
                }else if (pageStatus == DETAILS_CHOOSE_STATUS){
                    loadChooseDetails();
                }else if (pageStatus == DETAILS_STATUS){
                    getHtmlAfterDetailsLoading(view);
                }
            }
        });
        makeWebAlertAndroid();
    }


    /**
     * JavaScriptInterface to get Html From the Web View;
     */
    class LoadAccountListener{

        @JavascriptInterface
        public void processHTML(final String html)
        {
            if (pageStatus == LOGIN_STATUS){
                processHtmlAfterLogin(html);
            }else if (pageStatus == BALANCE_STATUS){
                processHtmlOnBalanceStatus(html);
            }else if (pageStatus == DETAILS_STATUS){
                processHtmlOnDetailsStatus(html);
            }


        }
    }




    @Override
    public void onClickShowDetails() {
        chooseDetails.dismiss();
        if (dateFromDay > 0){

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                myWebView.evaluateJavascript(
                        ADD_FROM_DATE_DAY + (dateFromDay - 1) + getResources().getString(R.string.simi),
                        null);
                myWebView.evaluateJavascript(
                        ADD_FROM_DATE_MONTH + dateFromMonth + getResources().getString(R.string.simi),
                        null);

                if (dateFromYear == 2017){
                    myWebView.evaluateJavascript(ADD_FROM_DATE_YEAR + 0 + getResources().getString(R.string.simi),
                            null);
                }else if (dateFromYear == 2018){
                    myWebView.evaluateJavascript(ADD_FROM_DATE_YEAR + 1 + getResources().getString(R.string.simi),
                            null);
                }

                myWebView.evaluateJavascript(ADD_TO_DATE_DAY + (dateToDay - 1) + getResources().getString(R.string.simi),
                        null);
                myWebView.evaluateJavascript(ADD_TO_DATE_MONTH + dateToMonth + getResources().getString(R.string.simi),
                        null);

                if (dateToYear == 2017){
                    myWebView.evaluateJavascript(ADD_TO_DATE_YEAR + 0 + getResources().getString(R.string.simi),
                            null);
                }else if (dateToYear == 2018){
                    myWebView.evaluateJavascript(ADD_TO_DATE_YEAR + 1 + getResources().getString(R.string.simi),
                            null);
                }

                loadingDialog.show();
                loadingDialog.setMessageTV(getResources().getString(R.string.data_loading));

                pageStatus = DETAILS_STATUS;
                myWebView.evaluateJavascript(SUBMIT_DETAILS_FORM, null);

            } else {
                myWebView.loadUrl(
                        getResources().getString(R.string.js_start)
                        + ADD_FROM_DATE_DAY + (dateFromDay - 1)
                                + getResources().getString(R.string.simi));
                myWebView.loadUrl(
                        getResources().getString(R.string.js_start)
                                + ADD_FROM_DATE_MONTH + dateFromMonth
                                + getResources().getString(R.string.simi));

                if (dateFromYear == 2017){
                    myWebView.loadUrl(
                            getResources().getString(R.string.js_start)
                                    + ADD_FROM_DATE_MONTH + 0
                                    + getResources().getString(R.string.simi));
                }else if (dateFromYear == 2018){
                    myWebView.loadUrl(
                            getResources().getString(R.string.js_start)
                                    + ADD_FROM_DATE_MONTH + 1
                                    + getResources().getString(R.string.simi));
                }

                myWebView.loadUrl(
                        getResources().getString(R.string.js_start)
                                + ADD_TO_DATE_DAY + (dateFromDay - 1)
                                + ";");
                myWebView.loadUrl(
                        getResources().getString(R.string.js_start)
                                + ADD_TO_DATE_MONTH + dateFromMonth
                                + getResources().getString(R.string.simi));

                if (dateToYear == 2017){
                    myWebView.loadUrl(
                            getResources().getString(R.string.js_start)
                                    + ADD_TO_DATE_YEAR + 0
                                    + getResources().getString(R.string.simi));
                }else if (dateToYear == 2018){
                    myWebView.loadUrl(
                            getResources().getString(R.string.js_start)
                                    + ADD_TO_DATE_YEAR + 1
                                    + getResources().getString(R.string.simi));
                }

                loadingDialog.show();
                loadingDialog.setMessageTV(getResources().getString(R.string.data_loading));

                pageStatus = DETAILS_STATUS;

                myWebView.loadUrl(
                        getResources().getString(R.string.js_start)
                                + SUBMIT_DETAILS_FORM);


            }






        }
    }


    @Override
    public void onClick(String click, int type, int position) {
        if (type == 1){
            pageStatus = BALANCE_STATUS;
            myWebView.loadUrl(BASE_URL + click);
            loadingDialog.show();
            loadingDialog.setMessageTV(getResources().getString(R.string.data_loading));


        }else if (type == 2){
            pageStatus = DETAILS_CHOOSE_STATUS;

            loadingDialog.show();
            loadingDialog.setMessageTV(getResources().getString(R.string.data_loading));
            myWebView.loadUrl(BASE_URL+accounts.get(position).getDetail());

        }
    }


    /**
     * Visiblity of screens
     */

    public void makeLoginVisible(){
        loginConstraint.setVisibility(View.VISIBLE);
        mainConstraint.setVisibility(View.INVISIBLE);
        detailsContainerConstraint.setVisibility(View.INVISIBLE);
    }

    public void makeMainVisible(){
        loginConstraint.setVisibility(View.INVISIBLE);
        mainConstraint.setVisibility(View.VISIBLE);
        detailsContainerConstraint.setVisibility(View.INVISIBLE);
    }

    public void makeDetailsVisible(){
        loginConstraint.setVisibility(View.INVISIBLE);
        mainConstraint.setVisibility(View.INVISIBLE);
        detailsContainerConstraint.setVisibility(View.VISIBLE);
    }



    private void processHtmlAfterLogin(String html) {

        if (html.contains("Accounts")){
            if (rememberSw.isActivated()){
                Toast.makeText(this, "remember", Toast.LENGTH_SHORT).show();
            }

            Document doc = Jsoup.parse(html);
            Elements accountsSpanElements = doc.select("#lblAccountsTable");
            Element accountsSpanElement = accountsSpanElements.first();
            Elements accountRows = accountsSpanElement.select("table tbody tr");
            accounts = new ArrayList<>();

            int ar = 0;
            for (Element accountRow: accountRows){
                Account account = new Account();
                if (ar !=0){
                    Elements accountDetailsRows = accountRow.select("td");

                    int adr = 0;

                    for (Element accountDetailsRow: accountDetailsRows){

                        if (adr == 0){
                            account.setAccountNumber(accountDetailsRow.text());
                        }else if (adr == 1){
                            account.setCurrency(accountDetailsRow.text());
                        }else if (adr == 2){
                            Element detailsUrlElement = accountDetailsRow.select("a").first();
                            String detailsUrl = detailsUrlElement.attr("href");
                            account.setDetail(detailsUrl);
                        }else if (adr == 3){
                            Element balanceUrlElement = accountDetailsRow.select("a").first();
                            String balanceUrl = balanceUrlElement.attr("href");
                            account.setBalance(balanceUrl);
                        }

                        adr++;
                    }

                    accounts.add(account);
                }
                ar++;
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadingProgressBar.setVisibility(View.INVISIBLE);
                    loginBtn.setEnabled(true);
                    makeMainVisible();

                    RecyclerView accountRecyclerView = findViewById(R.id.accounts_recycler_view);
                    AccountsAdapter accountsAdapter = new AccountsAdapter(MainActivity.this);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                    accountRecyclerView.setAdapter(accountsAdapter);
                    accountRecyclerView.setLayoutManager(layoutManager);


                    accountsAdapter.setAccounts(accounts);
                    accountsAdapter.notifyDataSetChanged();
                }
            });




            pageStatus = MAIN_PAGE_STATUS;

        }else{
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            loginBtn.setEnabled(true);
            alertDialog.setMessage(getResources().getString(R.string.wrong_credintial));
            alertDialog.show();
        }


    }

    private void processHtmlOnDetailsStatus(final String html) {
        Document doc = Jsoup.parse(html);
        Elements transactionsElements = doc.select("#lblTrxTable table tbody tr");
        transactions = new ArrayList<>();

        int t =0;
        for (Element transactionsElement : transactionsElements){

            if (t>1){
                Transaction transaction = new Transaction();

                Elements transactionDetailsElements = transactionsElement.select("td");
                int td = 0;
                for (Element transactionDetailsElement: transactionDetailsElements){
                    if (td == 0){
                        transaction.setDescription(transactionDetailsElement.text());
                    }else if (td==2){
                        transaction.setDate(transactionDetailsElement.text());
                    }else if (td==3){
                        transaction.setValueDate(transactionDetailsElement.text());
                    }else if (td==4){
                        transaction.setDebit(transactionDetailsElement.text());
                    }else if (td==5){
                        transaction.setCredit(transactionDetailsElement.text());
                    }else if (td==6){
                        transaction.setBalance(transactionDetailsElement.text());
                    }
                    td++;
                }

                transactions.add(transaction);
            }

            t++;
        }

        

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecyclerView transactionRecyclerView = findViewById(R.id.transaction_recycler);
                transactionAdapter = new TransactionAdapter();
                LinearLayoutManager tLayoutManager = new LinearLayoutManager(MainActivity.this);
                transactionRecyclerView.setAdapter(transactionAdapter);
                transactionRecyclerView.setLayoutManager(tLayoutManager);

                transactionAdapter.setTransactions(transactions);
                transactionAdapter.notifyDataSetChanged();

                loadingDialog.dismiss();
                makeDetailsVisible();

                if (html.contains("Next")){
                    endArrow.setVisibility(View.VISIBLE);


                    endArrow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pageStatus = DETAILS_STATUS;
                            transactions.clear();
                            transactionAdapter.notifyDataSetChanged();
                            loadingDialog.show();

                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                                myWebView.evaluateJavascript(LOAD_NEXT_PAGE, null);
                                getHtmlAfterDetailsLoading(myWebView);

                            } else {
                                myWebView.loadUrl(getResources().getString(R.string.js_start)+ LOAD_NEXT_PAGE );
                                getHtmlAfterDetailsLoading(myWebView);

                            }

                        }
                    });
                }else{
                    endArrow.setVisibility(View.INVISIBLE);
                }


                if (html.contains("Prev.")){
                    startArrow.setVisibility(View.VISIBLE);


                    startArrow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pageStatus = DETAILS_STATUS;
                            transactions.clear();
                            transactionAdapter.notifyDataSetChanged();
                            loadingDialog.show();

                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                                myWebView.evaluateJavascript(LOAD_PREV_PAGE, null);
                                getHtmlAfterDetailsLoading(myWebView);

                            } else {
                                myWebView.loadUrl(getResources().getString(R.string.js_start)+ LOAD_PREV_PAGE );
                                getHtmlAfterDetailsLoading(myWebView);

                            }

                        }
                    });
                }else{
                    startArrow.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void processHtmlOnBalanceStatus(String html) {
        Document doc = Jsoup.parse(html);
        Element balnceElement = doc.select(".BGTableRightCellCommon").first();
        Account account = new Account();

        Elements accountDetailsRows = balnceElement.select("td");
        int ab = 0;
        for (Element element: accountDetailsRows){
            if (ab == 1){
                int adr = 0;

                for (Element accountDetailsRow: accountDetailsRows){

                    if (adr == 0){
                        account.setAccountNumber(accountDetailsRow.text());
                    }else if (adr == 1){
                        account.setCurrency(accountDetailsRow.text());
                    }else if (adr == 2){
                        account.setBalance(accountDetailsRow.text());
                    }

                    adr++;
                }
            }
            ab++;
        }
        loadingDialog.dismiss();

        BalanceDialog balanceDialog = new BalanceDialog(MainActivity.this);
        balanceDialog.show();
        balanceDialog.setItems(
                account.getBalance(),
                account.getCurrency(),
                account.getAccountNumber()
        );

    }


    public void getHtmlAfterDetailsLoading(final WebView view){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.evaluateJavascript(GET_HTML, null);
                }
            }, 10000);



        } else {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.loadUrl(getResources().getString(R.string.js_start)+ GET_HTML);
                }
            }, 10000);

        }
    }

    private void getHtmlAfterBalance(final WebView view) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.evaluateJavascript(GET_HTML, null);
            }
        }, 5000);
    }



    private void beforeLogin(final WebView view) {
        loadingDialog.dismiss();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);

                usernameValue = usernameET.getText().toString();
                passwordValue = passwordET.getText().toString();

                //String usernameValue = "olb18715291";
                //String passwordValue = "Mm01147773369";


                view.addJavascriptInterface(new LoadAccountListener(), "HTMLOUT");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    view.evaluateJavascript(
                            CHANGE_USERNAME_VALUE_JS
                                    + usernameValue
                                    + END_OF_VALUE,
                            null);
                    view.evaluateJavascript(
                            CHANGE_PASSWORD_VALUE_JS
                                    + passwordValue
                                    + END_OF_VALUE,
                            null);
                    view.evaluateJavascript(
                            SUBMIT_LOGIN_FORM,
                            null);

                    loginBtn.setEnabled(false);



                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            view.evaluateJavascript(GET_HTML, null);
                        }
                    }, 8000);



                } else {
                    view.loadUrl(getResources().getString(R.string.js_start)
                            + CHANGE_USERNAME_VALUE_JS
                            + usernameValue
                            + END_OF_VALUE );

                    view.loadUrl(getResources().getString(R.string.js_start)
                            + CHANGE_PASSWORD_VALUE_JS
                            + passwordValue
                            + END_OF_VALUE);
                    view.loadUrl(getResources().getString(R.string.js_start)
                            + SUBMIT_LOGIN_FORM);

                    loginBtn.setEnabled(false);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            view.loadUrl(getResources().getString(R.string.js_start)
                                    + GET_HTML);
                        }
                    }, 8000);

                }



            }
        });
    }

    private void loadChooseDetails() {
        loadingDialog.dismiss();
        chooseDetails = new chooseDetails(MainActivity.this, MainActivity.this);
        chooseDetails.setFm(getSupportFragmentManager());
        chooseDetails.show();
    }



    @Override
    public void onBackPressed() {
        if (pageStatus == DETAILS_STATUS){
            makeMainVisible();
            transactions.clear();
            pageStatus = MAIN_PAGE_STATUS;
        }else {
            super.onBackPressed();
        }
    }

    private void makeWebAlertAndroid() {
        myWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result){
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Alert")
                        .setMessage(message)
                        .setPositiveButton("OK",
                                new AlertDialog.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        result.confirm();
                                    }
                                })
                        .setCancelable(false)
                        .create()
                        .show();
                return true;
            }
        });
    }
}
