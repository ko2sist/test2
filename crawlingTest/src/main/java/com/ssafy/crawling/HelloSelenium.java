package com.ssafy.crawling;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class HelloSelenium {
    public static void main(String[] args) throws InterruptedException {
        WebDriver driver = new ChromeDriver();
        
        // 웹페이지 열기
        driver.get("https://www.card-gorilla.com/search/card?cate=CHK&search_benefit=23,21");
        
        // 팝업(?) 뜨는거 때문에 5초 대기 후 닫고 이후 과정 진행
        TimeUnit.SECONDS.sleep(5);
        WebElement popup_close = driver.findElement(By.className("material-icons"));
        popup_close.click();

        // 카드 더보기 버튼의 display 속성이 none이 될 때까지 버튼 클릭
        while(true){
            WebElement button = driver.findElement(By.className("lst_more"));
            if(button.getCssValue("display").equals("none")){
                break;
            }

            button.click();
            TimeUnit.SECONDS.sleep(1);
        }

        // 자세히 보기 버튼을 하나씩 클릭(새탭에서 열기), 이후 필요한 내용 크롤링
        List<WebElement> details = driver.findElements(By.className("b_view"));

        for(WebElement detail : details){
            // url 얻기
            String url = detail.getAttribute("href");
//            System.out.println(url);

//            detail.sendKeys(Keys.CONTROL + "\n");
//            driver.switchTo();
            
            // 기존 창 정보 저장
            String originalWindow = driver.getWindowHandle();

            // 새탭 열고 포커스 새탭으로
            driver.switchTo().newWindow(WindowType.TAB);

            // 새탭에서 url 열기
            driver.get(url);
            TimeUnit.SECONDS.sleep(1);

            // 크롤링 부분
//            WebElement card_name = driver.findElement(By.className("brand"));
            WebElement card_name = null;
            try{
                card_name = driver.findElement(By.xpath("/html/body/div[1]/div/div/section/div[1]/section/div/article[1]/div/div/div[2]/div[2]/strong"));

            }catch (NoSuchElementException e){
                card_name = driver.findElement(By.xpath("/html/body/div[1]/div/div/section/div[1]/section/div/article[1]/div/div/div[2]/div[1]/strong"));
            }
            System.out.println(card_name.getText());

            List<WebElement> dl_list = driver.findElements(By.tagName("dl"));
            
            // 혜택 펼치기
            for(int i=1; i<=dl_list.size()-6; i++){
                String xp = "/html/body/div[1]/div/div/section/div[1]/section/div/article[2]/div[1]/dl["+String.valueOf(i)+"]";
                WebElement dl = driver.findElement(By.xpath(xp));
                dl.click();

            }

            // 탭 닫기
            driver.close();

            // 원래 탭으로 돌아오기
            driver.switchTo().window(originalWindow);
        }



        // 닫기
        driver.quit();
    }
}
