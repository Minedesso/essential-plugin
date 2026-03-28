package de.minedesso.essentialplugin.sub.economy.dto.out.balance;

import de.minedesso.essentialplugin.sub.economy.dto.transaction.TransactionContext;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class SetMoneyFlowBalanceOnlineCommand {
    private UUID adminUuid;
    private UUID targetUuid;
    private double newBalance;
    private TransactionContext transactionContext;
}
