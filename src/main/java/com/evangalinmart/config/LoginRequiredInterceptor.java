package com.evangalinmart.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        javax.servlet.http.HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("loggedInEmail") != null) {
            String path = request.getRequestURI().substring(request.getContextPath().length());
            if (path.startsWith("/admin/") && !"SELLER".equals(session.getAttribute("loggedInRole"))) {
                response.sendRedirect(request.getContextPath() + "/");
                return false;
            }
            if ((path.equals("/cart") || path.startsWith("/cart/") || path.equals("/checkout") || path.equals("/orders"))
                    && !"BUYER".equals(session.getAttribute("loggedInRole"))) {
                response.sendRedirect(request.getContextPath() + "/admin/products");
                return false;
            }
            if ("POST".equalsIgnoreCase(request.getMethod()) && path.startsWith("/products/") && path.endsWith("/reviews")
                    && !"BUYER".equals(session.getAttribute("loggedInRole"))) {
                response.sendRedirect(request.getContextPath() + "/admin/products");
                return false;
            }
            return true;
        }
        response.sendRedirect(request.getContextPath() + "/login");
        return false;
    }
}
