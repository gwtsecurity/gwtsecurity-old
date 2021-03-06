package com.gwt.ss.sharedservice.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.gwt.ss.client.GwtLoginAsync;
import com.gwt.ss.client.loginable.AbstractLoginBox;
import com.gwt.ss.client.loginable.LoginEvent;

public class LoginBox extends AbstractLoginBox {

    private static LoginBox loginBox;
    private String loginUrl;
    private TextBox userNameFIeld;
    private PasswordTextBox passworldField;
    private CheckBox remeberMeField;
    private Messages messages;
    private Label msgLabel;
    private Button submitButton;
    private Button cancelButton;

    public static LoginBox getLoginBox(String loginUrl) {
        if (loginUrl == null || loginUrl.isEmpty()) {
            Window.alert("login Url " + Messages.Util.getInstance().notNullValue());
            throw new RuntimeException("login Url " + Messages.Util.getInstance().notNullValue());
        } else {
            if (loginBox == null) {
                loginBox = new LoginBox();
            }
            loginBox.setLoginUrl(loginUrl);
            return loginBox;
        }
    }

    private LoginBox() {
        super(false, true);
        init();
    }

    private void init() {
        messages = Messages.Util.getInstance();
        setText(messages.loginRequired());
        setAnimationEnabled(true);
        FlexTable outer = new FlexTable();
        FlexTable.FlexCellFormatter formatter = outer.getFlexCellFormatter();

        msgLabel = new Label(messages.loginRequired());
        msgLabel.getElement().setAttribute("style", "color:red");
        outer.setWidget(0, 0, msgLabel);
        formatter.setColSpan(0, 0, 2);

        outer.setWidget(1, 0, new Label(messages.userName() + ":"));
        userNameFIeld = new TextBox();
        outer.setWidget(1, 1, userNameFIeld);

        outer.setWidget(2, 0, new Label(messages.password()));
        passworldField = new PasswordTextBox();
        outer.setWidget(2, 1, passworldField);

        remeberMeField = new CheckBox(messages.remeberMe());
        outer.setWidget(3, 0, remeberMeField);
        formatter.setColSpan(3, 0, 2);

        submitButton = new Button(messages.submitButton(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                LoginBox.this.submitButton.setEnabled(false);
                String userName = getUserNameFIeld().getValue();
                String password = getPassworldField().getValue();
                getSubmitButton().setEnabled(false);
                if (userName == null || userName.isEmpty()) {
                    getSubmitButton().setEnabled(true);
                    getUserNameFIeld().setFocus(true);
                    setMsgValue(messages.userName() + " " + messages.notNullValue());
                } else if (password == null || password.isEmpty()) {
                    getSubmitButton().setEnabled(true);
                    getPassworldField().setFocus(true);
                    setMsgValue(messages.password() + " " + messages.notNullValue());
                } else {
                    GwtLoginAsync loginService = GwtLoginAsync.Util.getInstance(getLoginUrl());
                    loginService.j_gwt_security_check(userName, password, getRemeberMeField().getValue(), new AsyncCallback<Void>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            GWT.log("Error response:\n" + caught.getMessage(), caught);
                            LoginBox.this.setMsgValue(messages.errorProne() + ":"
                                    + (caught.getMessage().length() > 10 ? caught.getMessage().substring(0, 10) + "..." : caught.getMessage()));
                            getSubmitButton().setEnabled(true);
                        }

                        @Override
                        public void onSuccess(Void result) {
                            hide();
                            GWT.log("user login succeed. ready to fire Login event(true)");
                            fireEvent(new LoginEvent(true));
                            getSubmitButton().setEnabled(true);
                            setMsgValue(messages.loginRequired());
                        }
                    });
                }
            }
        });
        outer.setWidget(4, 0, submitButton);
        formatter.setHorizontalAlignment(4, 0, HasHorizontalAlignment.ALIGN_CENTER);

        cancelButton = new Button(messages.cancelButton(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                hide();
                GWT.log("user click cancel button to stop login, so issue a Login event(false)");
                fireEvent(new LoginEvent(false));
            }
        });

        outer.setWidget(4, 1, cancelButton);
        formatter.setHorizontalAlignment(4, 1, HasHorizontalAlignment.ALIGN_CENTER);
        setWidget(outer);
    }

    @Override
    public void show() {
        super.show();
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

            @Override
            public void execute() {
                userNameFIeld.setFocus(true);
            }
        });

    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public void setMsgValue(String msg) {
        this.msgLabel.setText(msg == null ? "" : msg);
    }

    public PasswordTextBox getPassworldField() {
        return passworldField;
    }

    public TextBox getUserNameFIeld() {
        return userNameFIeld;
    }

    public Button getSubmitButton() {
        return submitButton;
    }

    public CheckBox getRemeberMeField() {
        return remeberMeField;
    }

    @Override
    public void startLogin(Throwable caught) {
        this.setMsgValue(messages.errorProne() + ":"
                + (caught.getMessage().length() > 10 ? caught.getMessage().substring(0, 10) + "..." : caught.getMessage()));
        center();
    }
}
