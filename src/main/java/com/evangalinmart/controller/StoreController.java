package com.evangalinmart.controller;

import com.evangalinmart.model.Product;
import com.evangalinmart.model.Review;
import com.evangalinmart.repository.ProductRepository;
import com.evangalinmart.repository.CustomerOrderRepository;
import com.evangalinmart.repository.ReviewRepository;
import com.evangalinmart.service.StoreService;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class StoreController {
    private final ProductRepository products;
    private final CustomerOrderRepository orders;
    private final StoreService store;
    private final ReviewRepository reviews;

    public StoreController(ProductRepository products, CustomerOrderRepository orders, StoreService store, ReviewRepository reviews) {
        this.products = products;
        this.orders = orders;
        this.store = store;
        this.reviews = reviews;
    }

    @SuppressWarnings("unchecked")
    private Map<Long, Integer> cart(HttpSession session) {
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart == null) { cart = new LinkedHashMap<>(); session.setAttribute("cart", cart); }
        return cart;
    }

    @ModelAttribute
    void shared(Model model, HttpSession session) {
        model.addAttribute("cartCount", cart(session).values().stream().mapToInt(Integer::intValue).sum());
    }

    @GetMapping("/")
    public String home(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("products", store.search(q));
        model.addAttribute("query", q == null ? "" : q);
        model.addAttribute("categories", new String[]{"Fresh Fruits", "Fresh Vegetables", "Dairy & Eggs", "Bakery", "Pantry Staples"});
        return "home";
    }

    @GetMapping("/products/{id}")
    public String product(@PathVariable Long id, HttpSession session, Model model) {
        return products.findById(id).map(product -> {
            var productReviews = reviews.findByProductIdOrderByCreatedAtDesc(id);
            model.addAttribute("product", product);
            model.addAttribute("reviews", productReviews);
            model.addAttribute("reviewCount", productReviews.size());
            model.addAttribute("averageRating", productReviews.stream().mapToInt(Review::getRating).average().orElse(0));
            model.addAttribute("hasReviewed", reviews.existsByProductIdAndCustomerEmailIgnoreCase(id, (String) session.getAttribute("loggedInEmail")));
            return "product-details";
        }).orElse("redirect:/");
    }

    @PostMapping("/products/{id}/reviews")
    public String submitReview(@PathVariable Long id, @RequestParam int rating, @RequestParam String comment,
                               HttpSession session, RedirectAttributes flash) {
        Product product = products.findById(id).orElse(null);
        if (product == null) return "redirect:/";
        String email = (String) session.getAttribute("loggedInEmail");
        if (rating < 1 || rating > 5 || comment.trim().isEmpty() || comment.length() > 1200) {
            flash.addFlashAttribute("reviewError", "Choose a 1–5 star rating and enter a comment up to 1,200 characters.");
        } else if (reviews.existsByProductIdAndCustomerEmailIgnoreCase(id, email)) {
            flash.addFlashAttribute("reviewError", "You have already reviewed this grocery item.");
        } else {
            reviews.save(new Review(id, email, (String) session.getAttribute("loggedInName"), rating, comment.trim()));
            flash.addFlashAttribute("reviewNotice", "Thank you! Your review has been added.");
        }
        return "redirect:/products/" + id + "#reviews";
    }

    @PostMapping("/cart/add/{id}")
    public String addToCart(@PathVariable Long id, HttpSession session, RedirectAttributes flash) {
        if (!products.existsById(id)) return "redirect:/";
        cart(session).merge(id, 1, Integer::sum);
        flash.addFlashAttribute("notice", "Added to your bag.");
        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String cartPage(HttpSession session, Model model) {
        Map<Long, Integer> cart = cart(session);
        Map<Product, Integer> lines = new LinkedHashMap<>();
        cart.forEach((id, quantity) -> products.findById(id).ifPresent(p -> lines.put(p, quantity)));
        model.addAttribute("lines", lines);
        model.addAttribute("total", store.total(cart));
        return "cart";
    }

    @PostMapping("/cart/update")
    public String updateCart(@RequestParam Long productId, @RequestParam int quantity, HttpSession session) {
        if (quantity < 1) cart(session).remove(productId); else cart(session).put(productId, Math.min(quantity, 99));
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        if (cart(session).isEmpty()) return "redirect:/cart";
        model.addAttribute("total", store.total(cart(session)));
        model.addAttribute("customerName", session.getAttribute("loggedInName"));
        model.addAttribute("email", session.getAttribute("loggedInEmail"));
        return "checkout";
    }

    @PostMapping("/checkout")
    public String placeOrder(@RequestParam String address,
                             HttpSession session, RedirectAttributes flash) {
        Map<Long, Integer> cart = cart(session);
        if (cart.isEmpty()) return "redirect:/cart";
        String customerName = (String) session.getAttribute("loggedInName");
        String email = (String) session.getAttribute("loggedInEmail");
        var order = store.placeOrder(customerName, email, address, cart);
        session.setAttribute("cart", new LinkedHashMap<Long, Integer>());
        flash.addFlashAttribute("orderId", order.getId());
        return "redirect:/order-success";
    }

    @GetMapping("/order-success")
    public String orderSuccess() { return "order-success"; }

    @GetMapping("/admin/products")
    public String admin(Model model) {
        model.addAttribute("products", products.findAll());
        model.addAttribute("product", new Product("", "Fresh Vegetables", "", new java.math.BigDecimal("1.00"), "", false));
        return "admin/products";
    }

    @PostMapping("/admin/products")
    public String saveProduct(@Valid @ModelAttribute Product product, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("products", products.findAll());
            return "admin/products";
        }
        products.save(product);
        return "redirect:/admin/products";
    }

    @GetMapping("/admin/products/{id}/edit")
    public String editProduct(@PathVariable Long id, Model model) {
        return products.findById(id).map(item -> {
            model.addAttribute("product", item);
            return "admin/product-edit";
        }).orElse("redirect:/admin/products");
    }

    @PostMapping("/admin/products/{id}/edit")
    public String updateProduct(@PathVariable Long id, @Valid @ModelAttribute Product form,
                                BindingResult result, Model model) {
        Product saved = products.findById(id).orElse(null);
        if (saved == null) return "redirect:/admin/products";
        if (result.hasErrors()) return "admin/product-edit";
        saved.setName(form.getName());
        saved.setCategory(form.getCategory());
        saved.setDescription(form.getDescription());
        saved.setPrice(form.getPrice());
        saved.setImageUrl(form.getImageUrl());
        saved.setFeatured(form.isFeatured());
        products.save(saved);
        return "redirect:/admin/products";
    }

    @PostMapping("/admin/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id) {
        products.deleteById(id);
        return "redirect:/admin/products";
    }

    @GetMapping("/orders")
    public String myOrders(HttpSession session, Model model) {
        String email = (String) session.getAttribute("loggedInEmail");
        model.addAttribute("orders", orders.findByEmailIgnoreCaseOrderByCreatedAtDesc(email));
        return "orders";
    }

    @GetMapping("/admin/orders")
    public String adminOrders(Model model) {
        model.addAttribute("orders", orders.findAll());
        return "admin/orders";
    }
}
