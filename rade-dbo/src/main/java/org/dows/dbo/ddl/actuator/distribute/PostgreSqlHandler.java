package org.dows.dbo.ddl.actuator.distribute;

import org.dows.dbo.ddl.actuator.sign.PostgreSqlSign;

public class PostgreSqlHandler extends AbstractDbHandler {
    @Override
    public boolean supports(Object source) {
        return source instanceof PostgreSqlSign;
    }

    @Override
    public String doHandler(Object param) {
        return null;
    }
}
