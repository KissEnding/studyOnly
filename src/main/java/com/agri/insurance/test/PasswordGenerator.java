package com.agri.insurance.test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "Zhangsan123";
        String hash = encoder.encode(password);
        
        System.out.println("Password: " + password);
        System.out.println("Hash: " + hash);
        System.out.println("Verification: " + encoder.matches(password, hash));
        
        // 生成 SQL
        System.out.println("\nSQL to update password:");
        System.out.println("UPDATE admin SET password = '" + hash + "' WHERE username = 'admin';");
    }
} 