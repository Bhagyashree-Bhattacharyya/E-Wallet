package org.myworkspace.UserServiceApplication.Repositories;

import org.myworkspace.UserServiceApplication.Entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {
    Users findByContactNo(String contactNo);
}
