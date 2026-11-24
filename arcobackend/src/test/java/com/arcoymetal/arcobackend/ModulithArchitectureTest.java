package com.arcoymetal.arcobackend;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModulithArchitectureTest {

    @Test
    void shouldVerifyModularStructure() {
        ApplicationModules.of(ArcobackendApplication.class).verify();
    }
}
