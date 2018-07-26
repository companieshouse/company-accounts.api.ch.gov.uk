package uk.gov.companieshouse.api.accounts.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.model.entity.CurrentPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.repository.CurrentPeriodRepository;
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
    private CurrentPeriodTransformer currentPeriodTransformer;

    @InjectMocks
    private CurrentPeriodServiceImpl currentPeriodService;

    @BeforeEach
    public void setUp() {
        when(currentPeriodTransformer.transform(currentPeriod))
                .thenReturn(createCurrentPeriodEntity);
    }

    @Test
    @DisplayName("Tests the successful creation of a currentPeriod resource")
    public void canCresteCurrentPeriod() {
        CurrentPeriod result = currentPeriodService.save(currentPeriod);
        assertNotNull(result);
        assertEquals(currentPeriod, result);

    }
}