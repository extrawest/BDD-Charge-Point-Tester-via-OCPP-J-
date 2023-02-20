package com.extrawest.jsonserver.config;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Objects;
import eu.chargetime.ocpp.feature.profile.Profile;
import eu.chargetime.ocpp.feature.profile.ServerCoreProfile;
import com.extrawest.jsonserver.repository.BddDataRepository;
import com.extrawest.jsonserver.ws.JsonWsServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonServerConfig {

    @Bean
    public JsonWsServer jsonServer(ServerCoreProfile coreProfile,
                                   List<Profile> profiles,
                                   ApplicationConfiguration configuration,
                                   BddDataRepository storage) throws UnknownHostException {
        JsonWsServer jsonServer = new JsonWsServer(coreProfile, storage);
        profiles.forEach(jsonServer::addFeatureProfile);

        String hostAddress = configuration.getHostAddress();
        if (Objects.isNull(hostAddress) || hostAddress.isBlank()) {
            configuration.setHostAddress(Inet4Address.getLocalHost().getHostAddress());
        }

        return jsonServer;
    }

}
