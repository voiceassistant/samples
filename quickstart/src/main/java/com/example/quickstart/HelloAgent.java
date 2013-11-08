package com.example.quickstart;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import mobi.voiceassistant.base.Request;
import mobi.voiceassistant.base.Response;
import mobi.voiceassistant.base.Token;
import mobi.voiceassistant.base.content.SpeechTextUtils;
import mobi.voiceassistant.client.AssistantAgent;

/**
 * Hello Agent
 */

public class HelloAgent extends AssistantAgent {

    private static final String PREF_NAME = "name";
    private static final String TOKEN_NAME = "UserName";

    @Override
    protected void onCommand(Request request) {
        switch (request.getDispatchId()) {
            case R.id.cmd_hello:
                onHello(request);
                break;
            case R.id.cmd_name:
                onName(request);
                break;
        }
    }

    @Override
    protected void onModalCancel(Request request) {
        request.addQuickResponse(getString(R.string.hello_cancel));
    }

    private void onHello(Request request) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String userName = preferences.getString(PREF_NAME, null);

        if(userName == null) {
            final Response response = request.createResponse();
            response.setContent(getString(R.string.hello_say_name));
            response.enterModalQuestionScope(R.xml.name);
            request.addResponse(response);
        } else {
            final CharSequence content = SpeechTextUtils.textWithSpeech(getString(R.string.hello_hello, userName), getString(R.string.speech_hello, userName));
            request.addQuickResponse(content);
        }
    }

    private void onName(Request request) {
        final Token token = request.getContent();
        final Token nameToken = token.findTokenByName(TOKEN_NAME);
        final String userName = nameToken.getSource();

        if(userName.length() == 0) {
            final Response response = request.createResponse();
            response.setContent(getString(R.string.hello_say_again));
            response.enterModalQuestionScope(R.xml.name);
            request.addResponse(response);
            return;
        }

        final StringBuilder sb = new StringBuilder(userName);
        sb.setCharAt(0, Character.toUpperCase(userName.charAt(0)));
        final String displayName = sb.toString();

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().putString(PREF_NAME, displayName).commit();

        onHello(request);
    }
}
