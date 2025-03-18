package com.example.cash_ratio_analyzer_test.application.service.financial;

import com.example.cash_ratio_analyzer_test.application.service.constants.XbrlConstants;
import com.example.cash_ratio_analyzer_test.application.service.dto.HeaderInfo;
import com.example.cash_ratio_analyzer_test.application.service.metadata.DocumentMetadataService;
import com.example.cash_ratio_analyzer_test.domain.enums.Balance;
import com.example.cash_ratio_analyzer_test.domain.enums.Currency;
import com.example.cash_ratio_analyzer_test.domain.enums.DisplayScale;
import com.example.cash_ratio_analyzer_test.domain.model.AccountMaster;
import com.example.cash_ratio_analyzer_test.domain.model.DocumentId;
import com.example.cash_ratio_analyzer_test.domain.model.FinancialData;
import com.example.cash_ratio_analyzer_test.domain.repository.IFinancialDocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinancialDocumentServiceTest {

    @Mock
    private DocumentMetadataService documentMetadataService;

    @Mock
    private IFinancialDocumentRepository financialDocumentRepository;

    @InjectMocks
    private FinancialDocumentService financialDocumentService;

    @BeforeEach
    void setUp() {}

    @Test
    void createFinancialDocument() {
        // given
        var documentId = "TEST0001";
        var headerInfo = createTestHeaderInfo();
        var financialDataList = List.of(createTestFinancialData());

        doNothing().when(documentMetadataService).updateMetadataProcessedStatus(any());
        doNothing().when(financialDocumentRepository).create(any());

        // when
        var result = financialDocumentService.createFinancialDocument(documentId, headerInfo, financialDataList);

        // then
        assertEquals(new DocumentId(documentId), result);

        verify(financialDocumentRepository, times(1)).create(any());
        verify(documentMetadataService, times(1)).updateMetadataProcessedStatus(any());
    }

    private HeaderInfo createTestHeaderInfo() {
        var deiInfo = Map.of(
                XbrlConstants.DEI_ATTRIBUTE_EDINET_CODE, "TEST01",
                XbrlConstants.DEI_ATTRIBUTE_DOCUMENT_TYPE, "テスト様式",
                XbrlConstants.DEI_ATTRIBUTE_CURRENT_PERIOD_END_DATE, "2024-02-29");
        return new HeaderInfo(
                deiInfo, Currency.JPY);
    }

    private FinancialData createTestFinancialData() {
        var account = new AccountMaster(
                1L, "CashAndDeposits",
                "現金預金", "Cash and deposits",
                Balance.DEBIT);

        return new FinancialData(
                account,
                "test-period",
                new BigDecimal("100"),
                DisplayScale.MILLION_YEN,
                Currency.JPY);
    }
}