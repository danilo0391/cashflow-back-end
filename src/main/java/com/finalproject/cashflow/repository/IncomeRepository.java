package com.finalproject.cashflow.repository;

import com.finalproject.cashflow.model.Income;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IncomeRepository extends JpaRepository <Income, Long>{

    @Query("FROM Income b WHERE b.date LIKE %:searchText% OR b.value LIKE %:searchText% OR b.category LIKE %:searchText% ORDER BY b.value DESC")
    Page<Income> findAll(Pageable pageable, @Param("searchText")String searchText);

}
