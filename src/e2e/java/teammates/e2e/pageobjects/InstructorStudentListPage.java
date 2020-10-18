package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import teammates.common.datatransfer.attributes.StudentAttributes;

/**
 * Page Object Model for instructor student list page.
 */
public class InstructorStudentListPage extends AppPage {

    public InstructorStudentListPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("Student List");
    }

    private List<WebElement> getCoursesTabs() {
        return browser.driver.findElements(By.className("course-table"));
    }

    public void clickCourseTabHeader(String targetHeader) {
        List<WebElement> courseTabs = getCoursesTabs();
        for (WebElement courseTab : courseTabs) {
            WebElement headerElement = courseTab.findElement(By.className("card-header"));
            String header = headerElement.getText();
            if (header.equals(targetHeader)) {
                click(headerElement);
                waitForPageToLoad();
                waitUntilAnimationFinish();
            }
        }
    }

    public void verifyStudentDetails(Map<String, StudentAttributes[]> students) {
        List<WebElement> coursesTabs = getCoursesTabs();
        assertEquals(students.size(), coursesTabs.size());

        students.forEach((courseHeader, studentsForCourse) -> verifyStudentDetails(courseHeader, studentsForCourse));
    }

    public void verifyStudentDetails(String targetCourseHeader, StudentAttributes[] students) {
        WebElement targetCourse = getCourseTabForHeader(targetCourseHeader);
        if (targetCourse == null) {
            fail("Course with header " + targetCourseHeader + " is not found");
        }

        if (students.length == 0) {
            String noStudentText = targetCourse.findElement(By.className("card-body")).getText();
            // Need to account for the text from the enroll students button as well
            String expectedText = "There are no students in this course."
                    + System.lineSeparator() + "Enroll Students";
            assertEquals(expectedText, noStudentText);
        } else {
            WebElement studentList = targetCourse.findElement(By.tagName("table"));
            verifyTableBodyValues(studentList, getExpectedStudentValues(students));
            verifyDisplayedNumbers(targetCourse, students);
        }
    }

    private WebElement getCourseTabForHeader(String targetHeader) {
        List<WebElement> courseTabs = getCoursesTabs();

        return courseTabs.stream().filter(courseTab -> {
            String courseHeader = courseTab.findElement(By.className("card-header")).getText();
            return targetHeader.equals(courseHeader);
        }).findFirst().orElse(null);
    }

    private void verifyDisplayedNumbers(WebElement courseTab, StudentAttributes[] students) {
        String nStudents = courseTab.findElement(By.id("num-students")).getText();
        String nSections = courseTab.findElement(By.id("num-sections")).getText();
        String nTeams = courseTab.findElement(By.id("num-teams")).getText();

        String expectedNStudents = students.length + " students";
        String expectedNSections = Arrays.stream(students)
                .map(StudentAttributes::getSection)
                .distinct()
                .count() + " sections";
        String expectedNTeams = Arrays.stream(students)
                .map(StudentAttributes::getTeam)
                .distinct()
                .count() + " teams";

        assertEquals(expectedNStudents, nStudents);
        assertEquals(expectedNSections, nSections);
        assertEquals(expectedNTeams, nTeams);
    }

    private String[][] getExpectedStudentValues(StudentAttributes[] students) {
        String[][] expected = new String[students.length][6];
        for (int i = 0; i < students.length; i++) {
            StudentAttributes student = students[i];
            expected[i][0] = "View Photo";
            expected[i][1] = student.getSection();
            expected[i][2] = student.getTeam();
            expected[i][3] = student.getName();
            expected[i][4] = student.getGoogleId().isEmpty() ? "Yet to Join" : "Joined";
            expected[i][5] = student.getEmail();
        }
        return expected;
    }

    public void deleteStudent(String courseHeader, String studentEmail) {
        clickAndConfirm(getDeleteButton(courseHeader, studentEmail));
        waitUntilAnimationFinish();
    }

    private WebElement getDeleteButton(String courseHeader, String studentEmail) {
        WebElement studentRow = getStudentRow(courseHeader, studentEmail);
        return studentRow.findElement(By.id("btn-delete"));
    }

    private WebElement getStudentRow(String courseHeader, String studentEmail) {
        WebElement targetCourse = getCourseTabForHeader(courseHeader);
        if (targetCourse == null) {
            fail("Course with header " + courseHeader + " is not found");
        }

        List<WebElement> studentRows = targetCourse.findElements(By.cssSelector("tbody tr"));
        for (WebElement studentRow : studentRows) {
            List<WebElement> studentCells = studentRow.findElements(By.tagName("td"));
            if (studentCells.get(5).getText().equals(studentEmail)) {
                return studentRow;
            }
        }
        return null;
    }

}
