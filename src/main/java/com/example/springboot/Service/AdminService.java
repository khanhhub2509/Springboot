package com.example.springboot.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.springboot.Repositories.AdminRepository;
import com.example.springboot.entities.admin;

@Service
public class AdminService {
    //Đối tượng giúp làm việc với DB
    @Autowired
    public AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    //Phương thức lấy tất cả bản ghi
    public List<admin> getAllAdmin() {
        return adminRepository.findAll();
    }

    //Phương thức lấy 1 bản ghi theo id
    public admin getAdminById(Long id) {
        return adminRepository.findById(id).orElseThrow(() -> new RuntimeException("Không có admin phù hợp"));
    }

    //Phương thức lưu dữ liệu
    public admin createAdmin(admin admin) {
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        return adminRepository.save(admin);
    }

    //phương thức sửa dữ liệu
    public admin updateAdmin(Long id, admin admin) {
        admin existingAdmin = getAdminById(id);
        existingAdmin.setFullName(admin.getFullName());
        existingAdmin.setEmail(admin.getEmail());
        if (admin.getPassword() != null && !admin.getPassword().isBlank()) {
            existingAdmin.setPassword(passwordEncoder.encode(admin.getPassword()));
        }
        existingAdmin.setPhone(admin.getPhone());
        return adminRepository.save(existingAdmin);
    }

    //Phương thức xóa dữ liệu
    public admin deleteAdmin(Long id) {
        admin existingAdmin = getAdminById(id);
        adminRepository.deleteById(id);
        return existingAdmin;
    }
}
