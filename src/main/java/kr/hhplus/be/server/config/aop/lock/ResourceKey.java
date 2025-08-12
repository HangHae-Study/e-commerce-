package kr.hhplus.be.server.config.aop.lock;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResourceKey {
    Resource resource();
    String key();
}
