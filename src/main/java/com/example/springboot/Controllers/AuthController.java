package com.example.springboot.Controllers;


import com.example.springboot.Repositories.AdminRepository;
import com.example.springboot.Utils.JwtUtil;
import com.example.springboot.entities.admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> accountRegister){
        String email = accountRegister.get("email");
        String password = accountRegister.get("password");

        if (adminRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body("Email đã tồn tại");
        }

        admin newAdmin = new admin();
        newAdmin.setEmail(email);
        newAdmin.setPassword(passwordEncoder.encode(password));
        adminRepository.save(newAdmin);
        return ResponseEntity.ok("Đăng ký thành công");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> account){
        String email = account.get("email");
        String password = account.get("password");

        Optional<admin> optionalAdmin = adminRepository.findByEmail(email);
        //Kiểm tra tồn tài không
        if(optionalAdmin.isPresent() && isValidPassword(password, optionalAdmin.get())){
            String token = jwtUtil.generateToken(email);

            return ResponseEntity.ok(Map.of(
                    "message", "Đăng nhập thành công",
                    "token", token
            ));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Tài khoản sai");
    }

    private boolean isValidPassword(String password, admin existingAdmin) {
        String storedPassword = existingAdmin.getPassword();

        if (passwordEncoder.matches(password, storedPassword)) {
            return true;
        }

        // Chuyển các tài khoản được tạo trước khi dùng BCrypt sang mật khẩu mã hóa.
        if (password.equals(storedPassword)) {
            existingAdmin.setPassword(passwordEncoder.encode(password));
            adminRepository.save(existingAdmin);
            return true;
        }

        return false;
    }

    @GetMapping("/personal")
    public ResponseEntity<?> getCurrentAdmin(@RequestHeader(value = "Authorization", required = false)String authHeader){
        //Kiểm tra header authorization tồn tại không
        if(authHeader == null || authHeader.startsWith("Bearer ")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Lỗi token");
        }

        String token = authHeader.substring(7);

        //Xác thực token
        if(jwtUtil.validateToken(token)){
            String email = jwtUtil.extractEmail(token);
            return ResponseEntity.ok("Xác thực thành công");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Xác thực thất bại");
    }
}
