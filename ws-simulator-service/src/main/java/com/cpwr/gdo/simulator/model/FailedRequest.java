package com.cpwr.gdo.simulator.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "failed_request")
@SequenceGenerator(name = "failed_request_id_gen", sequenceName = "failed_request_id_gen", allocationSize = 1)
public final class FailedRequest extends AbstractEntity<Long> implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "failed_request_id_gen")
    private Long id;

    @NotEmpty
    @Column(name = "request_type")
    private String requestType;

    @NotEmpty
    @Column(name = "request")
    @Lob
    private String request;

    @Column(name = "comments")
    private String comments;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public FailedRequest(final String requestType, final String request, final String comments) {
        this.requestType = requestType;
        this.request = request;
        this.comments = comments;
    }

    @Override
    public String toString() {

        return new ToStringBuilder(this).append("id", id).append("requestType", requestType).append("request", request)
                .append("comments", comments).toString();
    }
}
