package org.metabrainz.mobile.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.metabrainz.mobile.App;
import org.metabrainz.mobile.R;

public class WelcomeFragment extends ContextFragment {

    private TextView welcomeText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_dash_welcome, container);
        welcomeText = (TextView) layout.findViewById(R.id.welcome_text);
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateText();
    }

    public void updateText() {
        if (App.isUserLoggedIn()) {
            welcomeText.setText(getString(R.string.welcome_loggedin) + " " + App.getUser().getUsername());
        } else {
            welcomeText.setText(R.string.welcome);
        }
    }

}
