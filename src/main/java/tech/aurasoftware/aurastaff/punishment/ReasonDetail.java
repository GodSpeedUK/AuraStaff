package tech.aurasoftware.aurastaff.punishment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReasonDetail {

    private final String reason;
    private final long duration;
    private final boolean silent;


}
