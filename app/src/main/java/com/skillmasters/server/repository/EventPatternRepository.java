package com.skillmasters.server.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.skillmasters.server.model.EventPattern;

@Repository
public interface EventPatternRepository extends JpaRepository<EventPattern, Long>, QuerydslPredicateExecutor<EventPattern>
{
}