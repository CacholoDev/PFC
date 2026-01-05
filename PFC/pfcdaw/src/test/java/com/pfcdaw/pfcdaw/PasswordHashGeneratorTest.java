package com.pfcdaw.pfcdaw;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Test simple para generar hashes BCrypt
 * NO necesita Spring ni Base de Datos
 * Ejecuta rápido (<1 segundo)
 */
class PasswordHashGeneratorTest {

    @Test
    void generateHashesForDataSql() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║  GENERADOR DE HASHES BCRYPT PARA data.sql                ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");
        
        // ADMIN
        String adminPassword = "admin123/ñ";
        String adminHash = encoder.encode(adminPassword);
        System.out.println("🔐 ADMIN:");
        System.out.println("   Password: " + adminPassword);
        System.out.println("   Hash:     " + adminHash);
        System.out.println();
        
        // USER
        String userPassword = "user123/ñ";
        String userHash = encoder.encode(userPassword);
        System.out.println("👤 USER:");
        System.out.println("   Password: " + userPassword);
        System.out.println("   Hash:     " + userHash);
        System.out.println();
        
        // VERIFICACIÓN
        System.out.println("✅ VERIFICACIÓN:");
        System.out.println("   Admin matches: " + encoder.matches(adminPassword, adminHash));
        System.out.println("   User matches:  " + encoder.matches(userPassword, userHash));
        
        System.out.println("\n📋 COPIA LOS HASHES DE ARRIBA PARA TU data.sql\n");
    }
    
    @Test
    void generateCustomPassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Cambia esto por la contraseña que quieras
        String customPassword = "miPasswordPersonalizada";
        String hash = encoder.encode(customPassword);
        
        System.out.println("\n🔑 Hash personalizado:");
        System.out.println("   Password: " + customPassword);
        System.out.println("   Hash:     " + hash);
        System.out.println();
    }

}
