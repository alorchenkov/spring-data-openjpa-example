package com.cpwr.gdo.simulator.dao.repository;

import com.cpwr.gdo.simulator.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface WsServiceRepository extends JpaRepository<Service, Long>, JpaSpecificationExecutor<Service>  {
    
    /**
     * Find all services.
     *
     * @return the list
     */
    @Query("select s from Service s")
    List<Service> findAllServices();

    /**
     * Creates the service.
     *
     * @param requestType the request type
     * @param isActive the is active
     * @return the integer
     */
    @Modifying
    @Transactional
    @Query("update Service s set s.isActive=?2, s.requestType=?3 where s.requestType=?1")
    Integer setActiveStatusForService(final String requestType, final boolean isActive, final String updRequestType);

    /**
     * Find service by request type.
     *
     * @param requestType the request type
     * @return the service
     */
    @Query("select s from Service s where s.requestType=?1 and s.isActive=true")
    Service findServiceByRequestType(final String requestType);

}
