package br.com.dgimenes.smashbrostwitteranalytics.website.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.bean.RequestScoped;
import javax.inject.Named;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.tagcloud.DefaultTagCloudItem;
import org.primefaces.model.tagcloud.DefaultTagCloudModel;
import org.primefaces.model.tagcloud.TagCloudModel;

@Named(value = "analyticsBean")
@RequestScoped
public class AnalyticsBean {
	private LineChartModel tweetCountPerTimeChartModel;
	private TagCloudModel tweetWordsCloudModel;

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

	@PostConstruct
	public void init() {
		tweetCountPerTimeChartModel = new LineChartModel();
		tweetCountPerTimeChartModel.setShowPointLabels(true);
		ChartSeries chartSeries = new ChartSeries();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, -5);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date startTime, endTime;
		tweetCountPerTimeChartModel.setTitle("# of Tweets analysed on last hours");
		tweetCountPerTimeChartModel.setAnimate(true);
		tweetCountPerTimeChartModel.setShadow(false);
		chartSeries.setLabel("Tweets");
		startTime = calendar.getTime();
		calendar.add(Calendar.HOUR, 1);
		endTime = calendar.getTime();
		chartSeries.set(dateFormat.format(startTime) + " - " + dateFormat.format(endTime), 120);
		startTime = calendar.getTime();
		calendar.add(Calendar.HOUR, 1);
		endTime = calendar.getTime();
		chartSeries.set(dateFormat.format(startTime) + " - " + dateFormat.format(endTime), 130);
		startTime = calendar.getTime();
		calendar.add(Calendar.HOUR, 1);
		endTime = calendar.getTime();
		chartSeries.set(dateFormat.format(startTime) + " - " + dateFormat.format(endTime), 120);
		startTime = calendar.getTime();
		calendar.add(Calendar.HOUR, 1);
		endTime = calendar.getTime();
		chartSeries.set(dateFormat.format(startTime) + " - " + dateFormat.format(endTime), 150);
		startTime = calendar.getTime();
		calendar.add(Calendar.HOUR, 1);
		endTime = calendar.getTime();
		chartSeries.set(dateFormat.format(startTime) + " - " + dateFormat.format(endTime), 180);
		startTime = calendar.getTime();
		calendar.add(Calendar.HOUR, 1);
		endTime = calendar.getTime();
		chartSeries.set(dateFormat.format(startTime) + " - Now", 80);
		tweetCountPerTimeChartModel.addSeries(chartSeries);
		tweetCountPerTimeChartModel.getAxes().put(AxisType.X, new CategoryAxis());
		Axis yAxis = tweetCountPerTimeChartModel.getAxis(AxisType.Y);
		yAxis.setLabel("Tweets");
		yAxis.setMin(0);
		yAxis.setMax(200);

		createWordCloud();
	}

	private void createWordCloud() {
		tweetWordsCloudModel = new DefaultTagCloudModel();
		tweetWordsCloudModel.addTag(new DefaultTagCloudItem("Transformers", 1));
		tweetWordsCloudModel.addTag(new DefaultTagCloudItem("RIA", "#", 3));
		tweetWordsCloudModel.addTag(new DefaultTagCloudItem("AJAX", 2));
		tweetWordsCloudModel.addTag(new DefaultTagCloudItem("jQuery", "#", 25));
		tweetWordsCloudModel.addTag(new DefaultTagCloudItem("NextGen", 4));
		tweetWordsCloudModel.addTag(new DefaultTagCloudItem("JSF 2.0", "#", 2));
		tweetWordsCloudModel.addTag(new DefaultTagCloudItem("FCB", 5));
		tweetWordsCloudModel.addTag(new DefaultTagCloudItem("Mobile", 3));
		tweetWordsCloudModel.addTag(new DefaultTagCloudItem("Themes", "#", 4));
		tweetWordsCloudModel.addTag(new DefaultTagCloudItem("Rocks", "#", 1));
		tweetWordsCloudModel.addTag(new DefaultTagCloudItem("Transformers", 1));
		tweetWordsCloudModel.addTag(new DefaultTagCloudItem("RIA", "#", 3));
		tweetWordsCloudModel.addTag(new DefaultTagCloudItem("AJAX", 2));
		tweetWordsCloudModel.addTag(new DefaultTagCloudItem("jQuery", "#", 5));
		tweetWordsCloudModel.addTag(new DefaultTagCloudItem("NextGen", 4));
		tweetWordsCloudModel.addTag(new DefaultTagCloudItem("JSF 2.0", "#", 2));
		tweetWordsCloudModel.addTag(new DefaultTagCloudItem("FCB", 5));
		tweetWordsCloudModel.addTag(new DefaultTagCloudItem("Mobile", 3));
		tweetWordsCloudModel.addTag(new DefaultTagCloudItem("Themes", "#", 4));
		tweetWordsCloudModel.addTag(new DefaultTagCloudItem("Rocks", "#", 1));
		tweetWordsCloudModel.addTag(new DefaultTagCloudItem("Transformers", 1));
		tweetWordsCloudModel.addTag(new DefaultTagCloudItem("RIA", "#", 3));
		tweetWordsCloudModel.addTag(new DefaultTagCloudItem("AJAX", 2));
		tweetWordsCloudModel.addTag(new DefaultTagCloudItem("jQuery", "#", 5));
		tweetWordsCloudModel.addTag(new DefaultTagCloudItem("NextGen", 4));
		tweetWordsCloudModel.addTag(new DefaultTagCloudItem("JSF 2.0", "#", 2));
		tweetWordsCloudModel.addTag(new DefaultTagCloudItem("FCB", 5));
		tweetWordsCloudModel.addTag(new DefaultTagCloudItem("Mobile", 3));
		tweetWordsCloudModel.addTag(new DefaultTagCloudItem("Themes", "#", 4));
		tweetWordsCloudModel.addTag(new DefaultTagCloudItem("Rocks", "#", 1));
	}

	public AnalyticsBean() {
	}

	public void setTweetCountPerTimeChartModel(LineChartModel tweetCountPerTimeChartModel) {
		this.tweetCountPerTimeChartModel = tweetCountPerTimeChartModel;
	}

	public LineChartModel getTweetCountPerTimeChartModel() {
		return tweetCountPerTimeChartModel;
	}

	public TagCloudModel getTweetWordsCloudModel() {
		return tweetWordsCloudModel;
	}

	public void setTweetWordsCloudModel(TagCloudModel tweetWordsCloudModel) {
		this.tweetWordsCloudModel = tweetWordsCloudModel;
	}
}
