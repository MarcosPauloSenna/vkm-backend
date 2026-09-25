package com.vkm_backend.infra.global.exceptions;

public class GroupNotFoundExeception extends RuntimeException{
    public GroupNotFoundExeception() {
        super("Grupo não encontrado.");
    }
}
