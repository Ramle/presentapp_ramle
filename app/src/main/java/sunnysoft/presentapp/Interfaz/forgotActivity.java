package sunnysoft.presentapp.Interfaz;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import sunnysoft.presentapp.R;

public class forgotActivity extends AppCompatActivity {

    String forgot_password_url;
    WebView forgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        // recibe url para webview
        Bundle datos = getIntent().getExtras();
        forgot_password_url = datos.getString("forgot_password_url");

        forgot = (WebView) findViewById(R.id.forgotpwd);

        // configuramos webview

        WebSettings webSettings = forgot.getSettings();
        webSettings.setJavaScriptEnabled(true);
        forgot.setWebViewClient(new WebViewClient());
        forgot.loadUrl(forgot_password_url);
    }
}
