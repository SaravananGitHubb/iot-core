package com.infosys.iot.scheduler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.iot.models.IotData;
import com.infosys.iot.models.MessageData;
import com.infosys.iot.repo.MessageDataDAO;

import lombok.extern.log4j.Log4j;

@Component
@Log4j
public class DataExportScheduler {
	@Value("${iot.hourrange}")
	Long hourRange;
	@Value("${iot.violationRange}")
	Long violationRange;
	@Value("${iot.json.path}")
	String filePath;
	@Value("${iot.tempUpperRange}")
	Double upperRange;
	@Value("${iot.tempLowerRange}")
	Double lowerRange;
	@Autowired
	MessageDataDAO messageDao;
	File jsonfile;

	@PostConstruct
	public void init() {
		jsonfile = new File(filePath);
	}

	String previousRecord = "";

	@Scheduled(fixedRate = 10000)
	public void scheduleTaskWithFixedRate() {

		HashMap<String, List<MessageData>> recordSet = new HashMap<String, List<MessageData>>();
		List<String> sensorIds = messageDao.getDistinctSensor();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Date queryTime = new Date(new Date().getTime() - (hourRange * 60 * 60 * 1000));
		for (String sensorId : sensorIds) {
			List<MessageData> records = messageDao.getMessagesForSensor(sensorId, queryTime);
			List<MessageData> violationRecords = new ArrayList();
			int count = 0;
			for (MessageData md : records) {
				if (!(md.getTemperature() >= lowerRange && md.getTemperature() <= upperRange)) {
					if (count == 3) {
						violationRecords.remove(0);
					}
					violationRecords.add(md);
					count++;
				} else {
					if (count > 0 && count < 3) {
						count = 0;
					}
				}
			}
			if (violationRecords.size() > 2)
				recordSet.put(sensorId, violationRecords);
		}
		List<IotData> violationIot = new ArrayList<IotData>();
		HashMap<String, List<IotData>> iotRecordSet = new HashMap<String, List<IotData>>();
		for (String key : recordSet.keySet()) {
			List<MessageData> dataList = recordSet.get(key);
			IotData aData = new IotData();
			aData.setBinlocation("loc2A");
			aData.setSensor(dataList.get(0).getSensorId());
			aData.setTemperature(dataList.get(2).getTemperature().toString());
			aData.setStarttime(sdf.format(dataList.get(0).getTimestamp()));
			aData.setEndtime(sdf.format(dataList.get(2).getTimestamp()));
			violationIot.add(aData);
		}
		iotRecordSet.put("iotdata", violationIot);
		try {
			String newRecord = new ObjectMapper().writeValueAsString(iotRecordSet);
			log.info(newRecord);
			log.info("Fixed Rate Task :: Execution Time - {}" + newRecord);
			if (!previousRecord.equals(newRecord)) {
				FileOutputStream fos = new FileOutputStream(jsonfile);
				fos.write(newRecord.getBytes());
				previousRecord = newRecord;
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
