package com.example.recycling_service;

import com.example.recycling_service.service.RecyclingPointService;
import com.example.recycling_service.model.RecyclingPoint;
import com.example.recycling_service.repository.RecyclingPointRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class RecyclingServiceApplicationTests {

	@Mock
	private RecyclingPointRepository recyclingPointRepository;

	@InjectMocks
	private RecyclingPointService recyclingPointService;

	@Test
	public void testGetAllPoints() {
		when(recyclingPointRepository.findAll()).thenReturn(Collections.emptyList());
		List<RecyclingPoint> points = recyclingPointService.getAllPoints();
		assertEquals(0, points.size());
	}
}