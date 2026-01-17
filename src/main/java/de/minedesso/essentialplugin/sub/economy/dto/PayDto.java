package de.minedesso.essentialplugin.sub.economy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
public class PayDto {

    private UUID sender;
    private UUID receiver;
    private double amount;
    private TransactionContext context;

}
