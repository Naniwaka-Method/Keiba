package com.example.demo.Scrape.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Scrape.bean.ShowHorseInfoEntity;
import com.example.demo.Scrape.service.ScrapeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "api/keiba")
public class ScrapeApiController {
	
	private final ScrapeService service;
	
	@GetMapping(path = "/{order}")
	public List<ShowHorseInfoEntity> keibaShow(@PathVariable("order")Integer order) {
		log.info("GET");
		log.info("order:{}",order);
		
		if(order == null || order <= 0) {
			return null;
		}
		
		List<ShowHorseInfoEntity> list = service.show(order);
		
		return list;
	}
	
	
}
