package com.gwt.ss.demo1.client;

import com.gwt.ss.sharedservice.client.DefaultEntry;
import com.gwt.ss.sharedservice.client.RemoteAsync;

public class demo1 extends DefaultEntry {
    
    @Override
    protected RemoteAsync getGreetingService() {
        RemoteAsync gs = super.getGreetingService();
        if (gs == null) {
            gs = GreetingServiceAsync.Util.getInstance(getLoginUrl());
            super.setGreetingService(gs);
        }
        return gs;
    }
    
    @Override
    protected RemoteAsync getStaffService() {
        RemoteAsync sf = super.getStaffService();
        if (sf == null) {
            sf = StaffServiceAsync.Util.getInstance(getLoginUrl());
            super.setStaffService(sf);
        }
        return sf;
    }
    
    @Override
    public String getLoginUrl() {
        return "j_spring_security_check";
    }
    
    @Override
    public String getLogoutUrl() {
        return "j_spring_security_logout";
    }
}
