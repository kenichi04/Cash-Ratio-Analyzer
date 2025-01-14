package com.example.cash_ratio_analyzer_test.application.service;

import com.example.cash_ratio_analyzer_test.domain.model.FinancialData;
import com.example.cash_ratio_analyzer_test.domain.model.FinancialDocument;
import com.example.cash_ratio_analyzer_test.domain.repository.ICompanyRepository;
import com.example.cash_ratio_analyzer_test.domain.repository.IFinancialDocumentMetadataRepository;
import com.example.cash_ratio_analyzer_test.domain.repository.IFinancialDocumentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FinancialDocumentService {

    private final IFinancialDocumentMetadataRepository financialDocumentMetadataRepository;

    private final IFinancialDocumentRepository financialDocumentRepository;

    private final ICompanyRepository companyRepository;

    public FinancialDocumentService(IFinancialDocumentRepository financialDocumentRepository, IFinancialDocumentMetadataRepository financialDocumentMetadataRepository, IFinancialDocumentRepository financialDocumentRepository1, ICompanyRepository companyRepository) {
        this.financialDocumentMetadataRepository = financialDocumentMetadataRepository;
        this.financialDocumentRepository = financialDocumentRepository1;
        this.companyRepository = companyRepository;
    }

    public FinancialDocument getFinancialDocument(String documentId) {
        return financialDocumentRepository.findByDocumentId(documentId);
    }

    // TODO transactionalアノテーションを付与する
    // 書類一覧APIレスポンスからdocument作成する場合は、dataが取得できないため、documentIdのみで作成する
    // TODO 処理見直し. 書類一覧APIではdocumentではなく、documentMetadataを作成する方針に変更
    public void create(String companyId, String documentId) {
        // TODO 直接companyRepository使うか？companyServiceでラップするか？
        var company = companyRepository.findByCompanyId(companyId);
        // 仮実装, ここでcompanyIdを使用してcompanyの存在チェック
        if (company == null) {
            throw new RuntimeException("company is not found");
        }
        // companyとdocumentは一対多の関係
        // ここの処理は検討
        var financialDocument = new FinancialDocument(documentId);
        company.addDocument(financialDocument);
        companyRepository.save(company);
    }

    // TODO transactionalアノテーションを付与する
    // 書類取得APIレスポンスからの処理を想定。documentは上で作成済にするか、新規作成するかは要検討（dataなしのdocument作成してもよいのか）
    public void saveFinancialData(String documentId, List<FinancialData> financialDataList) {
        // financialDataは新規作成のみ、更新は不要の想定
        var financialDocument = new FinancialDocument(documentId);
        financialDocument.createData(financialDataList);
        financialDocumentRepository.save(financialDocument);

        // メタデータを処理済に更新
        updateMetadataProcessedStatus(documentId);
    }

    private void updateMetadataProcessedStatus(String documentId) {
        var metadata = financialDocumentMetadataRepository.findByDocumentId(documentId);
        metadata.updateProcessedStatus();
        financialDocumentMetadataRepository.save(metadata);
    }
}
