package com.exalttech.trex.ui.controllers.dashboard.tabs.charts;

import javafx.beans.property.IntegerProperty;
import javafx.scene.chart.XYChart;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.exalttech.trex.ui.models.stats.latency.MaxLatencyPoint;
import com.exalttech.trex.ui.views.statistics.StatsLoader;
import com.exalttech.trex.util.ArrayHistory;


public class DashboardTabChartsMaxLatency extends DashboardTabChartsLine {
    public DashboardTabChartsMaxLatency(IntegerProperty interval) {
        super(interval);
        getYAxis().setLabel("Max Latency (ms)");
    }

    public void update(Set<Integer> visiblePorts, Set<String> visibleStreams, int streamsCount) {
        getChart().getData().clear();

        if (visibleStreams != null && visibleStreams.isEmpty()) {
            return;
        }

        StatsLoader statsLoader = StatsLoader.getInstance();
        Map<String, ArrayHistory<MaxLatencyPoint>> streams = statsLoader.getMaxLatencyHistory();
        final double maxLatencyLastTime = statsLoader.getMaxLatencyLastTime();
        List<XYChart.Series<Number, Number>> seriesList = new LinkedList<>();
        AtomicInteger streamIndex = new AtomicInteger(0);
        streams.forEach((String stream, ArrayHistory<MaxLatencyPoint> history) -> {
            if (streamIndex.get() >= streamsCount || (visibleStreams != null && !visibleStreams.contains(stream))) {
                return;
            }

            XYChart.Series series = new XYChart.Series();
            series.setName(stream);
            history.forEach((MaxLatencyPoint point) -> {
                series.getData().add(new XYChart.Data<>(point.getTime() - maxLatencyLastTime, point.getValue()));
            });

            seriesList.add(series);

            streamIndex.getAndAdd(1);
        });
        getChart().getData().clear();
        getChart().getData().addAll(seriesList);
    }
}
