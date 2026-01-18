package de.minedesso.essentialplugin.sub.economy;

import de.minedesso.essentialplugin.EssentialPlugin;
import de.minedesso.essentialplugin.exception.InvalidAmountException;
import de.minedesso.essentialplugin.exception.InvalidBeneficiaryException;
import de.minedesso.essentialplugin.sub.economy.cmd.PayCommand;
import de.minedesso.essentialplugin.sub.economy.dto.*;
import de.minedesso.essentialplugin.util.HandleCooldownUtil;
import de.minedesso.essentialplugin.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EconomyService {

    private static EconomyService instance;
    private final EconomyApi economyApi;
    private final HandleCooldownUtil handleCooldownUtil;

    private static final int COOLDOWN_SECONDS = 5;

    public static EconomyService getInstance() {
        if (instance == null) {
            instance = new EconomyService();
        }
        return instance;
    }

    private EconomyService() {
        initializeCommands();
        economyApi = EconomyApi.getInstance();
        handleCooldownUtil = new HandleCooldownUtil(COOLDOWN_SECONDS);
    }

    public void pay(Player player, String receiverName, String amountString) {
        if(player.getName().equalsIgnoreCase(receiverName))
            throw new InvalidBeneficiaryException("You cannot pay yourself.");


        Player receiver = Bukkit.getPlayer(receiverName);
        int amount = validateAmount(amountString);

        TransactionContext context = new TransactionContext(TransactionType.PAY, TransactionSource.PLAYER);
        PayDto payDto = new PayDto(player.getUniqueId(), null, amount, context);

        PayResponse payResponse;
        if(receiver == null) {
            payResponse = economyApi.payOfflinePlayer(payDto, receiverName);
        } else {
            payDto.setReceiver(receiver.getUniqueId());
            payResponse = economyApi.pay(payDto);
        }

        switch (payResponse) {
            case SUCCESS -> {
                player.sendMessage(Message.PREFIX.message + "&aYou have sent " + amount + " to " + receiverName);
                player.sendMessage(Message.PREFIX.message + "&c- " + amount + "€");
                if(receiver != null) {
                    receiver.sendMessage(Message.PREFIX.message + "&aYou have received " + amount + " from " + player.getName());
                    receiver.sendMessage(Message.PREFIX.message + "&a+ " + amount + "€");
                }

                handleCooldownUtil.handleCooldown(player.getUniqueId());
            }
            case INSUFFICIENT_FUNDS -> throw new InvalidAmountException("Insufficient funds to complete the transaction.");
            case RECEIVER_NOT_FOUND -> throw new InvalidBeneficiaryException("Receiver not found.");
            case ERROR -> player.sendMessage(Message.ERROR.message + "Something went wrong");
        }
    }

    private int validateAmount(String amountString) {
        try {
            int amount = Integer.parseInt(amountString);

            if (amount <= 0) {
                throw new InvalidAmountException("Amount must be greater than zero.");
            }

            return amount;
        } catch (NumberFormatException e) {
            throw new InvalidAmountException("Invalid amount: " + amountString);
        }
    }

    private void initializeCommands() {
        EssentialPlugin essentialPlugin = EssentialPlugin.getInstance();

        essentialPlugin.getCommand("pay").setExecutor(new PayCommand());
    }
}
