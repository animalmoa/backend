package com.server.animalmoa.crawler.source.ijoa

import com.server.animalmoa.crawler.webdriver.WebDriverCommandService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class IjoaCrawlerTest {

    @Autowired
    private lateinit var webDriverCommandService: WebDriverCommandService

    @Test
    fun `test analyze HTML structure of ijoa website`() {
        // Navigate to the website
        val url = "https://www.ijoa.co.kr/42"
        webDriverCommandService.navigateTo(url)
        
        // Print the HTML structure
        val bodyElement = webDriverCommandService.getBody()
        println("[DEBUG_LOG] HTML Structure:")
        println("[DEBUG_LOG] ${bodyElement.getAttribute("outerHTML")}")
        
        // Analyze list page structure
        println("[DEBUG_LOG] Analyzing list page structure...")
        
        // Try to find elements with different XPath expressions
        val possibleListItemXPaths = listOf(
            "//div[@class='list-item']",
            "//div[contains(@class, 'list-item')]",
            "//div[contains(@class, 'item')]",
            "//div[@class='board-list']/div",
            "//div[@class='board-list']//div[contains(@class, 'item')]"
        )
        
        for (xpath in possibleListItemXPaths) {
            val elements = webDriverCommandService.findElementsWithWaitingAlwaysAsList(xpath)
            println("[DEBUG_LOG] XPath: $xpath, Found elements: ${elements.size}")
            
            if (elements.isNotEmpty()) {
                println("[DEBUG_LOG] First element HTML: ${elements[0].getAttribute("outerHTML")}")
                
                // Try to find title, thumbnail, and link within the first element
                try {
                    val title = elements[0].findElement(org.openqa.selenium.By.xpath(".//div[@class='list-title']"))
                    println("[DEBUG_LOG] Title found: ${title.text}")
                } catch (e: Exception) {
                    println("[DEBUG_LOG] Title not found with .//div[@class='list-title']")
                }
                
                try {
                    val thumbnail = elements[0].findElement(org.openqa.selenium.By.xpath(".//div[@class='list-img']/img"))
                    println("[DEBUG_LOG] Thumbnail found: ${thumbnail.getAttribute("src")}")
                } catch (e: Exception) {
                    println("[DEBUG_LOG] Thumbnail not found with .//div[@class='list-img']/img")
                }
                
                try {
                    val link = elements[0].findElement(org.openqa.selenium.By.xpath(".//a"))
                    println("[DEBUG_LOG] Link found: ${link.getAttribute("href")}")
                } catch (e: Exception) {
                    println("[DEBUG_LOG] Link not found with .//a")
                }
            }
        }
        
        // Try to click on the first item if found
        val firstItem = webDriverCommandService.findElementWithWaiting("//div[contains(@class, 'item')]//a")
        if (firstItem != null) {
            println("[DEBUG_LOG] Clicking on first item...")
            webDriverCommandService.clickElementWithAction(firstItem)
            
            // Analyze detail page structure
            println("[DEBUG_LOG] Analyzing detail page structure...")
            val detailBody = webDriverCommandService.getBody()
            println("[DEBUG_LOG] Detail page HTML: ${detailBody.getAttribute("outerHTML")}")
            
            // Try to find elements with different XPath expressions
            val possibleCreatedAtXPaths = listOf(
                "//div[@class='view-info']/span[contains(text(), '등록일')]/following-sibling::span",
                "//div[contains(@class, 'view-info')]//span[contains(text(), '등록일')]/following-sibling::span",
                "//div[contains(@class, 'view-info')]//span[contains(text(), '등록일')]",
                "//div[contains(@class, 'view-info')]//span[contains(text(), '날짜')]",
                "//div[contains(@class, 'view-info')]//span[contains(text(), '작성일')]"
            )
            
            for (xpath in possibleCreatedAtXPaths) {
                val element = webDriverCommandService.findElementWithWaiting(xpath)
                println("[DEBUG_LOG] XPath: $xpath, Found element: ${element != null}")
                if (element != null) {
                    println("[DEBUG_LOG] Created at text: ${element.text}")
                }
            }
            
            val possibleContentXPaths = listOf(
                "//div[@class='view-content']",
                "//div[contains(@class, 'view-content')]",
                "//div[contains(@class, 'content')]",
                "//div[@class='board-view']//div[contains(@class, 'content')]"
            )
            
            for (xpath in possibleContentXPaths) {
                val element = webDriverCommandService.findElementWithWaiting(xpath)
                println("[DEBUG_LOG] XPath: $xpath, Found element: ${element != null}")
                if (element != null) {
                    println("[DEBUG_LOG] Content text: ${element.text}")
                }
            }
            
            val possibleInfoTableXPaths = listOf(
                "//div[@class='view-info-table']",
                "//div[contains(@class, 'view-info-table')]",
                "//div[contains(@class, 'info-table')]",
                "//div[@class='board-view']//div[contains(@class, 'info')]"
            )
            
            for (xpath in possibleInfoTableXPaths) {
                val element = webDriverCommandService.findElementWithWaiting(xpath)
                println("[DEBUG_LOG] XPath: $xpath, Found element: ${element != null}")
                if (element != null) {
                    println("[DEBUG_LOG] Info table text: ${element.text}")
                }
            }
        }
    }
}