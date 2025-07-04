package com.bkap.controller;

import com.bkap.entity.Customer;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // Nếu dùng mã hóa password
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/customer")
public class ChangePasswordController {

    @Autowired
    private com.bkap.repositoty.CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Nếu password có mã hóa (bcrypt...)

    // Hiển thị form đổi mật khẩu
    @GetMapping("/change-password")
    public String showChangePasswordForm(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            return "redirect:/customer/login";
        }
        model.addAttribute("changePasswordForm", new ChangePasswordForm());
        return "customer/change-password";
    }

    // Xử lý đổi mật khẩu
    @PostMapping("/change-password")
    public String changePassword(@ModelAttribute("changePasswordForm") ChangePasswordForm form,
                                 BindingResult bindingResult,
                                 HttpSession session,
                                 Model model) {
        Customer sessionCustomer = (Customer) session.getAttribute("customer");
        if (sessionCustomer == null) {
            return "redirect:/customer/login";
        }

        // Validate các trường
        if (bindingResult.hasErrors()) {
            return "customer/change-password";
        }

        // Lấy customer từ DB (luôn lấy lại từ DB để tránh sai session)
        Customer customer = customerRepository.findById(sessionCustomer.getId()).orElse(null);
        if (customer == null) {
            model.addAttribute("error", "Customer not found.");
            return "customer/change-password";
        }

        // Kiểm tra mật khẩu cũ
        boolean match = passwordEncoder.matches(form.getOldPassword(), customer.getPassword());
        if (!match) {
            model.addAttribute("error", "Old password is incorrect.");
            return "customer/change-password";
        }

        // Kiểm tra xác nhận mật khẩu mới
        if (!form.getNewPassword().equals(form.getConfirmPassword())) {
            model.addAttribute("error", "New password and confirmation do not match.");
            return "customer/change-password";
        }

        // Đổi mật khẩu
        customer.setPassword(passwordEncoder.encode(form.getNewPassword()));
        customerRepository.save(customer);

        // Cập nhật lại session nếu cần
        session.setAttribute("customer", customer);

        model.addAttribute("message", "Password changed successfully!");
        return "customer/change-password";
    }

    // Inner static class cho form đổi mật khẩu
    public static class ChangePasswordForm {
        @NotBlank
        private String oldPassword;
        @NotBlank
        private String newPassword;
        @NotBlank
        private String confirmPassword;

        // getter/setter
        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
        public String getConfirmPassword() { return confirmPassword; }
        public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    }
}