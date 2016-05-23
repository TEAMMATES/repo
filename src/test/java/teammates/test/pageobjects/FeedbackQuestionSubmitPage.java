package teammates.test.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import teammates.common.util.Const;

public class FeedbackQuestionSubmitPage extends FeedbackSubmitPage {

    public FeedbackQuestionSubmitPage(Browser browser) {
        super(browser);
    }
    
    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Submit Feedback Question</h1>");
    }
    
    public void fillResponseTextBox(int questionNumber, int responseNumber, String text) {
        WebElement element = browser.driver.findElement(
                By.name(Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-" + questionNumber + "-" + responseNumber));
        fillTextBox(element, text);
    }
    
    public void clickSubmitButton() {
        getSubmitButton().click();
    }
    
    public WebElement getSubmitButton() {
        return browser.driver.findElement(By.id("response_submit_button"));
    }
    
    public WebElement getTextArea(int questionNum, int responseNum) {
        String textAreaName = "responsetext-" + questionNum + "-" + responseNum; 
        return browser.driver.findElement(By.name(textAreaName));
    }   
    
    public void clickRubricCell(int respIndex, int row, int col) {
        int qnIndex = 1;
        WebElement radio = browser.driver.findElement(By.id(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + "-" + qnIndex + "-" + respIndex + "-" + row + "-" + col));
        // Gets the parent element.
        WebElement cell = radio.findElement(By.xpath(".."));
        cell.click();
    }
}
