package com.pragma.powerup;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.profiles.active=test")
class PowerUpApplicationTests {

    @BeforeAll
    static void setup() {
        // Cargamos las variables de entorno para que el contexto de Spring en el test las reconozca
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
    }

    @Test
    void contextLoads() {
        // Test de humo para verificar que el contexto carga
    }

}
