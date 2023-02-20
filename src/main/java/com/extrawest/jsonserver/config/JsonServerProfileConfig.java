package com.extrawest.jsonserver.config;

import eu.chargetime.ocpp.feature.profile.ServerCoreEventHandler;
import eu.chargetime.ocpp.feature.profile.ServerCoreProfile;
import eu.chargetime.ocpp.feature.profile.ServerFirmwareManagementEventHandler;
import eu.chargetime.ocpp.feature.profile.ServerFirmwareManagementProfile;
import eu.chargetime.ocpp.feature.profile.ServerLocalAuthListProfile;
import eu.chargetime.ocpp.feature.profile.ServerRemoteTriggerProfile;
import eu.chargetime.ocpp.feature.profile.ServerReservationProfile;
import eu.chargetime.ocpp.feature.profile.ServerSmartChargingProfile;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonServerProfileConfig {

    @Bean
    public ServerCoreProfile createCore(ServerCoreEventHandler handler) {
        return new ServerCoreProfile(handler);
    }

    @Bean
    public ServerRemoteTriggerProfile createRemoteTrigger() {
        return new ServerRemoteTriggerProfile();
    }

    @Bean
    public ServerFirmwareManagementProfile createFirmwareManagement(ServerFirmwareManagementEventHandler handler) {
        return new ServerFirmwareManagementProfile(handler);
    }

    @Bean
    public ServerReservationProfile createServerReservationProfile() {
        return new ServerReservationProfile();
    }

    @Bean
    public ServerSmartChargingProfile createServerSmartChargingProfile() {
        return new ServerSmartChargingProfile();
    }

    @Bean
    public ServerLocalAuthListProfile createServerLocalAuthListProfile() {
        return new ServerLocalAuthListProfile();
    }

}
