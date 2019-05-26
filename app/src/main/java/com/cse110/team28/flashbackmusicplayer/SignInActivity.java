package com.cse110.team28.flashbackmusicplayer;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.api.services.people.v1.People;
import com.google.api.services.people.v1.PeopleScopes;
import com.google.api.services.people.v1.model.EmailAddress;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.Name;
import com.google.api.services.people.v1.model.Person;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener
        , GoogleApiClient.ConnectionCallbacks {
    GoogleApiClient client;
    final int RC_INTENT = 200;
    final int RC_API_CHECK = 100;
    private static ArrayList<String> friends = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_sign_in);

        // create google sign in
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestServerAuthCode(getString(R.string.clientID))
                            .requestEmail()
                            .requestScopes(new Scope(Scopes.PLUS_LOGIN),
                                    new Scope(PeopleScopes.CONTACTS_READONLY))
                            .build();

        // To connect with Google Play Services and Sign In
        client = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions)
                .build();
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Onstart", "connect to client");

        client.connect();
        getIdToken();
    }

    // Performed on Google Sign in click
    private void getIdToken() {
        // Show an account picker to let the user choose a Google account from the device.
        // If the GoogleSignInOptions only asks for IDToken and/or profile and/or email then no
        // consent screen will be shown here.
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(client);
        startActivityForResult(signInIntent, RC_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RC_INTENT:
                Log.d("sign in", "result");
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

                if (result.isSuccess()) {
                    GoogleSignInAccount acct = result.getSignInAccount();
                    System.out.println("onActivityResult:GET_TOKEN:success:" + result.getStatus().isSuccess() +" hi");
                    System.out.println("auth Code:" + acct.getServerAuthCode() + " done");

                    new PeoplesAsync().execute(acct.getServerAuthCode());
                    DBService.provideAccount(acct);
                } else {
                    Log.d(result.getStatus().toString() + "\nmsg: " + result.getStatus().getStatusMessage(), "hello");
                }
                break;

        }

        finish();
    }

    public class PeoplesAsync extends AsyncTask<String, Void, List<Name>> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected List<Name> doInBackground(String... params) {

            List<Name> nameList = new ArrayList<>();

            try {
                People peopleService = PeopleHelper.setUp(SignInActivity.this, params[0]);

                ListConnectionsResponse response = peopleService.people().connections()
                        .list("people/me")
                        // This line's really important! Here's why:
                        // http://stackoverflow.com/questions/35604406/retrieving-information-about-a-contact-with-google-people-api-java
                        .setRequestMaskIncludeField("person.names")
                        .execute();
                List<Person> connections = response.getConnections();
                if(connections == null) {
                    Log.d("No friends", "Cannot iterate through friends");
                }

                for (Person person : connections) {
                    if (!person.isEmpty()) {
                        List<Name> names = person.getNames();
                        if (names != null)
                            for (Name name : names){
                                nameList.add(name);
                                Log.d("name:", name.getDisplayName());}


                    }
                }
                friends = (ArrayList)nameList;
                DBService.provideContacts(nameList);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            return nameList;
        }


        @Override
        protected void onPostExecute(List<Name> nameList) {
            super.onPostExecute(nameList);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.d("connection", "msg: " + connectionResult.getErrorMessage());

        GoogleApiAvailability mGoogleApiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = mGoogleApiAvailability.getErrorDialog(this, connectionResult.getErrorCode(), RC_API_CHECK);
        dialog.show();

    }

    @Override
    public void onConnected(Bundle bundle) {


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public static ArrayList<String> getFriends() {
        return friends;
    }






}
