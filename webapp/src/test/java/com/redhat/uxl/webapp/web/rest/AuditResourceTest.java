package com.redhat.uxl.webapp.web.rest;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.redhat.uxl.datalayer.repository.PersistenceAuditEventRepository;
import com.redhat.uxl.dataobjects.domain.PersistentAuditEvent;
import com.redhat.uxl.webapp.Application;
import com.redhat.uxl.webapp.config.audit.AuditEventConverter;
import com.redhat.uxl.webapp.service.AuditEventService;
import com.redhat.uxl.webapp.service.impl.AuditEventServiceImpl;
import javax.inject.Inject;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

/**
 * The type Audit resource test.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
// TODO: WJK: Determine what to replace this with
//@IntegrationTest
@Transactional
public class AuditResourceTest {

    private static final String SAMPLE_PRINCIPAL = "SAMPLE_PRINCIPAL";
    private static final String SAMPLE_TYPE = "SAMPLE_TYPE";
    private static final LocalDateTime SAMPLE_TIMESTAMP = LocalDateTime.parse("2015-08-04T10:11:30");

    @Inject
    private PersistenceAuditEventRepository auditEventRepository;

    @Inject
    private AuditEventConverter auditEventConverter;

    private PersistentAuditEvent auditEvent;

    private MockMvc restAuditMockMvc;

    /**
     * Sets .
     */
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AuditEventService auditEventService = new AuditEventServiceImpl(auditEventRepository, auditEventConverter);
        AuditResource auditResource = new AuditResource(auditEventService);
        this.restAuditMockMvc = MockMvcBuilders.standaloneSetup(auditResource).build();
    }

    /**
     * Init test.
     */
    @Before
    public void initTest() {
        auditEventRepository.deleteAll();
        auditEvent = new PersistentAuditEvent();
        auditEvent.setAuditEventType(SAMPLE_TYPE);
        auditEvent.setPrincipal(SAMPLE_PRINCIPAL);
        auditEvent.setAuditEventDate(SAMPLE_TIMESTAMP);
    }


    /**
     * Gets all audits.
     *
     * @throws Exception the exception
     */
    @Test
    public void getAllAudits() throws Exception {
        // Initialize the database
        auditEventRepository.save(auditEvent);

        // Get all the audits
        restAuditMockMvc.perform(get("/api/audits")).andExpect(status().isOk())
                // .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].principal").value(hasItem(SAMPLE_PRINCIPAL)));
    }

    /**
     * Gets audit.
     *
     * @throws Exception the exception
     */
    @Test
    public void getAudit() throws Exception {
        // Initialize the database
        auditEventRepository.save(auditEvent);

        // Get the audit
        restAuditMockMvc.perform(get("/api/audits/{id}", auditEvent.getId())).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.principal").value(SAMPLE_PRINCIPAL));
    }

    /**
     * Gets non existing audit.
     *
     * @throws Exception the exception
     */
    @Test
    public void getNonExistingAudit() throws Exception {
        // Get the audit
        restAuditMockMvc.perform(get("/api/audits/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

}
