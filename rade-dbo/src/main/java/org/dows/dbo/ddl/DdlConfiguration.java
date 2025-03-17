package org.dows.dbo.ddl;

import org.dows.dbo.ddl.store.DdlBuildrRepository;
import org.dows.dbo.ddl.store.DdlWrappersRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DdlConfiguration {
    @Bean
    public DdlBuildrRepository ddlBuildrRepository() {
        return new DdlBuildrRepository();
    }

    @Bean
    public DdlWrappersRepository ddlWrappersRepository() {
        return new DdlWrappersRepository();
    }
}
