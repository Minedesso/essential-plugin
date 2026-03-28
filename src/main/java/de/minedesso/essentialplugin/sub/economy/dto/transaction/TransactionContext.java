package de.minedesso.essentialplugin.sub.economy.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TransactionContext {

    private TransactionType type;
    private TransactionSource source;

}
