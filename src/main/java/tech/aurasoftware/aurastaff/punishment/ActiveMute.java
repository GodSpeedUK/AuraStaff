package tech.aurasoftware.aurastaff.punishment;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ActiveMute {

    private final int id;
    private final String judge;
    private final String reason;
    private final long duration;
    private final long time;

}
