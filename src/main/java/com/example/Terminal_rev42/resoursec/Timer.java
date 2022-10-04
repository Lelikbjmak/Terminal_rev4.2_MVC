package com.example.Terminal_rev42.resoursec;

import org.springframework.stereotype.Component;

@Component
public class Timer{

    private java.util.Timer timer;

    public Timer(){
        this.timer = new java.util.Timer("CheckBillActive", true);
    }

    public java.util.Timer getTimer() {
        return timer;
    }
}
