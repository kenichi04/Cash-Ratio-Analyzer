package com.example.cash_ratio_analyzer.presentation.controller;

import com.example.cash_ratio_analyzer.application.service.metadata.DocumentMetadataService;
import com.example.cash_ratio_analyzer.application.service.FinancialDocumentScenarioService;
import com.example.cash_ratio_analyzer.domain.model.Company;
import com.example.cash_ratio_analyzer.domain.model.FinancialDocument;
import com.example.cash_ratio_analyzer.application.service.DocumentMetadataScenarioService;
import com.example.cash_ratio_analyzer.application.service.financial.FinancialDocumentService;
import com.example.cash_ratio_analyzer.domain.model.DocumentMetadata;
import com.example.cash_ratio_analyzer.presentation.controller.response.DocumentMetadataPostResponse;
import com.example.cash_ratio_analyzer.presentation.controller.response.FinancialDocumentPostResponse;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class EdinetController {

    private final DocumentMetadataScenarioService documentMetadataScenarioService;

    private final FinancialDocumentScenarioService financialDocumentScenarioService;

    private final FinancialDocumentService financialDocumentService;

    private final DocumentMetadataService documentMetadataService;

    public EdinetController(DocumentMetadataScenarioService documentMetadataScenarioService, FinancialDocumentScenarioService financialDocumentScenarioService, FinancialDocumentService financialDocumentService, DocumentMetadataService documentMetadataService) {
        this.documentMetadataScenarioService = documentMetadataScenarioService;
        this.financialDocumentScenarioService = financialDocumentScenarioService;
        this.financialDocumentService = financialDocumentService;
        this.documentMetadataService = documentMetadataService;
    }

    @PostMapping("/metadata/fetch-and-save")
    public DocumentMetadataPostResponse fetchAndSaveDocumentMetadata(@RequestParam LocalDate fromDate) {
        var documentIds = documentMetadataScenarioService.fetchAndSaveDocumentMetadata(fromDate);

        var documentIdList = documentIds.stream()
                .map(documentId -> documentId.value())
                .toList();
        return new DocumentMetadataPostResponse(documentIdList);
    }

    @PostMapping("/{documentId}/fetch-and-save")
    public FinancialDocumentPostResponse fetchAndSaveFinancialData(@PathVariable String documentId) {
        var documentIdModel = financialDocumentScenarioService.fetchAndSaveFinancialData(documentId);
        return new FinancialDocumentPostResponse(documentIdModel.value());
    }

    // TODO ここから下の処理はEdinet使用しないので別のクラスに移動する
    @GetMapping("/{documentId}")
    public FinancialDocument getFinancialDocument(@PathVariable String documentId) {
        // TODO レスポンスモデルを作成する
        return financialDocumentService.getFinancialDocument(documentId);
    }

    @GetMapping("/unprocessedMetadata")
    public List<DocumentMetadata> getUnprocessedMetadata() {
        // TODO レスポンスモデルを作成する
        return documentMetadataService.getUnprocessedMetadata();
    }

    @GetMapping("/companies")
    public List<Company> getCompanies() {
        // TODO レスポンスモデルを作成する
        return documentMetadataService.getCompanies();
    }
}
