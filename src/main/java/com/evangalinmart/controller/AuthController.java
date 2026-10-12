package com.evangalinmart.controller;

import com.evangalinmart.model.Customer;
import com.evangalinmart.repository.CustomerRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class AuthController {
    private final CustomerRepository customers;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(CustomerRepository customers) {
        this.customers = customers;
    }

    @GetMapping("/login")
    public String login(HttpSession session) {
        return session.getAttribute("loggedInEmail") == null ? "login" : "redirect:/";
    }

    @PostMapping("/login")
    public String authenticate(@RequestParam String email, @RequestParam String password,
                               HttpSession session, Model model) {
        return customers.findByEmailIgnoreCase(email.trim())
                .filter(customer -> passwordEncoder.matches(password, customer.getPasswordHash()))
                .map(customer -> {
                    session.setAttribute("loggedInName", customer.getName());
                    session.setAttribute("loggedInEmail", customer.getEmail());
                    session.setAttribute("loggedInRole", customer.getRole());
                    return "SELLER".equals(customer.getRole()) ? "redirect:/admin/products" : "redirect:/";
                })
                .orElseGet(() -> {
                    model.addAttribute("error", "Email or password is incorrect. Please try again.");
                    model.addAttribute("email", email);
                    return "login";
                });
    }

    @GetMapping("/register")
    public String register(HttpSession session) {
        return session.getAttribute("loggedInEmail") == null ? "register" : "redirect:/";
    }

    @PostMapping("/register")
    public String createAccount(@RequestParam String name, @RequestParam String email,
                                @RequestParam String password, @RequestParam String confirmPassword,
                                @RequestParam(defaultValue = "BUYER") String requestedRole,
                                HttpSession session, Model model) {
        String cleanEmail = email.trim().toLowerCase();
        if (name.trim().isEmpty() || cleanEmail.isEmpty() || password.length() < 6) {
            model.addAttribute("error", "Enter your name and email, and use a password with at least 6 characters.");
            model.addAttribute("name", name);
            model.addAttribute("email", email);
            model.addAttribute("requestedRole", requestedRole);
            return "register";
        }
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "The passwords do not match.");
            model.addAttribute("name", name);
            model.addAttribute("email", email);
            model.addAttribute("requestedRole", requestedRole);
            return "register";
        }
        if (customers.existsByEmailIgnoreCase(cleanEmail)) {
            model.addAttribute("error", "An account with this email already exists. Please log in.");
            model.addAttribute("name", name);
            model.addAttribute("email", email);
            model.addAttribute("requestedRole", requestedRole);
            return "register";
        }

        String role = "SELLER".equalsIgnoreCase(requestedRole) ? "SELLER" : "BUYER";
        Customer customer = customers.save(new Customer(name.trim(), cleanEmail, passwordEncoder.encode(password), role));
        session.setAttribute("loggedInName", customer.getName());
        session.setAttribute("loggedInEmail", customer.getEmail());
        session.setAttribute("loggedInRole", customer.getRole());
        return "SELLER".equals(role) ? "redirect:/admin/products" : "redirect:/";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
