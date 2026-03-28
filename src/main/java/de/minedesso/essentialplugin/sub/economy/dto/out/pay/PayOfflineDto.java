package de.minedesso.essentialplugin.sub.economy.dto.out.pay;

import de.minedesso.essentialplugin.sub.economy.dto.transaction.TransactionContext;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
public class PayOfflineDto {

    private UUID sender;
    private String receiverName;
    private double amount;
    private TransactionContext context;

}
