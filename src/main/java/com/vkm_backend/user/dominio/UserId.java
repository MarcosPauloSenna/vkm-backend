package com.vkm_backend.user.dominio;

import java.util.UUID;


public record UserId(UUID value) {

    public static UserId newId(){
        return new UserId(UUID.randomUUID());
    }

    public static UserId of(UUID value){
        return new UserId(value);
    }

    public static UserId of(String value){
        return new UserId(UUID.fromString(value));
    }
}
