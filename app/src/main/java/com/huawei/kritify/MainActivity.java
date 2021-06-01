package com.huawei.kritify;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.account.service.AccountAuthService;

public class MainActivity extends AppCompatActivity{

    public static final String TAG = "HuaweiIdActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }

    private void initViews() {
        findViewById(R.id.signInButton).setOnClickListener(v -> signInCode());
    }

    /**
     * Codelab Code
     * Pull up the authorization interface by getSignInIntent
     */
    private void signInCode() {
        AccountAuthParams mAuthParam = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .setIdToken()
                .setAccessToken()
                .createParams();
        AccountAuthService mAuthManager = AccountAuthManager.getService(MainActivity.this, mAuthParam);
        startActivityForResult(mAuthManager.getSignInIntent(), 8888);
    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Process the authorization result and obtain an ID token from AuthAccount.
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 8888) {
            Task<AuthAccount> authAccountTask = AccountAuthManager.parseAuthResultFromIntent(data);
            if (authAccountTask.isSuccessful()) {
                // The sign-in is successful, and the user's ID information and ID token are obtained.
                AuthAccount authAccount = authAccountTask.getResult();
                Log.i(TAG, "idToken:" + authAccount.getIdToken());
                // Obtain the ID type (0: HUAWEI ID; 1: AppTouch ID).
                Log.i(TAG, "accountFlag:" + authAccount.getAccountFlag());

                Intent intent = new Intent(this, FeedActivity.class);
                startActivity(intent);
            } else {
                // The sign-in failed. No processing is required. Logs are recorded for fault locating.
                Log.e(TAG, "sign in failed : " +((ApiException) authAccountTask.getException()).getStatusCode());
                Toast.makeText(this, "Invalid login. Please try again", Toast.LENGTH_LONG).show();
            }
        }
    }
}