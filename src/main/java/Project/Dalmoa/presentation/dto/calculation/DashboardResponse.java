package Project.Dalmoa.presentation.dto.calculation;

import Project.Dalmoa.domain.subscribe.SubType;
import Project.Dalmoa.domain.subscribe.Subscribe;
import Project.Dalmoa.presentation.dto.subscribe.SubscribeDetailResponse;

import java.util.List;
import java.util.Map;

public record DashboardResponse(
        double totalAmount,
        Map<SubType, Double> categorySums,
        List<SubscribeDetailResponse> subscriptions
) {
    public static DashboardResponse of(double total, Map<SubType, Double> sums, List<Subscribe> list) {
        return new DashboardResponse(
                total,
                sums,
                list.stream().map(SubscribeDetailResponse::from).toList()
        );
    }
}
