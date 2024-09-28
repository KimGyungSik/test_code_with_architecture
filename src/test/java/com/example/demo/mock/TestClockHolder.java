package com.example.demo.mock;

import com.example.demo.common.service.port.ClockHolder;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
public class TestClockHolder implements ClockHolder {

    private long millis;
    @Override
    public long millis() {
        return millis;
    }

    public void changeTime(long millis) {
        this.millis = millis;
    }
}
