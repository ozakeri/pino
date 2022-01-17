package com.gap.bis_inspection.db.objectmodel;

public class GlobalDomain {
    private String domain;
    private static GlobalDomain instance = new GlobalDomain();
    public static GlobalDomain getInstance() {
        return instance;
    }
    private boolean onRestart = false;
    private boolean isSizeCorrect = false;

    private GlobalDomain() {

    }


    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }


    public boolean isOnRestart() {
        return onRestart;
    }

    public void setOnRestart(boolean onRestart) {
        this.onRestart = onRestart;
    }

    public boolean isSizeCorrect() {
        return isSizeCorrect;
    }

    public void setSizeCorrect(boolean sizeCorrect) {
        isSizeCorrect = sizeCorrect;
    }
}
