package uk.gov.companieshouse.api.accounts.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyByte;
import static org.mockito.Mockito.when;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.companieshouse.api.accounts.model.entity.CurrentPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.repository.CurrentPeriodRepository;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.transformer.CurrentPeriodTransformer;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class CurrentPeriodServiceImplTest {

    @Mock
    private CurrentPeriod currentPeriod;

    @Mock
    private CurrentPeriodEntity createCurrentPeriodEntity;

    @Mock
    private CurrentPeriodRepository currentPeriodRepository;

    @Mock
    private MessageDigest messageDigest;

    @Mock
    private CurrentPeriodTransformer currentPeriodTransformer;

    @InjectMocks
    private CurrentPeriodServiceImpl currentPeriodService;



    @BeforeEach
    public void setUp() {
        currentPeriodService.setMessageDigest(messageDigest);
        when(currentPeriodTransformer.transform(currentPeriod))
                .thenReturn(createCurrentPeriodEntity);
        byte[] b = {12, 10, 56, 120, 13, 15};
        when(messageDigest.digest(any())).thenReturn(b);
    }

    @Test
    @DisplayName("Tests the successful creation of a currentPeriod resource")
    public void canCreateCurrentPeriod() {
        ResponseObject<CurrentPeriod> result = currentPeriodService.save(currentPeriod, "");
        assertNotNull(result);
        assertEquals(currentPeriod, result.getData());

    }
}