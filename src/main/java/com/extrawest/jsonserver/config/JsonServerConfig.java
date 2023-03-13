package com.extrawest.jsonserver.config;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Objects;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import eu.chargetime.ocpp.feature.profile.Profile;
import eu.chargetime.ocpp.feature.profile.ServerCoreEventHandler;
import eu.chargetime.ocpp.feature.profile.ServerCoreProfile;
import com.extrawest.jsonserver.repository.BddDataRepository;
import com.extrawest.jsonserver.ws.JsonWsServer;
import eu.chargetime.ocpp.feature.profile.ServerFirmwareManagementEventHandler;
import eu.chargetime.ocpp.feature.profile.ServerFirmwareManagementProfile;
import eu.chargetime.ocpp.feature.profile.ServerLocalAuthListProfile;
import eu.chargetime.ocpp.feature.profile.ServerRemoteTriggerProfile;
import eu.chargetime.ocpp.feature.profile.ServerReservationProfile;
import eu.chargetime.ocpp.feature.profile.ServerSmartChargingProfile;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
@EnableConfigurationProperties
public class JsonServerConfig {
    @Value("${host.address:}")
    @Getter
    @Setter
    private String hostAddress;
    @Value("${server.port}")
    @Getter private Integer serverPort;

    @Bean
    public ObjectMapper objectMapper() {
        Jackson2ObjectMapperBuilder builder = jacksonBuilder();
        ObjectMapper objectMapper = builder.build();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, true);
        return objectMapper;
    }

    private Jackson2ObjectMapperBuilder jacksonBuilder() {
        return new Jackson2ObjectMapperBuilder();
    }

    @Bean
    public JsonWsServer jsonServer(ServerCoreProfile coreProfile, List<Profile> profiles,
                                   BddDataRepository storage) throws UnknownHostException {
        JsonWsServer jsonServer = new JsonWsServer(coreProfile, storage);
        profiles.forEach(jsonServer::addFeatureProfile);

        if (Objects.isNull(hostAddress) || hostAddress.isBlank()) {
            hostAddress = Inet4Address.getLocalHost().getHostAddress();
        }

        return jsonServer;
    }

    @Bean
    public ServerCoreProfile serverCoreProfile(ServerCoreEventHandler handler) {
        return new ServerCoreProfile(handler);
    }

    @Bean
    public ServerRemoteTriggerProfile serverRemoteTriggerProfile() {
        return new ServerRemoteTriggerProfile();
    }

    @Bean
    public ServerFirmwareManagementProfile serverFirmwareManagementProfile(ServerFirmwareManagementEventHandler handler) {
        return new ServerFirmwareManagementProfile(handler);
    }

    @Bean
    public ServerReservationProfile serverReservationProfile() {
        return new ServerReservationProfile();
    }

    @Bean
    public ServerSmartChargingProfile serverSmartChargingProfile() {
        return new ServerSmartChargingProfile();
    }

    @Bean
    public ServerLocalAuthListProfile serverLocalAuthListProfile() {
        return new ServerLocalAuthListProfile();
    }

}
