package ru.vladislav.cost_analysis_nau.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vladislav.cost_analysis_nau.dto.TransferForm;
import ru.vladislav.cost_analysis_nau.entity.Account;
import ru.vladislav.cost_analysis_nau.entity.Transfer;
import ru.vladislav.cost_analysis_nau.entity.User;
import ru.vladislav.cost_analysis_nau.repository.AccountRepository;
import ru.vladislav.cost_analysis_nau.repository.TransferRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransferService {

    private static final Logger log = LoggerFactory.getLogger(TransferService.class);
    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;

    public List<Transfer> getTransfersByUser(User user) {
        return transferRepository.findByUserId(user.getId());
    }

    @Transactional
    public Transfer create(TransferForm form, User user) {
        if (form.getFromAccountId().equals(form.getToAccountId())) {
            throw new RuntimeException("Source and destination accounts must be different");
        }

        Account from = accountRepository.findById(form.getFromAccountId())
                .filter(a -> a.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Source account not found"));

        Account to = accountRepository.findById(form.getToAccountId())
                .filter(a -> a.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Destination account not found"));

        if (from.getBalance() < form.getAmount()) {
            throw new RuntimeException("Insufficient balance on source account");
        }

        from.setBalance(from.getBalance() - form.getAmount());
        to.setBalance(to.getBalance() + form.getAmount());
        accountRepository.save(from);
        accountRepository.save(to);

        Transfer transfer = new Transfer();
        transfer.setFrom(from);
        transfer.setTo(to);
        transfer.setAmount(form.getAmount());
        transfer.setDescription(form.getDescription());
        transfer.setCreatedAt(LocalDateTime.now());

        log.info("Transfer of {} from account {} to account {} by user '{}'",
                form.getAmount(), from.getId(), to.getId(), user.getLogin());
        return transferRepository.save(transfer);
    }
}
