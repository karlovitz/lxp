package com.redhat.uxl.datalayer.repository;

import com.redhat.uxl.dataobjects.domain.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * The interface Feedback repository.
 */
public interface FeedbackRepository
    extends BaseJpaRepository<Feedback, Long>, JpaSpecificationExecutor<Feedback> {

}
