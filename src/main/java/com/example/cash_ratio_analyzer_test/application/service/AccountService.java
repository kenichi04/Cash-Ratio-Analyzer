package com.example.cash_ratio_analyzer_test.application.service;

import com.example.cash_ratio_analyzer_test.domain.model.AccountMaster;
import com.example.cash_ratio_analyzer_test.domain.repository.IAccountMasterRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private final IAccountMasterRepository accountMasterRepository;

    public AccountService(IAccountMasterRepository accountMasterRepository) {
        this.accountMasterRepository = accountMasterRepository;
    }

    public List<AccountMaster> getAccounts() {
        return accountMasterRepository.findAll();
    }
}
