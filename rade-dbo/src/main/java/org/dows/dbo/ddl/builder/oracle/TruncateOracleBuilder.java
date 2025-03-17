package org.dows.dbo.ddl.builder.oracle;

import org.dows.dbo.ddl.actuator.ExecuteTruncate;
import org.dows.dbo.ddl.actuator.ibuilder.TruncateBuilder;
import org.dows.dbo.ddl.actuator.sign.OracleSign;

public class TruncateOracleBuilder extends ExecuteTruncate<TruncateOracleBuilder> implements OracleSign, TruncateBuilder<TruncateOracleBuilder> {


    @Override
    public TruncateOracleBuilder truncate() {
        return null;
    }
}
