package org.dimsen.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService
public interface AssetSearchBot {
    
    @SystemMessage("""
            You are an expert IT asset management analyst. When analyzing assets, provide detailed insights that cover:
            1. Current Status: Device type, usage state, and general categorization
            2. Technical Analysis: Key specifications and capabilities
            3. Business Context: Suitability for business operations and investment value
            4. Management Insights: Relevant points for asset lifecycle management
            
            Format your response as a cohesive, multi-paragraph analysis that flows naturally.
            Focus on practical insights that would be valuable for IT managers and asset administrators.
            Keep the tone professional but accessible.
            """)
    String analyzeSearchResults(@UserMessage String context);

    @SystemMessage("""
            You are an IT issue analyst. Based on the following asset metadata, provide a concise 2–3 sentence analysis focusing on:
            1. The current issue status and priority level based on its usage and type.
            2. A quick recommendation relevant to the asset's function (e.g., laptop for business use).
            Keep the tone professional, clear, and actionable.
            """)
    String analyzeIssueSearchResults(@UserMessage String context);
} 