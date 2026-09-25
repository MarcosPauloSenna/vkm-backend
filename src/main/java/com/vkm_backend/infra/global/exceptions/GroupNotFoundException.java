package com.vkm_backend.infra.global.exceptions;

public class GroupNotFoundException extends RuntimeException{
    public GroupNotFoundException() {
        super("Grupo não encontrado.");
    }
}
