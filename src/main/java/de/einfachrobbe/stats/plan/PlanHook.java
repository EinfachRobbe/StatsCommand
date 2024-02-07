package de.einfachrobbe.stats.plan;

import com.djrapitops.plan.capability.CapabilityService;
import com.djrapitops.plan.query.QueryService;

import java.util.Optional;

public class PlanHook {

    public PlanHook() {
    }

    public Optional<PlanAccessor> hookIntoPlan() {
        if (!areAllCapabilitiesAvailable()) return Optional.empty();
        return Optional.ofNullable(createQueryAPIAccessor());
    }

    private boolean areAllCapabilitiesAvailable() {
        CapabilityService capabilities = CapabilityService.getInstance();
        return capabilities.hasCapability("QUERY_API");
    }

    private PlanAccessor createQueryAPIAccessor() {
        try {
            return new PlanAccessor(QueryService.getInstance());
        } catch (IllegalStateException planIsNotEnabled) {
            // Plan is not enabled, handle exception
            return null;
        }
    }
}