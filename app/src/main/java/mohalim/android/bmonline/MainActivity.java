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


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import mohalim.android.bmonline.Adapters.AccountsAdapter;
import mohalim.android.bmonline.Models.Account;
import mohalim.android.bmonline.Utils.Utils;

public class MainActivity extends AppCompatActivity {
    String usernameValue = "";
    String passwordValue = "";
    private final String LOGIN_URL= "https://www.banquemisr.com.eg/onlineservices/onlinebanking/Login.aspx";
    private final String CHANGE_USERNAME_VALUE_JS = "document.getElementById(\"txtId\").value = \"";
    private final String CHANGE_PASSWORD_VALUE_JS = "document.getElementById(\"txtpwd\").value = \"";
    private final String END_OF_VALUE= "\";";
    private final String SUBMIT_LOGIN_FORM = "document.getElementById('Button1').click();";
    private final String GET_HTML_FROM_ACCOUNTS = "javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');";

    private final int LOGIN_STATUS = 1;
    private final int MAIN_PAGE_STATUS = 2;
    private final int BALANCE_STATUS = 3;
    private final int DETAILS_CHOOSE_STATUS = 4;
    private final int DETAILS_STATUS = 5;

    private int pageStatus = LOGIN_STATUS;


    ConstraintLayout loginConstraint,
            mainConstraint,
            balanceConstraint,
            detailschooserConstraint,
            detailsContainerConstraint;



    WebView myWebView;
    AlertDialog.Builder alertBuilder;
    AlertDialog alertDialog;

    EditText usernameET, passwordET;
    Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        usernameET = findViewById(R.id.username_et);
        passwordET = findViewById(R.id.password_et);
        loginBtn = findViewById(R.id.login);

        loginConstraint = findViewById(R.id.login_container);
        mainConstraint = findViewById(R.id.main_screen_container);
        balanceConstraint = findViewById(R.id.balance_screen_container);
        detailschooserConstraint = findViewById(R.id.detail_screen_chooser);
        detailsContainerConstraint = findViewById(R.id.detail_screen_container);

        alertBuilder = Utils.getAlertBuilder(this, "رجاء الانتظار...","جاري الاتصال بالانترنت");
        alertBuilder.setCancelable(false);
        alertDialog = alertBuilder.show();


        makeLoginVisible();



        /**
         * Instintiate the web view
         */
        final WebView myWebView = (WebView) findViewById(R.id.bmonline_webview);
        myWebView.loadUrl(LOGIN_URL);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.getSettings().setDomStorageEnabled(true);

        /**
         * Do Login when finish
         */
        myWebView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(final WebView view, String url) {


                // After Login page load completed
                if (pageStatus == LOGIN_STATUS){
                    alertDialog.dismiss();

                    loginBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            usernameValue = usernameET.getText().toString();
                            passwordValue = passwordET.getText().toString();


                            view.addJavascriptInterface(new LoadAccountListener(), "HTMLOUT");
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                                view.evaluateJavascript(CHANGE_USERNAME_VALUE_JS + usernameValue + END_OF_VALUE, null);
                                view.evaluateJavascript(CHANGE_PASSWORD_VALUE_JS + passwordValue + END_OF_VALUE, null);
                                view.evaluateJavascript(SUBMIT_LOGIN_FORM, null);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        view.evaluateJavascript(GET_HTML_FROM_ACCOUNTS, null);
                                    }
                                }, 10000);



                            } else {
                                view.loadUrl("javascript: "+ CHANGE_USERNAME_VALUE_JS + usernameValue + END_OF_VALUE );
                                view.loadUrl("javascript: "+ CHANGE_PASSWORD_VALUE_JS + passwordValue + END_OF_VALUE);
                                view.loadUrl("javascript: "+ SUBMIT_LOGIN_FORM);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        view.loadUrl("javascript: "+ GET_HTML_FROM_ACCOUNTS);
                                    }
                                }, 10000);

                            }


                        }
                    });

                }



            }
        });


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


    /**
     * JavaScriptInterface to get Html From the Web View;
     */
    class LoadAccountListener{

        @JavascriptInterface
        public void processHTML(String html)
        {
            if (pageStatus == LOGIN_STATUS){
                if (html.contains("Accounts")){
                    Document doc = Jsoup.parse(html);
                    Elements accountsSpanElements = doc.select("#lblAccountsTable");
                    Element accountsSpanElement = accountsSpanElements.first();
                    Elements accountRows = accountsSpanElement.select("table tbody tr");
                    final List<Account> accounts = new ArrayList<>();

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
                            makeMainVisible();

                            RecyclerView accountRecyclerView = findViewById(R.id.accounts_recycler_view);
                            AccountsAdapter accountsAdapter = new AccountsAdapter();
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
                    alertDialog.setMessage("Wrong credintial");
                    alertDialog.show();
                }
            }




        }
    }

    /**
     * Visiblity of screens
     */

    public void makeLoginVisible(){
        loginConstraint.setVisibility(View.VISIBLE);
        mainConstraint.setVisibility(View.INVISIBLE);
        balanceConstraint.setVisibility(View.INVISIBLE);
        detailschooserConstraint.setVisibility(View.INVISIBLE);
        detailsContainerConstraint.setVisibility(View.INVISIBLE);
    }

    public void makeMainVisible(){
        loginConstraint.setVisibility(View.INVISIBLE);
        mainConstraint.setVisibility(View.VISIBLE);
        balanceConstraint.setVisibility(View.INVISIBLE);
        detailschooserConstraint.setVisibility(View.INVISIBLE);
        detailsContainerConstraint.setVisibility(View.INVISIBLE);
    }

    public void makeBalanceVisible(){
        loginConstraint.setVisibility(View.INVISIBLE);
        mainConstraint.setVisibility(View.INVISIBLE);
        balanceConstraint.setVisibility(View.VISIBLE);
        detailschooserConstraint.setVisibility(View.INVISIBLE);
        detailsContainerConstraint.setVisibility(View.INVISIBLE);
    }

    public void makeDetailsChooserVisible(){
        loginConstraint.setVisibility(View.INVISIBLE);
        mainConstraint.setVisibility(View.INVISIBLE);
        balanceConstraint.setVisibility(View.INVISIBLE);
        detailschooserConstraint.setVisibility(View.VISIBLE);
        detailsContainerConstraint.setVisibility(View.INVISIBLE);
    }

    public void makeDetailsVisible(){
        loginConstraint.setVisibility(View.INVISIBLE);
        mainConstraint.setVisibility(View.INVISIBLE);
        balanceConstraint.setVisibility(View.INVISIBLE);
        detailschooserConstraint.setVisibility(View.INVISIBLE);
        detailsContainerConstraint.setVisibility(View.VISIBLE);
    }

}
