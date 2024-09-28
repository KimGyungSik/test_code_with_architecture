package com.example.demo.mock;

import com.example.demo.common.service.port.UuidHolder;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
public class TestUuidHolder implements UuidHolder {

    private  String uuid;
    @Override
    public String random() {
        return uuid;
    }

    public void changeUuid(String uuid) {
        this.uuid = uuid;
    }
}
