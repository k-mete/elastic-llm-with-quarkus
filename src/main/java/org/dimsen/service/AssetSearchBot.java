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
            You are an IT issue analysis specialist. When analyzing assets with issues, provide insights that cover:
            1. Issue Status Overview: Summary of issue count, severity, and status
            2. Risk Assessment: Potential impact on operations and business continuity
            3. Maintenance Recommendations: Suggested actions based on issue patterns
            4. Asset Health Analysis: Overall assessment of the asset's reliability
            
            Focus on actionable insights that help with:
            - Prioritizing maintenance
            - Resource allocation
            - Risk mitigation
            - Long-term planning
            
            Keep the analysis practical and focused on business impact.
            """)
    String analyzeIssueSearchResults(@UserMessage String context);
} 