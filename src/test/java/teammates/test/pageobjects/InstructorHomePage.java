package teammates.test.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.util.Url;



public class InstructorHomePage extends AppPage {
    
    @FindBy(id = "searchbox")
    private WebElement searchBox;
    
    @FindBy(id = "button_search")
    private WebElement searchButton;
    
    @FindBy(id = "sortbyid")
    private WebElement sortByIdButton;

    @FindBy(id = "sortbyname")
    private WebElement sortByNameButton;
    
    @FindBy(id = "sortbydate")
    private WebElement sortByDateButton;
    
    public InstructorHomePage(Browser browser){
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return containsExpectedPageContents(getPageSource());
    }
    
    public static boolean containsExpectedPageContents(String pageSource){
        return pageSource.contains("<h1>Instructor Home</h1>");
    }

    public InstructorHelpPage clickHelpLink() {
        instructorHelpTab.click();
        waitForPageToLoad();
        switchToNewWindow();
        return changePageType(InstructorHelpPage.class);
    }
    
    public void clickSortByIdButton() {
        sortByIdButton.click();
        waitForPageToLoad();
    }
    
    public void clickSortByNameButton() {
        sortByNameButton.click();
        waitForPageToLoad();
    }
    
    public void clickSortByDateButton() {
        sortByDateButton.click();
        waitForPageToLoad();
    }
    
    public InstructorCourseEnrollPage clickCourseErollLink(String courseId) {
        getLinkInRow("t_course_enroll", getCourseRowId(courseId)).click();
        waitForPageToLoad();
        return changePageType(InstructorCourseEnrollPage.class);
    }
    
    public InstructorCourseDetailsPage clickCourseViewLink(String courseId) {
        getLinkInRow("t_course_view", getCourseRowId(courseId)).click();
        waitForPageToLoad();
        return changePageType(InstructorCourseDetailsPage.class);
    }
    
    public InstructorCourseEditPage clickCourseEditLink(String courseId) {
        getLinkInRow("t_course_edit", getCourseRowId(courseId)).click();
        waitForPageToLoad();
        return changePageType(InstructorCourseEditPage.class);
    }
    
    public InstructorEvalsPage clickCourseAddEvaluationLink(String courseId) {
        getLinkInRow("t_course_add_eval", getCourseRowId(courseId)).click();
        waitForPageToLoad();
        return changePageType(InstructorEvalsPage.class);
    }
    
    public InstructorEvalResultsPage clickSessionViewResultsLink(String courseId, String evalName) {
        getViewResultsLink(courseId, evalName).click();
        waitForPageToLoad();
        return changePageType(InstructorEvalResultsPage.class);
    }
    
    public InstructorEvalEditPage clickSessionEditLink(String courseId, String evalName) {
        getEditLink(courseId, evalName).click();
        waitForPageToLoad();
        return changePageType(InstructorEvalEditPage.class);
    }
    
    public InstructorEvalPreview clickSessionPreviewLink(String courseId, String evalName) {
        getPreviewLink(courseId, evalName).click();
        waitForPageToLoad();
        switchToNewWindow();
        return changePageType(InstructorEvalPreview.class);
    }
    
    public void clickHomeTab() {
        instructorHomeTab.click();
        waitForPageToLoad();
    }
    
    public InstructorStudentListPage searchForStudent(String studentName) {
        searchBox.clear();
        searchBox.sendKeys(studentName);
        searchButton.click();
        waitForPageToLoad();
        return changePageType(InstructorStudentListPage.class);
    }
    
    public WebElement getViewResponseLink(String courseId, String evalName) {
        int evaluationRowId = getEvaluationRowId(courseId, evalName);
        String xpathExp = "//tr[@id='session"+ evaluationRowId +"']/td[contains(@class,'t_session_response')]/a";

        return browser.driver.findElement(By.xpath(xpathExp));
    }
    
    public void setViewResponseLinkValue(WebElement element, String newValue) {
        JavascriptExecutor js = (JavascriptExecutor) browser.driver; 
        js.executeScript("arguments[0].href=arguments[1]", element, newValue );
    }

    public WebElement getViewResultsLink(String courseId, String evalName) {
        return getLinkInRow("t_session_view", getEvaluationRowId(courseId, evalName));
    }
    
    public WebElement getEditLink(String courseId, String evalName) {
        return getLinkInRow("t_session_edit", getEvaluationRowId(courseId, evalName));
    }
    
    public WebElement getPreviewLink(String courseId, String evalName) {
        return getLinkInRow("t_session_preview", getEvaluationRowId(courseId, evalName));
    }
    
    public WebElement getRemindLink(String courseId, String evalName) {
        return getLinkInRow("t_session_remind", getEvaluationRowId(courseId, evalName));
    }
    
    public WebElement getPublishLink(String courseId, String evalName){
        return getLinkInRow("t_session_publish", getEvaluationRowId(courseId, evalName));
    }
    
    public WebElement getUnpublishLink(String courseId, String evalName){
        return getLinkInRow("t_session_unpublish", getEvaluationRowId(courseId, evalName));
    }
    
    public WebElement getDeleteEvalLink(String courseId, String evalName){
        return getLinkInRow("t_session_delete", getEvaluationRowId(courseId, evalName));
    }
    
    public WebElement getDeleteCourseLink(String courseId){
        return getLinkInRow("t_course_delete", getCourseRowId(courseId));
    }
    
    public InstructorHomePage clickArchiveCourseLink(String courseId){
        getLinkInRow("t_course_archive", getCourseRowId(courseId)).click();
        waitForPageToLoad();
        return this;
    }
    
    public Url getArchiveCourseLink(String courseId){
        String url = getLinkInRow("t_course_archive", getCourseRowId(courseId)).getAttribute("href");
        return new Url(url);
    }
    
    private WebElement getLinkInRow(String elementClassNamePrefix, int rowId){
        return browser.driver.findElement(By.className(elementClassNamePrefix + rowId));
    }

    private int getEvaluationRowId(String courseId, String evalName) {
        int courseRowID = getCourseRowId(courseId);
        if (courseRowID == -1)
            return -2;
        String template = "//div[@id='course%d']//table[@class='dataTable']//tr[@id='session%d']";
        int max = (Integer) (browser.selenium)
                .getXpathCount("//div//table[@class='dataTable']//tr");
        for (int id = 0; id < max; id++) {
            if (getElementText(
                    By.xpath(String.format(template + "//td[1]", courseRowID,
                            id))).equals(evalName)) {
                return id;
            }
        }
        return -1;
    }
    
    private int getCourseRowId(String courseId) {
        int id = 0;
        while (isElementPresent(By.id("course" + id))) {
            if (getElementText(
                    By.xpath("//div[@id='course" + id
                            + "']/div[@class='result_homeTitle']/h2"))
                    .startsWith("[" + courseId + "]")) {
                return id;
            }
            id++;
        }
        return -1;
    }
    
    private String getElementText(By locator) {
        if (!isElementPresent(locator))
            return "";
        return browser.driver.findElement(locator).getText();
    }
    

}
