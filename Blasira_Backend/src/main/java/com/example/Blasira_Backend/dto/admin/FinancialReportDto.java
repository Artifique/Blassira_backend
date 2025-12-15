package com.example.Blasira_Backend.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
public class FinancialReportDto {
    private LocalDate reportDate;
    private BigDecimal totalRevenue;
    private BigDecimal totalExpenses;
    private BigDecimal netProfit;
    private Map<String, BigDecimal> revenueByCategory;
    private Map<String, BigDecimal> expensesByCategory;
}
