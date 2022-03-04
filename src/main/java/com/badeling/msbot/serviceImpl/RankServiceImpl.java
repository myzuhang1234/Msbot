package com.badeling.msbot.serviceImpl;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.badeling.msbot.config.MsbotConst;
import com.badeling.msbot.service.MvpImageService;
import com.badeling.msbot.service.RankService;
import com.badeling.msbot.util.Loadfont2;

@Component
public class RankServiceImpl implements RankService{
	@Autowired
	MvpImageService mvpImageService;
	
	@Override
	public String getRank(String raw_message) {
		raw_message = raw_message.replace(MsbotConst.botName, "");
		raw_message = raw_message.replace("联盟", "");
		raw_message = raw_message.replace(" ", "");
		String imageUrl;
		String url = "https://api.maplestory.gg/v1/public/character/gms/" + raw_message;
		String message = "";
		
		try {
				StringBuilder json = new StringBuilder();
	            URL urlObject = new URL(url);  
	            URLConnection uc = urlObject.openConnection();
	            uc.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
	            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream(),"UTF-8"));  
	            String inputLine = null;  
	            while ( (inputLine = in.readLine()) != null) {  
	                json.append(inputLine);  
	            }  
	            in.close();
	        
	        @SuppressWarnings("unchecked")
			Map<String,Object> mapler = (Map<String, Object>) JSONObject.parse(json.toString());
	        


//			m.setLegionCoinsPerDay(getJson(jsonData,"LegionCoinsPerDay"));
//			m.setLegionLevel(getJson(jsonData,"LegionLevel"));
//			m.setLegionPower(getJson(jsonData,"LegionPower"));
//			m.setLegionRank(getJson(jsonData,"LegionRank"));
			imageUrl = (String) mapler.get("CharacterImageURL");
			
			message = message + "角色：" + mapler.get("Name") + "\r\n"
					+ "服务器：" + mapler.get("Server") + "\r\n"
					+ "等级：" + mapler.get("Level") + " - " + mapler.get("EXPPercent") + "%  (排名" + mapler.get("ServerRank") + ")\r\n"
					+ "职业：" + mapler.get("Class") + "  (排名" + mapler.get("ServerClassRanking") +")\r\n";
			if(mapler.get("LegionCoinsPerDay")==null) {
				message = message + "非联盟最高角色，无法查询联盟信息";
			}else {
				message = message + "联盟等级：" + mapler.get("LegionLevel") + "  (排名" + mapler.get("LegionRank") +")\r\n"
						+ "联盟战斗力：" + mapler.get("LegionPower") + "  (每天" + mapler.get("LegionCoinsPerDay") +"币)";
			}
		
			@SuppressWarnings("unchecked")
			List<Map<String,Object>> gd = (List<Map<String, Object>>) mapler.get("GraphData");
			if(gd!=null) {
				Long totalDifference = (long) 0;
				for(Map<String,Object> temp : gd) {
					totalDifference = totalDifference + Long.parseLong(temp.get("EXPDifference").toString());
				}
				Long perDay = totalDifference/gd.size();
				Long currentExp = Long.parseLong(gd.get(gd.size()-1).get("TotalOverallEXP").toString());
				if((int)mapler.get("Level")<250) {
					Long nextExp = Long.parseLong("7764453421743");
					String theDay = "";
					if(perDay==0) {
						theDay = "Infinity";
					}else {
						Long day = (nextExp - currentExp)/perDay;
						theDay = day + "";
					}
					message = message + "\r\n" + "按照目前的进度，还需要" + theDay + "天到达250级。";
				}else{
					Long nextExp = Long.parseLong("84583665273612");
					String theDay = "";
					if(perDay==0) {
						theDay = "Infinity";
					}else {
						Long day = (nextExp - currentExp)/perDay;
						theDay = day + "";
					}
					message = message + "\r\n" + "按照目前的进度，还需要" + theDay + "天到达275级。";
				}
				
				Long nextExp = Long.parseLong("10104895811478400");
				String theDay = "";
				if(perDay==0) {
					theDay = "Infinity";
				}else {
					Long day = (nextExp - currentExp)/perDay;
					theDay = day + "";
				}
				message = message + "\r\n" + "按照目前的进度，还需要" + theDay + "天到达300级。";
				
//				message = message + makeLevelChart(gd);
				
			}
			
		} catch (FileNotFoundException e) {
			String a = "查询角色不存在";
			return a;
		} catch (Exception e) {
			e.printStackTrace();
			String a = "连接超时";
			return a;
		}
		//"[CQ:image,file="+imageUrl+"]\r\n"
		if(imageUrl==null) {
			return message;
		}else {
			try {
				String imageName = mvpImageService.saveTempImage(imageUrl);
				message = "[CQ:image,file="+imageName+ "]\r\n" + message;
				return message;
			} catch (Exception e) {
				return message;
			}
		}
	}
	
	private String makeLevelChart(List<Map<String, Object>> gd) {
		XYSeriesCollection dataset1;  
        XYSeriesCollection dataset2;  
        JFreeChart chart;  
        XYPlot plot;  
    	// 生成数据  
        XYSeries series1 = new XYSeries("当天获得经验");
  
        XYSeries series2 = new XYSeries("总经验");
        for(int i=0;i<gd.size();i++) {
        	series1.add(i+1, Long.parseLong(gd.get(i).get("EXPDifference").toString())/100000000);
        	series2.add(i+1, Long.parseLong(gd.get(i).get("TotalOverallEXP").toString())/100000000);  
        }
        
        dataset1 = new XYSeriesCollection();  
        dataset2 = new XYSeriesCollection();  
        
        dataset1.addSeries(series1);  
        dataset2.addSeries(series2);  
  
        chart = ChartFactory.createXYLineChart("近两周内经验获取", "日期",  
                "当天获得经验(亿)", dataset1, PlotOrientation.VERTICAL, true, true,  
                false);  
        
        plot = chart.getXYPlot();  
		 // 设置图表的纵轴和横轴org.jfree.chart.axis.CategoryAxis
       ValueAxis domainAxis = plot.getDomainAxis();
       NumberAxis numberaxis = (NumberAxis) plot.getRangeAxis();
       // 解决中文乱码问题
       Font font = Loadfont2.Font();
       domainAxis.setTickLabelFont(font);
       domainAxis.setLabelFont(font);
       numberaxis.setTickLabelFont(font);
       numberaxis.setLabelFont(font);
       chart.getLegend().setItemFont(font);
       chart.getTitle().setFont(font);// 设置标题字体
       numberaxis.setLowerBound(0);
        // 添加第2个Y轴  
        NumberAxis axis2 = new NumberAxis("总经验(亿)");  
            // -- 修改第2个Y轴的显示效果  
        axis2.setAxisLinePaint(Color.BLUE);  
        axis2.setLabelPaint(Color.BLUE);  
        axis2.setTickLabelPaint(Color.BLUE);  
          
        plot.setRangeAxis(1, axis2);  
        plot.setDataset(1, dataset2);  
        plot.mapDatasetToRangeAxis(1, 1);  
             // -- 修改第2条曲线显示效果  
        XYLineAndShapeRenderer render2 =  new XYLineAndShapeRenderer();   
        render2.setSeriesPaint(0, Color.BLUE);  
        plot.setRenderer(1, render2);  
        plot.mapDatasetToRangeAxis(2, 2);  
          
        XYLineAndShapeRenderer render3 =  new XYLineAndShapeRenderer();   
        render3.setSeriesPaint(0, Color.GREEN);  
        plot.setRenderer(2, render3);  
        
        chart.getLegend().setItemFont(font);  

//        ChartFrame frame = new ChartFrame("多坐标轴", chart);  
//        frame.pack();  
//        frame.setVisible(true);  
        String url =  gd.hashCode() + ".jpg";
        saveAsFile(chart, MsbotConst.imageUrl + url , 600, 400);
        url = "[CQ:image,file=" + url +  "]";
		return url;
	}


	private void saveAsFile(JFreeChart chart, String outputPath, int weight, int height) {
		FileOutputStream out = null;
		try {
			File outFile = new File(outputPath);
			if (!outFile.getParentFile().exists()) {
				outFile.getParentFile().mkdirs();
			}
			out = new FileOutputStream(outputPath);
			// 保存为PNG
			// ChartUtilities.writeChartAsPNG(out, chart, 600, 400);
			// 保存为JPEG
			ChartUtilities.writeChartAsJPEG(out, chart, 600, 400);
			out.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}
	}
}
