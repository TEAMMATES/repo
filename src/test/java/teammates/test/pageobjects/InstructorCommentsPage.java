package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertEquals;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class InstructorCommentsPage extends AppPage {

    @FindBy(id = "option-check")
    private WebElement showMoreOptionsCheckbox;
    
    @FindBy(id = "displayArchivedCourses_check")
    private WebElement isIncludeArchivedCoursesCheckbox;
    
    public InstructorCommentsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Comments from Instructors</h1>");
    }
    
    public void clickShowMoreOptions(){
        showMoreOptionsCheckbox.click();
        waitForPageToLoad();
    }
    
    public void clickIsIncludeArchivedCoursesCheckbox(){
        isIncludeArchivedCoursesCheckbox.click();
        waitForPageToLoad();
    }
    
    public void clickPreviousCourseLink(){
        getPreviousCourseLink().click();
        waitForPageToLoad();
    }
    
    public void clickNextCourseLink(){
        getNextCourseLink().click();
        waitForPageToLoad();
    }
    
    public void showCommentsForAll(){
        browser.driver.findElement(By.id("panel_all")).click();;
    }
    
    public void showCommentsFromAll(){
        browser.driver.findElement(By.id("giver_all")).click();;
    }
    
    public void showCommentsForPanel(int panelIdx){
        browser.driver.findElement(By.id("panel_check-" + panelIdx)).click();;
    }
    
    public void showCommentsFromGiver(String giverIdx){
        browser.driver.findElement(By.id("giver_check-by-" + giverIdx)).click();;
    }

    public WebElement getNextCourseLink() {
        String xpathExp = "//*[@id=\"frameBodyWrapper\"]/ul[1]/li[4]/a";
        return browser.driver.findElement(By.xpath(xpathExp));
    }
    
    public WebElement getPreviousCourseLink() {
        String xpathExp = "//*[@id=\"frameBodyWrapper\"]/ul[1]/li[1]/a";
        return browser.driver.findElement(By.xpath(xpathExp));
    }
    
    public WebElement getStudentCommentRow(int rowIdx) {
        return browser.driver.findElement(By.id("form_commentedit-" + rowIdx));
    }

    public void clickStudentCommentEditForRow(int i) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) browser.driver;
        jsExecutor.executeScript("document.getElementById('"+"commentedit-"+i+"').click();");
    }

    public void fillTextareaToEditStudentCommentForRow(int i, String text){
        WebElement textarea = browser.driver.findElement(By.id("commentText" + i));
        textarea.click();
        textarea.clear();
        textarea.sendKeys(text);
    }
    
    public void saveEditStudentCommentForRow(int i){
        browser.driver.findElement(By.id("commentsave-" + i)).click();
        waitForPageToLoad();
    }

    public void clickResponseCommentAdd(int sessionIdx, int questionIdx, int responseIdx) {
        browser.driver.findElement(By.id("button_add_comment-" + sessionIdx + "-" + questionIdx + "-" + responseIdx)).click();
        waitForPageToLoad();
    }

    public void fillTextareaToEditResponseComment(int sessionIdx, int questionIdx, int responseIdx, String text) {
        WebElement textarea = browser.driver.findElement(By.id("responseCommentAddForm-" + sessionIdx + "-" + questionIdx + "-" + responseIdx));
        textarea.click();
        textarea.clear();
        textarea.sendKeys(text);
    }
    
    public void fillTextareaToEditResponseComment(int sessionIdx, int questionIdx, int responseIdx, Integer commentIdx, String text) {
        WebElement textarea = browser.driver.findElement(By.id("responsecommenttext-" + sessionIdx + "-" + questionIdx + "-" + responseIdx
                 + "-" + commentIdx));
        textarea.click();
        textarea.clear();
        textarea.sendKeys(text);
    }

    public void addResponseComment(int sessionIdx, int questionIdx, int responseIdx) {
        browser.driver.findElement(By.id("button_save_comment_for_add-" + sessionIdx + "-" + questionIdx + "-" + responseIdx)).click();
        waitForPageToLoad();
    }

    public void clickResponseCommentEdit(int sessionIdx, int questionIdx, int responseIdx, int commentIdx) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) browser.driver;
        jsExecutor.executeScript("document.getElementById('"
                + "commentedit-" + sessionIdx + "-" + questionIdx + "-" + responseIdx + "-" + commentIdx + "').click();");
        waitForPageToLoad();
    }

    public void saveResponseComment(int sessionIdx, int questionIdx, int responseIdx, int commentIdx) {
        browser.driver.findElement(By.id("button_save_comment_for_edit-" + sessionIdx + "-" + questionIdx + "-" + responseIdx + "-" + commentIdx)).click();
        waitForPageToLoad();
    }

    public void clickResponseCommentDelete(int sessionIdx, int questionIdx, int responseIdx, int commentIdx) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) browser.driver;
        jsExecutor.executeScript("document.getElementById('"
                + "commentdelete-" + sessionIdx + "-" + questionIdx + "-" + responseIdx + "-" + commentIdx + "').click();");
        waitForPageToLoad();
    }
    
    public void verifyCommentFormErrorMessage(String commentTableIdSuffix, String errorMessage) {
        int idNumber = commentTableIdSuffix.split("-").length;
        if(idNumber == 4){
            WebElement commentRow = browser.driver.findElement(By.id("responseCommentEditForm-" + commentTableIdSuffix));
            waitForPageToLoad();
            assertEquals(errorMessage, commentRow.findElement(By.tagName("span")).getText());
        } else if(idNumber == 3){
            WebElement commentRow = browser.driver.findElement(By.id("showResponseCommentAddForm-" + commentTableIdSuffix));
            waitForPageToLoad();
            assertEquals(errorMessage, commentRow.findElement(By.tagName("span")).getText());
        }
    }
}
