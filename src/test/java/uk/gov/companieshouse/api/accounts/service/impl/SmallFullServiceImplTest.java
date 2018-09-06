package uk.gov.companieshouse.api.accounts.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.security.MessageDigest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.entity.SmallFullEntity;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.SmallFullTransformer;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class SmallFullServiceImplTest {

    @Mock
    private SmallFull smallFull;

    @Mock
    private Transaction transaction;

    @Mock
    private SmallFullEntity smallFullEntity;

    @Mock
    private MessageDigest messageDigest;

    @Mock
    private SmallFullTransformer smallFullTransformer;

    @InjectMocks
    private SmallFullServiceImpl smallFullService;

    @BeforeEach
    public void setUp() {
        smallFullService.setMessageDigest(messageDigest);
        when(smallFullTransformer.transform(smallFull)).thenReturn(smallFullEntity);
        byte[] b = {12, 10, 56, 120, 13, 15};
        when(messageDigest.digest(any())).thenReturn(b);
    }

    @Test
    @DisplayName("Tests the successful creation of a smallFull resource")
    public void canCreateAccount() throws DataException {
        ResponseObject<SmallFull> result = smallFullService.create(smallFull, transaction, "", "");
        assertNotNull(result);
        assertEquals(smallFull, result.getData());

    }
}
