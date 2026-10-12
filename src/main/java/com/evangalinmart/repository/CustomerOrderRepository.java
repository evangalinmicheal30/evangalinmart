package com.evangalinmart.repository;

import com.evangalinmart.model.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {
    java.util.List<CustomerOrder> findByEmailIgnoreCaseOrderByCreatedAtDesc(String email);
}
