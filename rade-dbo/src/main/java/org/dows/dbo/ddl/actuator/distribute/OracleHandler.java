package org.dows.dbo.ddl.actuator.distribute;

import org.dows.dbo.ddl.actuator.sign.OracleSign;

public class OracleHandler extends AbstractDbHandler {
    @Override
    public boolean supports(Object source) {
        return source instanceof OracleSign;
    }

    @Override
    public String doHandler(Object param) {
        return null;
    }
}
