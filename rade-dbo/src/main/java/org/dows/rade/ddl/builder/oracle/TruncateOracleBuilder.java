package org.dows.rade.ddl.builder.oracle;

import org.dows.rade.ddl.actuator.ExecuteTruncate;
import org.dows.rade.ddl.actuator.ibuilder.TruncateBuilder;
import org.dows.rade.ddl.actuator.sign.OracleSign;

public class TruncateOracleBuilder extends ExecuteTruncate<TruncateOracleBuilder> implements OracleSign, TruncateBuilder<TruncateOracleBuilder> {


    @Override
    public TruncateOracleBuilder truncate() {
        return null;
    }
}
