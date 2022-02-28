package com.gap.bis_inspection.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.db.enumtype.LoginStatusEn;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.manager.IDatabaseManager;
import com.gap.bis_inspection.db.objectmodel.User;
import com.gap.bis_inspection.fragment.login.ActivationFragment;
import com.gap.bis_inspection.fragment.login.DomainFragment;
import com.gap.bis_inspection.fragment.login.LoginFragment;
import com.gap.bis_inspection.fragment.login.PasswordCreationFragment;
import com.gap.bis_inspection.fragment.login.RegistrationFragment;
import com.gap.bis_inspection.service.CoreService;

import java.util.Date;
import java.util.List;

/**
 * Created by root on 9/28/15.
 */
public class MainActivity extends AppCompatActivity {

    private AppController application;
    private Handler handler;
    private CoreService coreService;
    private DatabaseManager databaseManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseManager = new DatabaseManager(this);
        coreService = new CoreService(databaseManager);

        application = AppController.getInstance();


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String data = bundle.getString("data");
            System.out.println("data====" + data);
        }

        //System.out.println("token====" + MyFirebaseMessagingService.getToken(getApplicationContext()));

        //https://bis.tehran.ir
        String baseService = AppController.getInstance().getSharedPreferences().getString(Constants.DOMAIN_WEB_SERVICE_URL, null);

        if (baseService != null) {
            if (baseService.equals("http://bis.tehran.ir")) {
                String strUrl = "https://bis.tehran.ir";
                SharedPreferences.Editor editor = AppController.getInstance().getSharedPreferences().edit();
                editor.putString(Constants.DOMAIN_WEB_SERVICE_URL, strUrl);
                editor.apply();
            }
        }

       /* SharedPreferences.Editor editor = AppController.getInstance().getSharedPreferences().edit();
        editor.putString(Constants.DOMAIN_WEB_SERVICE_URL, domainEditText.getText().toString());
        editor.apply();*/


        FragmentManager fragmentManager = getFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        List<User> userList = coreService.getDatabaseManager().listUsers();

        System.out.println("userList========" + userList.size());


        /*
         * check userList
         * */
        if (userList.isEmpty()) {
            fragmentTransaction.replace(R.id.fragment_place, new DomainFragment());
        } else {
            User user = userList.get(0);

            if (user.getMobileNo() != null) {
                if (user.getLoginStatus().equals(LoginStatusEn.Init.ordinal())) {
                    if (user.getExpireDate().compareTo(new Date()) > 0) {
                        application.setCurrentUser(user);

                        fragmentTransaction.replace(R.id.fragment_place, new ActivationFragment());
                    } else {
                        fragmentTransaction.replace(R.id.fragment_place, new RegistrationFragment());
                    }
                } else if (user.getLoginStatus().equals(LoginStatusEn.PasswordCreation.ordinal())) {
                    application.setCurrentUser(user);

                    fragmentTransaction.replace(R.id.fragment_place, new PasswordCreationFragment());
                } else if (user.getLoginStatus().equals(LoginStatusEn.Registered.ordinal())) {
                    application.setCurrentUser(user);


                    user = userList.get(0);
                    application = AppController.getInstance();
                    application.setCurrentUser(user);
                    System.out.println("setCurrentUser=" + user.getBisPassword());
                    System.out.println("setCurrentUser=" + user.getUsername());

                    //new Thread(new MainActivity.GetMessage()).start();

                    if (user.getAutoLogin() && (user.getLoginIs() != null || !user.getLoginIs())) {
                        user.setLoginIs(Boolean.TRUE);
                        user.setLastLoginDate(new Date());
                        coreService.updateUser(user);


                        System.out.println("getUserPermissionMap=" + user.getId());
                        application.setPermissionMap(coreService.getUserPermissionMap(user.getId()));
                        DatabaseManager.SERVER_USER_ID = user.getServerUserId();
                        showHomePage();

                        System.out.println("getPermissionMap=" +application.getPermissionMap());
                    } else if (!user.getAutoLogin()) {
                        fragmentTransaction.replace(R.id.fragment_place, new LoginFragment());
                    }

                }
            }
        }
        fragmentTransaction.commit();
    }

    public void showHomePage() {
        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(i);
    }

}
