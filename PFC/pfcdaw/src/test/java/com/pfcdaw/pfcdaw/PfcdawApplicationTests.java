package com.pfcdaw.pfcdaw;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
@SpringBootTest // necesito bd + spring correndo

class PfcdawApplicationTests {

	// OPCIÓN 1: Inyección por campo (recomendado para tests)
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	void generateHashForDataSql() {
		System.out.println("\n=== GENERANDO HASHES PARA data.sql ===\n");
		
		// Admin
		String adminPassword = "admin123/ñ";
		String adminHash = passwordEncoder.encode(adminPassword);
		System.out.println("ADMIN:");
		System.out.println("  Password: " + adminPassword);
		System.out.println("  Hash: " + adminHash);
		
		// User
		String userPassword = "user123/ñ";
		String userHash = passwordEncoder.encode(userPassword);
		System.out.println("\nUSER:");
		System.out.println("  Password: " + userPassword);
		System.out.println("  Hash: " + userHash);
		
		// Verificar que funcionan
		System.out.println("\n=== VERIFICANDO HASHES ===");
		System.out.println("Admin matches: " + passwordEncoder.matches(adminPassword, adminHash));
		System.out.println("User matches: " + passwordEncoder.matches(userPassword, userHash));
		System.out.println("\n✅ Copia los hashes de arriba para tu data.sql\n");
	}
	
	// OPCIÓN 2: Test sin Spring (más rápido, recomendado para generar hashes)
	@Test
	void generateHashWithoutSpring() {
		// No necesitas levantar Spring para generar hashes
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		
		String adminPassword = "admin123/ñ";
		String userPassword = "user123/ñ";
		
		System.out.println("\n=== HASHES SIN SPRING CONTEXT ===");
		System.out.println("Admin hash: " + encoder.encode(adminPassword));
		System.out.println("User hash: " + encoder.encode(userPassword));
	}

}
