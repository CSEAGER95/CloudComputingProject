package edu.appstate.cs.cloud.restful;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserInputRepository extends JpaRepository<UserInput, Integer> {
    // You can add custom query methods here if needed

    // Custom query to fetch user inputs by a specific criterion (e.g., type or status)
    List<UserInput> findByType(String type);

    // Placeholder for processing logic to generate satirical AI stories
    // This logic should be implemented in the service layer or a utility class
}
