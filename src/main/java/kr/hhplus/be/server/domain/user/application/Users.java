package kr.hhplus.be.server.domain.user.application;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Users {
    private final Long userId;
    private String username;
    private LocalDateTime createDt;

}
