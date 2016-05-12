package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class InstructorStudentRecordsPage extends AppPage {

    @FindBy(id = "button_add_comment")
    private WebElement addCommentLink;

    @FindBy(id = "button_save_comment")
    private WebElement saveCommentLink;

    @FindBy(id = "commentText")
    private WebElement commentTextBox;

    public InstructorStudentRecordsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        String source = getPageSource();
        return source.contains("'s Records") && source.contains("<small class=\"muted\"> - ");
    }

    public void verifyBelongsToStudent(String name) {
        assertTrue(getPageSource().contains(name));
    }

    public void verifyIsCorrectPage(String studentName) {
        assertTrue(containsExpectedPageContents());
        verifyBelongsToStudent(studentName);
    }

    public InstructorStudentRecordsPage addComment(String commentText) {
        addCommentLink.click();
        commentTextBox.sendKeys(commentText);
        saveCommentLink.click();
        waitForPageToLoad();
        return this;
    }

    public InstructorStudentRecordsPage clickDeleteCommentAndCancel(int id) {
        clickAndCancel(getCommentDeleteLink(id));
        waitForPageToLoad();
        return this;
    }

    public InstructorStudentRecordsPage clickDeleteCommentAndConfirm(int id) {
        clickAndConfirm(getCommentDeleteLink(id));
        waitForPageToLoad();
        return this;
    }

    public InstructorStudentRecordsPage editComment(int id, String comment) {
        getCommentEditLink(id).click();
        getCommentTextBox(id).clear();
        getCommentTextBox(id).sendKeys(comment);
        getCommentSaveLink(id).click();
        waitForPageToLoad();
        return this;
    }

    public boolean verifyAddCommentButtonClick() {
        addCommentLink.click();
        boolean display = commentTextBox.isDisplayed()
                       && saveCommentLink.isDisplayed()
                       && !addCommentLink.isDisplayed();
        return display;
    }

    public boolean verifyEditCommentButtonClick(int id) {
        getCommentEditLink(id).click();
        boolean display = getCommentTextBox(id).isEnabled()
                       && getCommentSaveLink(id).isDisplayed()
                       && !getCommentEditLink(id).isDisplayed();
        return display;
    }
    
    public void clickEditCommentAndCancel(int id) {
        getCommentEditLink(id).click();
        getCommentEditCancelLink(id).click();
    }
    
    public void verifyCommentEditBoxNotVisible(int id) {
        assertFalse(isElementVisible(By.id("commentTextEdit" + id)));
    }

    private WebElement getCommentEditLink(int id) {
        return browser.driver.findElement(By.id("commentedit-" + id));
    }

    private WebElement getCommentEditCancelLink(int id) {
        return browser.driver.findElement(By.id("commentsave-" + id))
                             .findElement(By.xpath("./following-sibling::input"));
    }

    private WebElement getCommentDeleteLink(int id) {
        return browser.driver.findElement(By.id("commentdelete-" + id));
    }

    private WebElement getCommentTextBox(int id) {
        return browser.driver.findElement(By.id("commentText" + id));
    }

    private WebElement getCommentSaveLink(int id) {
        return browser.driver.findElement(By.id("commentsave-" + id));
    }

    /**
     * Clicks all the headings of the record panels to either expand/collapse the panel body.
     */
    public void clickAllRecordPanelHeadings() {
        for (WebElement e : browser.driver.findElements(By.cssSelector("div[id^='studentFeedback-']"))) {
            e.findElement(By.cssSelector(".panel-heading")).click();
        }
    }

    /**
     * Checks if the body of all the record panels are visible.
     * @return true if all record panel bodies are visible
     */
    public boolean areRecordsVisible() {
        return areAllRecordPanelBodiesVisibilityEquals(true);
    }

    /**
     * Checks if the body of all the record panels are hidden
     * @return true if all record panel bodies are hidden
     */
    public boolean areRecordsHidden() {
        return areAllRecordPanelBodiesVisibilityEquals(false);
    }

    /**
     * Checks if the bodies of all the record panels are collapsed or expanded.
     * @param isVisible true to check for expanded, false to check for collapsed.
     * @return true if all record panel bodies are equals to the visibility being checked.
     */
    private boolean areAllRecordPanelBodiesVisibilityEquals(boolean isVisible) {
        for (WebElement e : getStudentFeedbackPanels()) {
            if(e.isDisplayed() != isVisible) {
                return false;
            }
        }

        return true;
    }

    public List<WebElement> getStudentFeedbackPanels() {
        List<WebElement> webElements = new ArrayList<WebElement>();
        for (WebElement e : browser.driver.findElements(By.cssSelector("div[id^='studentFeedback-']"))) {
            WebElement panel = e.findElement(By.cssSelector(".panel-collapse"));
            if (panel != null) {
                webElements.add(panel);
            }
        }

        return webElements;
    }

    /**
     * Waits for all the panels to collapse.
     */
    public void waitForPanelsToCollapse() {
        waitForElementsToDisappear(getStudentFeedbackPanels());
    }

    /**
     * Waits for all the panels to expand.
     */
    public void waitForPanelsToExpand() {
        waitForElementsVisibility(getStudentFeedbackPanels());
    }

}
