package com.agri.insurance.test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123";
        String dbHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTTyUor7IKm";
        
        // 测试数据库中的密码
        System.out.println("Testing with DB hash:");
        System.out.println("Password: " + password);
        System.out.println("DB Hash: " + dbHash);
        System.out.println("DB Match: " + encoder.matches(password, dbHash));
        
        // 生成新的哈希测试
        System.out.println("\nTesting with new hash:");
        String newHash = encoder.encode(password);
        System.out.println("New Hash: " + newHash);
        System.out.println("New Match: " + encoder.matches(password, newHash));
        
        // 测试错误密码
        String wrongPassword = "wrong123";
        System.out.println("\nTesting wrong password:");
        System.out.println("Wrong Password: " + wrongPassword);
        System.out.println("DB Match: " + encoder.matches(wrongPassword, dbHash));
    }
} 