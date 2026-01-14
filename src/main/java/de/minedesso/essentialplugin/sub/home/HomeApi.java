package de.minedesso.essentialplugin.sub.home;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class HomeApi {

    private static HomeApi instance;

    public static HomeApi getInstance() {
        if (instance == null) {
            instance = new HomeApi();
        }
        return instance;
    }

    private HomeApi() {
    }

    public void saveHome(HomeDto homeDto) {
    }

    public void deleteHome(HomeDto homeDto) {
    }

    public Optional<HomeDto> fetchHomeByNameAndOwnerUuid(String name, UUID ownerUuid) {
        return null;
    }

    public Optional<List<HomeDto>> fetchHomesByOwnerUuid(UUID ownerUuid) {
        return null;
    }

}
