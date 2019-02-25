package teammates.e2e.pageobjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class DevServerLoginPageNew extends LoginPageNew {

    @FindBy(id = "email")
    private WebElement emailTextBox;

    @FindBy(id = "isAdmin")
    private WebElement isAdminCheckBox;

    @FindBy(xpath = "/html/body/form/div/p[3]/input[1]")
    private WebElement loginButton;

    public DevServerLoginPageNew(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h3>Not logged in</h3>");
    }

//    @Override
//    public InstructorHomePage loginAsInstructor(String username, String password) {
//        return loginAsInstructor(username, password, InstructorHomePage.class);
//    }
//
//    @Override
//    public <T extends AppPageNew> T loginAsInstructor(String username, String password, Class<T> typeOfPage) {
//        fillTextBox(emailTextBox, username);
//        click(loginButton);
//        waitForPageToLoad();
//        browser.isAdminLoggedIn = false;
//        return changePageType(typeOfPage);
//    }
//
//    @Override
//    public AppPageNew loginAsInstructorUnsuccessfully(String userName, String password) {
//        fillTextBox(emailTextBox, userName);
//        click(loginButton);
//        waitForPageToLoad();
//        browser.isAdminLoggedIn = false;
//        return this;
//    }

    @Override
    public void loginAdminAsInstructor(
            String adminUsername, String adminPassword, String instructorUsername) {
        fillTextBox(emailTextBox, instructorUsername);
        click(isAdminCheckBox);
        click(loginButton);
        waitForPageToLoad();
        browser.isAdminLoggedIn = true;
    }

    @Override
    public StudentHomePageNew loginAsStudent(String username, String password) {
        return loginAsStudent(username, password, StudentHomePageNew.class);
    }

    @Override
    public <T extends AppPageNew> T loginAsStudent(String username, String password, Class<T> typeOfPage) {
        fillTextBox(emailTextBox, username);
        click(loginButton);
        waitForPageToLoad();
        browser.isAdminLoggedIn = false;
        return changePageType(typeOfPage);
    }

//    @Override
//    public StudentCourseJoinConfirmationPageNew loginAsJoiningStudent(String username, String password) {
//        fillTextBox(emailTextBox, username);
//        click(loginButton);
//        waitForPageToLoad();
//        browser.isAdminLoggedIn = false;
//        return changePageType(StudentCourseJoinConfirmationPageNew.class);
//    }
//
//    @Override
//    public InstructorCourseJoinConfirmationPage loginAsJoiningInstructor(String username, String password) {
//        fillTextBox(emailTextBox, username);
//        click(loginButton);
//        waitForPageToLoad();
//        browser.isAdminLoggedIn = false;
//        return changePageType(InstructorCourseJoinConfirmationPage.class);
//    }
//
//    @Override
//    public InstructorHomePage loginAsJoiningInstructorByPassConfirmation(String username, String password) {
//        fillTextBox(emailTextBox, username);
//        click(loginButton);
//        waitForPageToLoad();
//        browser.isAdminLoggedIn = false;
//        return changePageType(InstructorHomePage.class);
//    }

}
