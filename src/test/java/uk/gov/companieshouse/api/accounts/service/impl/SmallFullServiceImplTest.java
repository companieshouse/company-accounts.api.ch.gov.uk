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
import uk.gov.companieshouse.api.accounts.model.entity.SmallFullEntity;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.repository.SmallFullRepository;
import uk.gov.companieshouse.api.accounts.transformer.SmallFullTransformer;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class SmallFullServiceImplTest {

        @Mock
        private SmallFull smallFull;

        @Mock
        private SmallFullEntity createdSmallFullEntity;

        @Mock
        private SmallFullRepository smallFullRepository;

        @Mock
        private SmallFullTransformer smallFullTransformer;

        @InjectMocks
        private SmallFullServiceImpl smallFullService;

        @BeforeEach
        public void setUp() {
            when(smallFullTransformer.transform(smallFull)).thenReturn(createdSmallFullEntity);
        }

        @Test
        @DisplayName("Tests the successful creation of a smallFull resource")
        public void canCreateAccount() {
            SmallFull result = smallFullService.save(smallFull);
            assertNotNull(result);
            assertEquals(smallFull, result);

        }
    }
