package jsf.classes;

import entity.ReportField;
import entity.ReportFieldGroup;
import java.util.ArrayList;
import java.util.List;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.ChartModel;
import org.primefaces.model.charts.axes.cartesian.CartesianScaleLabel;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.optionconfig.legend.Legend;
import org.primefaces.model.charts.optionconfig.legend.LegendLabel;
import org.primefaces.model.charts.pie.PieChartDataSet;
import org.primefaces.model.charts.pie.PieChartModel;
import util.enumeration.ReportFieldType;

public class ReportFieldWrapper {

    private ReportField reportField;

    private ChartModel chart;

    private static final String[] bgColors = {
        "rgba(255, 99, 132, 0.2)",
        "rgba(255, 159, 64, 0.2)",
        "rgba(255, 205, 86, 0.2)",
        "rgba(75, 192, 192, 0.2)",
        "rgba(54, 162, 235, 0.2)",
        "rgba(153, 102, 255, 0.2)",
        "rgba(201, 203, 207, 0.2)"
    };

    private static final String[] borderColors = {
        "rgb(255, 99, 132)",
        "rgb(255, 159, 64)",
        "rgb(255, 205, 86)",
        "rgb(75, 192, 192)",
        "rgb(54, 162, 235)",
        "rgb(153, 102, 255)",
        "rgb(201, 203, 207)"
    };

    public ReportFieldWrapper() {
    }

    public ReportFieldWrapper(ReportField reportField) {
        this();
        this.reportField = reportField;
    }
    
    public ReportFieldWrapper(ReportFieldWrapper another) {
        this();
        this.reportField = new ReportField(another.getReportField());
        createChart();
    }

    public ReportField getReportField() {
        return reportField;
    }

    public void setReportField(ReportField reportField) {
        this.reportField = reportField;
    }

    public ChartModel getChart() {
        return chart;
    }

    public void setChart(ChartModel chart) {
        this.chart = chart;
    }

    public void createChart() {
        if (reportField.getType() == ReportFieldType.PIE) {
            chart = createPieModel();
        } else if (reportField.getType() == ReportFieldType.BAR) {
            chart = createBarModel();
        } else if (reportField.getType() == ReportFieldType.LINE) {

        }
    }

    private PieChartModel createPieModel() {
        PieChartModel pieModel = new PieChartModel();
        List<ReportFieldGroup> groups = reportField.getReportFieldGroups();
        ChartData data = new ChartData();

        PieChartDataSet dataSet = new PieChartDataSet();
        List<Number> values = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<String> borderColors = new ArrayList<>();
        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i).getQuantity() > 0) {
                values.add(groups.get(i).getQuantity());
                labels.add(groups.get(i).getName());
                borderColors.add(this.borderColors[i % this.borderColors.length]);
            }
        }
        dataSet.setData(values);

        dataSet.setBackgroundColor(borderColors);

        data.addChartDataSet(dataSet);
        data.setLabels(labels);

        pieModel.setData(data);
        return pieModel;
    }

    private BarChartModel createBarModel() {
        BarChartModel barModel = new BarChartModel();
        List<ReportFieldGroup> groups = reportField.getReportFieldGroups();
        ChartData data = new ChartData();

        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel("Dataset");

        List<Number> values = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<String> bgColors = new ArrayList<>();
        List<String> borderColors = new ArrayList<>();
        for (int i = 0; i < groups.size(); i++) {
            values.add(groups.get(i).getQuantity());
            labels.add(groups.get(i).getName());
            bgColors.add(this.bgColors[i % this.bgColors.length]);
            borderColors.add(this.borderColors[i % this.borderColors.length]);
        }
        barDataSet.setData(values);
        barDataSet.setBackgroundColor(bgColors);
        barDataSet.setBorderColor(borderColors);
        barDataSet.setBorderWidth(1);
        data.addChartDataSet(barDataSet);
        data.setLabels(labels);
        barModel.setData(data);

        //Options
        BarChartOptions options = new BarChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        linearAxes.setOffset(true);
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        ticks.setStepSize(1);
        ticks.setBeginAtZero(true);
        linearAxes.setTicks(ticks);
        linearAxes.setScaleLabel(new CartesianScaleLabel());
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);

        Legend legend = new Legend();
        // TO ENABLE LEGEND DISPLAY
        legend.setDisplay(false);
        legend.setPosition("top");
        LegendLabel legendLabels = new LegendLabel();
        legendLabels.setFontStyle("bold");
        legendLabels.setFontColor("#2980B9");
        legend.setLabels(legendLabels);
        options.setLegend(legend);

        barModel.setOptions(options);
        return barModel;
    }

}
