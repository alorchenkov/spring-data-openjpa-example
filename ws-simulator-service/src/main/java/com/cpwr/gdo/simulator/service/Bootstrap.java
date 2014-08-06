package com.cpwr.gdo.simulator.service;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cpwr.gdo.simulator.dao.repository.WsFailedRequestRepository;
import com.cpwr.gdo.simulator.dao.repository.security.AuthorityRepository;
import com.cpwr.gdo.simulator.model.FailedRequest;
import com.cpwr.gdo.simulator.model.security.Authority;
import com.cpwr.gdo.simulator.model.security.AuthorityEnum;

/**
 * The class populates the dictionary tables with values if the database has not
 * been set up with data.
 *
 */
@Service
public final class Bootstrap {
    private static final Logger LOG = LoggerFactory.getLogger(Bootstrap.class);
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private WsFailedRequestRepository failedRequestRepository;

    @PostConstruct
    @Transactional
    private void loadDictionaries() {
        LOG.debug("loadDictionaries...");

        checkNotNull(authorityRepository, "AuthorityRepository must not be null!");
        checkNotNull(failedRequestRepository, "WsFailedRequestRepository must not be null!");

        for (AuthorityEnum auth : AuthorityEnum.values()) {

            Authority authority = authorityRepository.findByAuthority(auth.toString());

            if (authority == null) {
                authority = new Authority(auth.toString());

                authorityRepository.saveAndFlush(authority);

                LOG.debug("new role was saved: {}", authority);
            }

        }

        /* load failed request test data */
        loadTestFailedRequestData();

    }

    private void loadTestFailedRequestData() {
        LOG.debug("loadTestFailedRequestData...");

        final long testFailedRequestAmount = failedRequestRepository.count();

        if (testFailedRequestAmount < 50) {
            for (int i = 1; i < 5; i++) {
                FailedRequest failedRequest = new FailedRequest("","","");
                failedRequest.setRequestType("failedrequestType" + i);
                failedRequest.setRequest("<request>value=" + i + "</request>");
                failedRequestRepository.saveAndFlush(failedRequest);
            }
        }

    }
}
