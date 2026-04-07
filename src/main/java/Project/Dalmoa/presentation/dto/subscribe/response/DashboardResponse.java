package Project.Dalmoa.presentation.dto.subscribe.response;

import Project.Dalmoa.domain.subscribe.SubCategory;
import java.util.Map;

public record DashboardResponse(
        double totalAmount,
        Map<SubCategory, Double> categorySums
) {
}
