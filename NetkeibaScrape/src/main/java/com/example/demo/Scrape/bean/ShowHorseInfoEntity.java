package com.example.demo.Scrape.bean;

import lombok.Data;

@Data
public class ShowHorseInfoEntity {
	
	/*
	 * レースID
	 */
	private String raceId;
	
	/*
	 * 馬名
	 */
	private String horseName;
	
	/*
	 * 騎手
	 */
	private String jockeyName;
	
	/*
	 * オッズ
	 */
	private String odds;
	
	/*
	 * 通過順
	 */
	private String passingOrder;
	
	/*
	 * 着順
	 */
	private String arrivalOrder;
	
	/*
	 * 年齢
	 */
	private String age;
	
	/*
	 * 斤量
	 */
	private String impost;
	
	/*
	 * 人気
	 */
	private String popularity;
	
	/*
	 * クラス
	 */
	private String raceClass;
	
	/*
	 * 芝・ダート
	 */
	private String lawnDart;
	
	/*
	 * 距離
	 */
	private String distance; 
	
	/*
	 * 回り
	 */
	private String rotation; 
	
	/*
	 * 馬場
	 */
	private String fieldCondition;
	
	/*
	 * 開催日
	 */
	private String raceDate;
	
	/*
	 * 1着馬とのタイム差
	 */
	private double timeDifference;
}
