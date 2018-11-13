package com.example.archer.amadeus;

/**
 * Created by noble on 3/13/18.
 */

public class SmsSingleton {
    private static final SmsSingleton ourInstance = new SmsSingleton();
    private int incomingSkipCount;
    private int outgoingSkipCount;


    static SmsSingleton getInstance() {
        return ourInstance;
    }

    private SmsSingleton() {
        this.incomingSkipCount = 0;
        this.outgoingSkipCount= 0;
    }

    public void decrementIncomingSkipCount() {
        this.incomingSkipCount--;
    }

    public void decrementOutgoingSkipCount() {
        this.outgoingSkipCount--;
    }

    public void incrementIncomingSkipCount() {
        this.incomingSkipCount++;
    }

    public void incrementOutgoingSkipCount() {
        this.outgoingSkipCount++;
    }

    public int getOutgoingSkipCount() {
        return this.outgoingSkipCount;
    }

    public int getIncomingSkipCount() {
        return  this.incomingSkipCount;
    }
}
