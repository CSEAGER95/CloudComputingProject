package src.main.java;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInputRepository extends JpaRepository<UserInput, Integer> {
    // You can add custom query methods here if needed
}
