package Project.Dalmoa.presentation.dto.subscribe.response;

import Project.Dalmoa.domain.subscribe.SubCategory;
import java.util.Map;

public record DashboardResponse(
        double totalAmount,
        double previousTotalAmount,
        double diffAmount,
        double diffPercent,
        Map<SubCategory, Double> categorySums
) {
}
