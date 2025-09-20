package com.thomas.medicatieinnameapp.serviceTest;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PwHashTest {
    @Test
    void printHashes() {
        var enc = new BCryptPasswordEncoder(10); // zelfde strength als in je app
        System.out.println("Admin123!:   " + enc.encode("Admin123!"));
        System.out.println("admin123!:   " + enc.encode("admin123!"));
        System.out.println("Welkom123!:  " + enc.encode("Welkom123!"));
    }
}