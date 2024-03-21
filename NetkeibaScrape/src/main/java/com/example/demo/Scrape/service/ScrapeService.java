package com.example.demo.Scrape.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.demo.Scrape.bean.KeibaEntity;
import com.example.demo.Scrape.bean.ShowHorseInfoEntity;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ScrapeService {
	
	/*
	 * csvファイル読み込み
	 */
	private List<String[]> readCSV(String filePath) {
        List<String[]> dataList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath,  Charset.forName("Shift_JIS")));
            CSVReader csvReader = new CSVReader(reader)) {

           String[] line;
           while ((line = csvReader.readNext()) != null) {
               dataList.add(line);
           }
       } catch (IOException e) {
           e.printStackTrace();
       } catch (CsvValidationException e) {
		// TODO 自動生成された catch ブロック
		e.printStackTrace();
	}

        return dataList;
    }
	
	/*
	 * 全レースListに格納
	 */
	private List<List<KeibaEntity>> add(List<String[]> csvList){
		
		//全レースリストを作成
		List<List<KeibaEntity>>allList = new ArrayList<>();
		
		//レースリストを作成(1レース)
		List<KeibaEntity> raceList = new ArrayList<>();
		
		//ヘッダーを無視するフラグ
		boolean headerFlag = true;
		
		//全データを取得
		for(int i=0;i<csvList.size();i++) {
			
			//ヘッダーは無視する
        	if(headerFlag) {
        		headerFlag = false;
        	}else {
        		
        		//レースidを取得
        		String raceId = csvList.get(i)[0];
        		
        		KeibaEntity entity = new KeibaEntity();
        		
        		//次のレースIDと同じだったら（同レース）
        		if((i+1) != csvList.size() && raceId.equals(csvList.get(i+1)[0])) {
        			
        			//レースリストに追加
            		entity = setEntity(csvList.get(i));
            		
            		raceList.add(entity);
            		
        		}else {
        			log.info("レース:{}",raceList);
        			log.info("サイズ:{}",raceList.size());
        			
        			//レースリストに追加
        			entity = setEntity(csvList.get(i));
            		
            		raceList.add(entity);
        			
        			//全レースリストに追加
        			allList.add(raceList);
            		
            		//レースリストを初期化
        			raceList = new ArrayList<>();
        		}
        	}		
		}
		
		return allList;
	}
	
	private KeibaEntity setEntity(String[] array) {
		KeibaEntity entity = new KeibaEntity();
		
		entity.setRaceId(array[0]);
		entity.setHorseName(array[1]);
		entity.setJockeyName(array[2]);
		entity.setHorseNumber(array[3]);
		entity.setRunningTime(array[4]);
		entity.setOdds(array[5]);
		entity.setPassingOrder(array[6]);
		entity.setArrivalOrder(array[7]);
		entity.setWeight(array[8]);
		entity.setWeightChange(array[9]);
		entity.setSex(array[10]);
		entity.setAge(array[11]);
		entity.setImpost(array[12]);
		entity.setRiseTime(array[13]);
		entity.setPopularity(array[14]);
		entity.setRaceName(array[15]);
		entity.setRaceDate(array[16]);
		entity.setOpen(array[17]);
		entity.setRaceClass(array[18]);
		entity.setLawnDart(array[19]);
		entity.setDistance(array[20]);
		entity.setRotation(array[21]);
		entity.setFieldCondition(array[22]);
		entity.setWeather(array[23]);
		entity.setFieldId(array[24]);
		entity.setFieldName(array[25]);
		
		return entity;
	}
	
	//分析クラス
	private ShowHorseInfoEntity analysis(List<KeibaEntity> raceList,int arrival){
		
		if(raceList.size() <= 10) {
			return null;
		}
		
		//指定した着順の競走馬データを取得
		KeibaEntity entity = raceList.get(arrival-1);
		//通過順をハイフンでスプリット
		String[] passingOrderSp = entity.getPassingOrder().split("-");
		
		//現在のレースリストからn着馬のデータを除去する
		raceList.remove(arrival - 1);
		
		//指定した馬を除いた上位４着馬の最終コーナ通過順の平均
		int averageLast = 0;
		
		//指定した馬を除いた上位４着馬を取得
		for(int i=0; i<４; i++) {
			KeibaEntity highEntity = raceList.get(i);
			
			//通過順をハイフンでスプリット
			String[] highPassingOrderSp = highEntity.getPassingOrder().split("-");
			
			int lastIndex = highPassingOrderSp.length - 1;
			
			averageLast += Integer.parseInt(highPassingOrderSp[lastIndex]);
		}
		
		averageLast = averageLast/4;
		
		int lastIndex = passingOrderSp.length - 1;
		
		//1～4着馬の平均通過順から5着馬の平均順位を引く
		int calcAve = averageLast - Integer.parseInt(passingOrderSp[lastIndex]);
		
		if(calcAve > 5) {
			log.info("いい馬:{}",entity);
			
			// 各時間を秒に変換
	        double seconds1 = timeToSeconds(entity.getRunningTime());
	        double seconds2 = timeToSeconds(raceList.get(0).getRunningTime());
	        
	        // 時間差を計算
	        double timeDifference = Math.abs(seconds1 - seconds2);
	        
	        if(timeDifference <= 0.5) {
	        	log.info("時間差:{}秒",timeDifference);
	        	
				
				ShowHorseInfoEntity showHorseInfoEntity = new ShowHorseInfoEntity();
				ModelMapper mapper = new ModelMapper();
				mapper.map(entity, showHorseInfoEntity);
				return showHorseInfoEntity;
	        }
			
		}
		
		return null;
	}
	
	public List<ShowHorseInfoEntity> show(int order) {
		String csvPath = "スクレイピングしたCSVファイルパス";
		
		//csv読み込み
		List<String[]> csvList = readCSV(csvPath);
		
		//全レースデータ取得
		List<List<KeibaEntity>>allList = add(csvList);
		
		//対象馬リスト
		List<ShowHorseInfoEntity>horseList = new ArrayList<>();
		
		for(List<KeibaEntity> raceList : allList) {
			ShowHorseInfoEntity showHorseInfoEntity = new ShowHorseInfoEntity();
			showHorseInfoEntity = analysis(raceList,order);//指定した着順の対象馬を抽出
			
			if(showHorseInfoEntity != null) {
				horseList.add(showHorseInfoEntity);
			}
		}
		
		
//		for(ShowHorseInfoEntity entity : horseList) {
//			System.out.println(entity.getRaceId()+","+entity.getRaceDate());
//		}
		
		return horseList;
		
	}
	
	
	
	private double timeToSeconds(String timeStr) {
        // 分と秒に分割
        String[] parts = timeStr.split(":");
        double minutes = Double.parseDouble(parts[0]);
        double seconds = Double.parseDouble(parts[1]);

        // 分を秒に変換して合算
        return minutes * 60 + seconds;
    }
	
}
