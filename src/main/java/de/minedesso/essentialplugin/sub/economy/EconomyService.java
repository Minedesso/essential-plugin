package de.minedesso.essentialplugin.sub.economy;

import de.minedesso.essentialplugin.EssentialPlugin;
import de.minedesso.essentialplugin.exception.InvalidAmountException;
import de.minedesso.essentialplugin.exception.InvalidBeneficiaryException;
import de.minedesso.essentialplugin.exception.MinecraftPlayerNotFoundException;
import de.minedesso.essentialplugin.sub.economy.cmd.balance.BalanceBaseCommand;
import de.minedesso.essentialplugin.sub.economy.cmd.pay.PayCommand;
import de.minedesso.essentialplugin.sub.economy.dto.in.balance.BalanceDto;
import de.minedesso.essentialplugin.sub.economy.dto.out.balance.SetMoneyFlowBalanceOfflineCommand;
import de.minedesso.essentialplugin.sub.economy.dto.out.balance.SetMoneyFlowBalanceOnlineCommand;
import de.minedesso.essentialplugin.sub.economy.dto.out.pay.PayDto;
import de.minedesso.essentialplugin.sub.economy.dto.out.pay.PayOfflineDto;
import de.minedesso.essentialplugin.sub.economy.dto.transaction.TransactionContext;
import de.minedesso.essentialplugin.sub.economy.dto.transaction.TransactionResponse;
import de.minedesso.essentialplugin.sub.economy.dto.transaction.TransactionSource;
import de.minedesso.essentialplugin.sub.economy.dto.transaction.TransactionType;
import de.minedesso.essentialplugin.sub.tpa.cmd.sub.*;
import de.minedesso.essentialplugin.util.HandleCooldownUtil;
import de.minedesso.essentialplugin.util.Message;
import de.minedesso.essentialplugin.util.Permission;

import java.util.Optional;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EconomyService {

    private static EconomyService instance;
    private final EconomyApi economyApi;
    private final PlayerApi playerApi;
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
        playerApi = PlayerApi.getInstance();
        handleCooldownUtil = new HandleCooldownUtil(COOLDOWN_SECONDS);
    }

    public void pay(Player player, String receiverName, String amountString) {
        if(player.getName().equalsIgnoreCase(receiverName))
            throw new InvalidBeneficiaryException("You cannot pay yourself.");


        Player receiver = Bukkit.getPlayer(receiverName);
        int amount = validateAmount(amountString);

        TransactionContext context = new TransactionContext(TransactionType.PAY,
                TransactionSource.PLAYER);

        TransactionResponse payResponse;
        if(receiver == null) {
            PayOfflineDto payOfflineDto = new PayOfflineDto(player.getUniqueId(),
                    receiverName, amount, context);
            payResponse = economyApi.payOfflinePlayer(payOfflineDto);
        } else {
            PayDto payDto = new PayDto(player.getUniqueId(),
                    receiver.getUniqueId(), amount, context);
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

    private double validateMoneyAmount(String amountString) {
        try {
            double amount = Double.parseDouble(amountString);

            if (amount < 0) {
                throw new InvalidAmountException("Amount must be zero or greater.");
            }

            return amount;
        } catch (NumberFormatException e) {
            throw new InvalidAmountException("Invalid amount: " + amountString);
        }
    }

    public void showBalance(Player player) {
        if(!player.hasPermission(Permission.MONEY_VIEW.perm)) {
            player.sendMessage(Message.NO_PERMISSION.message);
            return;
        }

        BalanceDto balance = economyApi.getBalance(player.getUniqueId());
        if(balance == null) {
            player.sendMessage(Message.PLAYER_NOT_FOUND.message);
            return;
        }

        player.sendMessage(Message.PREFIX.message + "&7Your balance: &e" + formatAmount(balance.getBalance()) + " €");
    }

    public void showBalance(Player player, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        UUID targetUuid;

        if(target != null) {
            targetUuid = target.getUniqueId();
        } else {
            Optional<UUID> resolvedUuid = playerApi.resolvePlayerUuid(targetName);
            if(resolvedUuid.isEmpty()) {
                player.sendMessage(Message.PLAYER_NOT_FOUND.message);
                return;
            }
            targetUuid = resolvedUuid.get();
        }

        // Check permission - self check needs MONEY_VIEW, others need MONEY_VIEW_OTHERS
        boolean viewingSelf = player.getUniqueId().equals(targetUuid);
        if(!viewingSelf && !player.hasPermission(Permission.MONEY_VIEW_OTHERS.perm)) {
            player.sendMessage(Message.NO_PERMISSION.message);
            return;
        }

        BalanceDto balance = economyApi.getBalance(targetUuid);
        if(balance == null) {
            player.sendMessage(Message.PLAYER_NOT_FOUND.message);
            return;
        }

        player.sendMessage(Message.PREFIX.message + "&7Balance of &e" + (target != null ? target.getName() : targetName) + "&7: &e" + formatAmount(balance.getBalance()) + " €");
    }

    public void setBalance(Player player, String targetName, String amountString) {
        if(!player.hasPermission(Permission.MONEY_ADMIN.perm)) {
            player.sendMessage(Message.NO_PERMISSION.message);
            return;
        }

        double amount = validateMoneyAmount(amountString);

        Player target = Bukkit.getPlayer(targetName);
        UUID targetUuid;

        if(target != null) {
            targetUuid = target.getUniqueId();
            SetMoneyFlowBalanceOnlineCommand command = new SetMoneyFlowBalanceOnlineCommand(
                    player.getUniqueId(),
                    targetUuid,
                    amount,
                    new TransactionContext(TransactionType.SET, TransactionSource.ADMIN_COMMAND)
            );
            try {
                TransactionResponse response = economyApi.setBalanceOnline(command);
                if(response == TransactionResponse.SUCCESS) {
                    player.sendMessage(Message.PREFIX.message + "&aSet balance of &e" + target.getName() + "&a to &e" + formatAmount(amount) + " €&a.");
                } else if(response == TransactionResponse.RECEIVER_NOT_FOUND) {
                    player.sendMessage(Message.PLAYER_NOT_FOUND.message);
                } else {
                    player.sendMessage(Message.ERROR.message + "Failed to set balance.");
                }
            } catch (MinecraftPlayerNotFoundException e) {
                player.sendMessage(Message.PLAYER_NOT_FOUND.message);
            } catch (Exception e) {
                player.sendMessage(Message.ERROR.message + "An internal error occurred. Please try again.");
            }
        } else {
            Optional<UUID> resolvedUuid = playerApi.resolvePlayerUuid(targetName);
            if(resolvedUuid.isEmpty()) {
                player.sendMessage(Message.PLAYER_NOT_FOUND.message);
                return;
            }

            SetMoneyFlowBalanceOfflineCommand command = new SetMoneyFlowBalanceOfflineCommand(
                    player.getUniqueId(),
                    targetName,
                    amount,
                    new TransactionContext(TransactionType.SET, TransactionSource.ADMIN_COMMAND)
            );
            try {
                TransactionResponse response = economyApi.setBalanceOffline(command);
                if(response == TransactionResponse.SUCCESS) {
                    player.sendMessage(Message.PREFIX.message + "&aSet balance of &e" + targetName + "&a to &e" + formatAmount(amount) + " €&a.");
                } else if(response == TransactionResponse.RECEIVER_NOT_FOUND) {
                    player.sendMessage(Message.PLAYER_NOT_FOUND.message);
                } else {
                    player.sendMessage(Message.ERROR.message + "Failed to set balance.");
                }
            } catch (MinecraftPlayerNotFoundException e) {
                player.sendMessage(Message.PLAYER_NOT_FOUND.message);
            } catch (Exception e) {
                player.sendMessage(Message.ERROR.message + "An internal error occurred. Please try again.");
            }
        }
    }

    private String formatAmount(double amount) {
        if(amount >= 1_000_000) {
            return String.format("%.2fM €", amount / 1_000_000);
        } else if(amount >= 1_000) {
            return String.format("%.2fk €", amount / 1_000);
        } else {
            return String.format("%.2f €", amount);
        }
    }

    private void initializeCommands() {
        EssentialPlugin essentialPlugin = EssentialPlugin.getInstance();

        essentialPlugin.getCommand("pay").setExecutor(new PayCommand());
        essentialPlugin.getCommand("money").setExecutor(new BalanceBaseCommand());
    }
}
