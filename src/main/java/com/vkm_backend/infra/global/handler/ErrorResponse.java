package com.vkm_backend.infra.global.handler;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record ErrorResponse (@JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
                             LocalDateTime timeStamp,
                             int status,
                             String message,
                             String error){


}
