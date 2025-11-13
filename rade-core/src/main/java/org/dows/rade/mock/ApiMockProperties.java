package org.dows.rade.mock;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Set;

@Data
@Component
@ConfigurationProperties(prefix = "rade.api.mock")
public class ApiMockProperties {
    private boolean enabled;
    private Set<String> packages;
}
