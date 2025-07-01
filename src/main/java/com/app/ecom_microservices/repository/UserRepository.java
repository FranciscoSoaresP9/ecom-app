package com.app.ecom_microservices.repository;

import com.app.ecom_microservices.module.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
