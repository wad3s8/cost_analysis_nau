package ru.vladislav.cost_analysis_nau.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vladislav.cost_analysis_nau.dto.AccountForm;
import ru.vladislav.cost_analysis_nau.entity.Account;
import ru.vladislav.cost_analysis_nau.entity.User;
import ru.vladislav.cost_analysis_nau.repository.AccountRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);
    private final AccountRepository accountRepository;

    public List<Account> getAccountsByUser(User user) {
        return accountRepository.findByUserId(user.getId());
    }

    public Account getAccountByIdAndUser(Long id, User user) {
        return accountRepository.findById(id)
                .filter(a -> a.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    @Transactional
    public Account create(AccountForm form, User user) {
        Account account = new Account();
        account.setName(form.getName());
        account.setBalance(form.getBalance() != null ? form.getBalance() : 0.0);
        account.setDescription(form.getDescription());
        account.setUser(user);
        log.info("Creating account '{}' for user '{}'", form.getName(), user.getLogin());
        return accountRepository.save(account);
    }

    @Transactional
    public Account update(Long id, AccountForm form, User user) {
        Account account = getAccountByIdAndUser(id, user);
        account.setName(form.getName());
        account.setDescription(form.getDescription());
        log.info("Updating account {} for user '{}'", id, user.getLogin());
        return accountRepository.save(account);
    }

    @Transactional
    public void delete(Long id, User user) {
        Account account = getAccountByIdAndUser(id, user);
        log.info("Deleting account {} for user '{}'", id, user.getLogin());
        accountRepository.delete(account);
    }

    public double getTotalBalance(User user) {
        return accountRepository.findByUserId(user.getId()).stream()
                .mapToDouble(Account::getBalance)
                .sum();
    }
}
