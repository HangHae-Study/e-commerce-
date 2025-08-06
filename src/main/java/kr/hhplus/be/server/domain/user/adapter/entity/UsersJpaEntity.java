package kr.hhplus.be.server.domain.user.adapter.entity;


import jakarta.persistence.*;
import kr.hhplus.be.server.domain.user.application.Users;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name="users")
public class UsersJpaEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(length = 20)
    private String username;

    @CreationTimestamp
    @Column(
            nullable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP"
    )
    private LocalDateTime createDt;
    @UpdateTimestamp
    @Column(
            name = "update_dt",
            nullable = false,
            columnDefinition = "DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
    )
    private LocalDateTime udpateDt;

    public static UsersJpaEntity fromDomain(Users user) {
        UsersJpaEntity entity = new UsersJpaEntity();
        entity.userId = user.getUserId(); // null일 경우에는 JPA가 생성
        entity.username = user.getUsername();
        entity.createDt = user.getCreateDt();
        entity.udpateDt = user.getUpdateDt();
        return entity;
    }

    public Users toDomain() {
        return Users.builder()
                .userId(userId)
                .username(username)
                .createDt(createDt)
                .updateDt(udpateDt)
                .build();
    }

}
