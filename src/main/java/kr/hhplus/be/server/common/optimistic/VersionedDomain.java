package kr.hhplus.be.server.common.optimistic;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class VersionedDomain {
    @Builder.Default
    protected Long version = 0L;
}
