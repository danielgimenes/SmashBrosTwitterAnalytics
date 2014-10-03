/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2014 Daniel Gimenes
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package br.com.dgimenes.smashbrostwitteranalytics.website.presentation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.HorizontalBarChartModel;
import org.primefaces.model.chart.LineChartModel;

import br.com.dgimenes.smashbrostwitteranalytics.website.control.AnalyticsController;
import br.com.dgimenes.smashbrostwitteranalytics.website.control.TweetController;
import br.com.dgimenes.smashbrostwitteranalytics.website.model.Tweet;
import br.com.dgimenes.smashbrostwitteranalytics.website.model.dto.CharacterRankPosition;
import br.com.dgimenes.smashbrostwitteranalytics.website.model.dto.TweetCountPerDay;
import br.com.dgimenes.smashbrostwitteranalytics.website.model.dto.TweetCountPerHour;
import br.com.dgimenes.smashbrostwitteranalytics.website.model.dto.WordCount;

@Named(value = "analyticsBean")
@RequestScoped
public class AnalyticsBean {
	private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd");
	private static final DateFormat tweetDateFormat = new SimpleDateFormat("HH:mm - dd MMM yyyy");
	private static final int NUMBER_OF_HOURS_TO_DISPLAY_ON_BAR_CHART = 6;
	private static final int NUMBER_OF_LATEST_TWEETS = 3;
	private static final int NUMBER_OF_DAYS_TO_DISPLAY_ON_BAR_CHART = 6;
	private List<Tweet> latestTweets;
	private LineChartModel tweetCountPerTimeChartModel;
	private BarChartModel tweetCountPerDayChartModel;
	private HorizontalBarChartModel characterRankChartModel;
	private Long totalTweetCount;
	private String wordCloudJSList;

	@Inject
	private AnalyticsController analyticsController;

	@Inject
	private TweetController tweetController;

	@PostConstruct
	public void init() {
		this.totalTweetCount = analyticsController.getTweetCount();
		createTimeBarChart();
		createDayBarChart();
		createRankBarChart();
		createWordCloud();
		loadLatestTweets();
	}

	private void loadLatestTweets() {
		latestTweets = tweetController.getLatestTweets(NUMBER_OF_LATEST_TWEETS);
	}

	private void createTimeBarChart() {
		Calendar calendar = Calendar.getInstance();
		Date now = calendar.getTime();
		calendar.add(Calendar.HOUR, (NUMBER_OF_HOURS_TO_DISPLAY_ON_BAR_CHART - 1) * -1);
		List<TweetCountPerHour> tweetCountPerHour = analyticsController.getTweetCountPerHour(calendar.getTime(), now);
		tweetCountPerTimeChartModel = new LineChartModel();
		tweetCountPerTimeChartModel.setShowPointLabels(true);
		ChartSeries chartSeries = new ChartSeries();
		chartSeries.setLabel("Tweets");
		tweetCountPerTimeChartModel.setAnimate(true);
		tweetCountPerTimeChartModel.setShadow(false);
		Date endTime;
		for (TweetCountPerHour tweetCount : tweetCountPerHour) {
			calendar = Calendar.getInstance();
			calendar.setTime(tweetCount.getHour());
			calendar.add(Calendar.HOUR, 1);
			endTime = calendar.getTime();
			chartSeries.set(timeFormat.format(tweetCount.getHour()) + " - " + timeFormat.format(endTime),
					tweetCount.getCount());
		}
		tweetCountPerTimeChartModel.addSeries(chartSeries);
		tweetCountPerTimeChartModel.getAxes().put(AxisType.X, new CategoryAxis());
		Axis yAxis = tweetCountPerTimeChartModel.getAxis(AxisType.Y);
		yAxis.setLabel("Tweets");
		yAxis.setMin(0);
	}

	private void createDayBarChart() {
		Calendar calendar = Calendar.getInstance();
		Date now = calendar.getTime();
		calendar.add(Calendar.DATE, (NUMBER_OF_DAYS_TO_DISPLAY_ON_BAR_CHART - 1) * -1);
		List<TweetCountPerDay> tweetCountPerDay = analyticsController.getTweetCountPerDay(calendar.getTime(), now);
		tweetCountPerDayChartModel = new BarChartModel();
		tweetCountPerDayChartModel.setShowPointLabels(true);
		ChartSeries chartSeries = new ChartSeries();
		chartSeries.setLabel("Tweets");
		tweetCountPerDayChartModel.setAnimate(true);
		tweetCountPerDayChartModel.setShadow(false);
		Date endTime;
		for (TweetCountPerDay tweetCount : tweetCountPerDay) {
			calendar = Calendar.getInstance();
			calendar.setTime(tweetCount.getDay());
			calendar.add(Calendar.DATE, 1);
			endTime = calendar.getTime();
			chartSeries.set(dateFormat.format(tweetCount.getDay()) + " - " + dateFormat.format(endTime),
					tweetCount.getCount());
		}
		tweetCountPerDayChartModel.addSeries(chartSeries);
		tweetCountPerDayChartModel.getAxes().put(AxisType.X, new CategoryAxis());
		Axis yAxis = tweetCountPerDayChartModel.getAxis(AxisType.Y);
		yAxis.setLabel("Tweets");
		yAxis.setMin(0);
	}

	private void createRankBarChart() {
		characterRankChartModel = new HorizontalBarChartModel();
		characterRankChartModel.setShowPointLabels(true);
		ChartSeries chartSeries = new ChartSeries();
		chartSeries.setLabel("Tweets");
		characterRankChartModel.setAnimate(true);
		characterRankChartModel.setShadow(false);
		List<CharacterRankPosition> charactersRank = analyticsController.getCharactersRank();
		for (CharacterRankPosition characterRankPosition : charactersRank) {
			chartSeries.set(characterRankPosition.getCharacter().getPrintableName(), characterRankPosition.getRefs());
		}
		characterRankChartModel.addSeries(chartSeries);
		Axis xAxis = characterRankChartModel.getAxis(AxisType.X);
		xAxis.setLabel("Tweets");
		xAxis.setMin(0);
	}

	private void createWordCloud() {
		List<WordCount> words = analyticsController.getMostUsedWordsOnTweets();
		StringBuilder sb = new StringBuilder();
		for (WordCount wordCount : words) {
			sb.append("{text: '");
			sb.append(wordCount.getWord());
			sb.append("', weight: ");
			sb.append(wordCount.getCount());
			sb.append("},");
		}
		this.wordCloudJSList = sb.toString();
	}

	public AnalyticsBean() {
	}

	public void setTweetCountPerTimeChartModel(LineChartModel tweetCountPerTimeChartModel) {
		this.tweetCountPerTimeChartModel = tweetCountPerTimeChartModel;
	}

	public LineChartModel getTweetCountPerTimeChartModel() {
		return tweetCountPerTimeChartModel;
	}

	public Long getTotalTweetCount() {
		return totalTweetCount;
	}

	public List<Tweet> getLatestTweets() {
		return latestTweets;
	}

	public String formatDate(Date date) {
		return tweetDateFormat.format(date).toLowerCase();
	}

	public String getWordCloudJSList() {
		return wordCloudJSList;
	}

	public BarChartModel getTweetCountPerDayChartModel() {
		return tweetCountPerDayChartModel;
	}

	public BarChartModel getCharacterRankChartModel() {
		return characterRankChartModel;
	}
}
