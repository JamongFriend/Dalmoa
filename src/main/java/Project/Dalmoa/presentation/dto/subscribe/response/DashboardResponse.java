package Project.Dalmoa.presentation.dto.subscribe.response;

import Project.Dalmoa.domain.subscribe.SubType;
import java.util.Map;

public record DashboardResponse(
        double totalAmount,
        Map<SubType, Double> categorySums
) {
}
