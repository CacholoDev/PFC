package com.pfcdaw.pfcdaw.service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;

@Service
public class LimitesIntentosService {

    // Mapa en memoria: email -> Bucket (cada email tiene su propio "cubo" de
    // tokens)
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    /**
     * Crea un bucket con límite:
     * - 5 intentos de login
     * - Se recargan 5 tokens cada 15 minutos
     */
    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.builder()
            .capacity(5)
            .refillIntervally(5, Duration.ofMinutes(15))
            .build();
        return Bucket.builder().addLimit(limit).build();
    }

    /**
     * Obtiene o crea un bucket para un email específico
     * 
     * @param email - Email del usuario intentando hacer login
     * @return Bucket - El bucket con los tokens disponibles
     */
    public Bucket resolveBucket(String email) {
        // Si no existe bucket para este email, lo crea y guarda
        return buckets.computeIfAbsent(email, k -> createNewBucket());
    }
    
    /**
     * Verifica si el usuario puede intentar login
     * 
     * @param email - Email del usuario
     * @return true si tiene tokens disponibles, false si alcanzó el límite
     */
    public boolean tryLogin(String email) {
        return resolveBucket(email).tryConsume(1);
    }

        /**
     * Obtiene tokens disponibles para un email (útil para mensajes de error)
     * @param email - Email del usuario
     * @return long - Cantidad de intentos restantes
     */
    public long getAvailableTokens(String email) {
        return resolveBucket(email).getAvailableTokens();
    }
}