package org.dows.rade.git.dto;

import lombok.Data;
import org.dows.rade.git.CmdParam;

/*@NoArgsConstructor
@AllArgsConstructor*/
@Data
public class BaseCmdParam implements CmdParam {
    int index;
    String sourceBranch;
    String targetBranch;
}
