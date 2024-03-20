package edu.unam.springsecurity.system.repository;

import edu.unam.springsecurity.system.model.CatContactType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatContactTypeRepository extends JpaRepository<CatContactType, Integer> {
}
