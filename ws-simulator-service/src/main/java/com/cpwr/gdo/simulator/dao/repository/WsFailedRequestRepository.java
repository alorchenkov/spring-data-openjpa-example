package com.cpwr.gdo.simulator.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.cpwr.gdo.simulator.model.FailedRequest;

public interface WsFailedRequestRepository extends JpaRepository<FailedRequest, Long>,
        JpaSpecificationExecutor<FailedRequest> {
}
