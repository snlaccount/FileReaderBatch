package com.example.springBootBatch.fileRead.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dummy")
public class DumyController {

	@GetMapping("/test")
	public String TestEndpoint() {
		return "Rest call successful";
	}
}
