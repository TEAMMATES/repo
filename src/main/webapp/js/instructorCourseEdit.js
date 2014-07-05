/**
 * Enable the user to edit one instructor and disable editting for other instructors
 * @param instructorNum
 * @param totalInstructors
 */
function enableEditInstructor(instructorNum, totalInstructors) {
    for (var i=1; i<=totalInstructors; i++) {
        if (i == instructorNum) {
            enableFormEditInstructor(i);
        } else {
            disableFormEditInstructor(i);
        }
    }
    hideNewInstructorForm();
}

/**
 * Enable formEditInstructor's input fields, display the "Save changes" button,
 * and disable the edit link.
 * @param number
 */
function enableFormEditInstructor(number) {
    $("#instructorTable" + number).find(":input").not(".immutable").prop("disabled", false);
    $("#instrEditLink" + number).hide();
    $("#accessControlInfoForInstr" + number).hide();
    $("#accessControlEditDivForInstr" + number).show();
    $("#btnSaveInstructor" + number).show();
}

/**
 * Disable formEditInstructor's input fields, hide the "Save changes" button,
 * and enable the edit link.
 * @param number
 */
function disableFormEditInstructor(number) {
    $("#instructorTable" + number).find(":input").not(".immutable").prop("disabled", true);
    $("#instrEditLink" + number).show();
    $("#accessControlInfoForInstr" + number).show();
    $("#accessControlEditDivForInstr" + number).hide();
    $("#btnSaveInstructor" + number).hide();
}

function showNewInstructorForm() {
    $("#panelAddInstructor").show();
    $("#btnShowNewInstructorForm").hide();
    $('html, body').animate({scrollTop: $('#frameBodyWrapper')[0].scrollHeight}, 1000);
}

function hideNewInstructorForm() {
	$("#panelAddInstructor").hide();
    $("#btnShowNewInstructorForm").show();
}

/**
 * Functions to trigger registration key sending to a specific instructor in the
 * course.
 * @param courseID
 * @param email
 */
function toggleSendRegistrationKey(courseID, email) {
    return confirm("Do you wish to re-send the invitation email to this instructor now?");
}

function toggleTunePermissionsDiv(instrNum) {
    $("#tunePermissionsDivForInstructor" + instrNum).toggle();
}

function showTuneSectionPermissionsDiv(instrNum, sectionNum) {
	$("#tuneSectionPermissionsDiv" + sectionNum + "ForInstructor" + instrNum).show();
	var numberOfSections = $("select#section" + sectionNum + "forinstructor" + instrNum + " option").length;
	var numOfVisibleSections = $("#tunePermissionsDivForInstructor" + 1 + " div[id^='tuneSectionPermissionsDiv']").filter(":visible").length;
	
	if (numOfVisibleSections == numberOfSections) {
		$("#addSectionLevelForInstructor" + instrNum).hide();
	}
	$("#tuneSectionPermissionsDiv" + sectionNum + "ForInstructor" + instrNum + " input[name='issection" + sectionNum + "set']").attr("value", "true");
	setAddSectionLevelLink(instrNum);
}

function setAddSectionLevelLink(instrNum) {
	var foundNewLink = false;
	var allSectionSelects = $("#tunePermissionsDivForInstructor" + instrNum + " div[id^='tuneSectionPermissionsDiv']").find("input[type=hidden]").not("[name*='sessions']");
	for (var idx=0;idx < allSectionSelects.length;idx++) {
		var item = $(allSectionSelects[idx]);
		if (item.attr("value") === "false") {
			var sectionNumStr = item.attr("name").substring(9).slice(0, -3);
			$("#addSectionLevelForInstructor" + instrNum).attr("onclick", "showTuneSectionPermissionsDiv(" + instrNum + ", " + sectionNumStr + ")");
			foundNewLink = true;
			break;
		}
	}
	if (!foundNewLink) {
		$("#addSectionLevelForInstructor" + instrNum).hide();
	}
}

function hideTuneSectionPermissionsDiv(instrNum, sectionNum) {
	$("#tuneSectionPermissionsDiv" + sectionNum + "ForInstructor" + instrNum).hide();
	$("#tuneSectionPermissionsDiv" + sectionNum + "ForInstructor" + instrNum + " input[name='issection" + sectionNum + "set']").attr("value", "false");
	$("#addSectionLevelForInstructor" + instrNum).show();
	setAddSectionLevelLink(instrNum);
}

function showTuneSessionnPermissionsDiv(instrNum, sectionNum) {
	$("#tuneSessionPermissionsDiv" + sectionNum + "ForInstructor" + instrNum).show();
	$("#toggleSessionLevelInSection" + sectionNum + "ForInstructor" + instrNum).html("Hide session-level privileges");
	$("#toggleSessionLevelInSection" + sectionNum + "ForInstructor" + instrNum).attr("onclick", "hideTuneSessionnPermissionsDiv(" + instrNum + ", " + sectionNum + ")");
    $("#tuneSectionPermissionsDiv" + sectionNum + "ForInstructor" + instrNum + " input[name='issection" + sectionNum + "sessionsset']").attr("value", "true");
}

function hideTuneSessionnPermissionsDiv(instrNum, sectionNum) {
	$("#tuneSessionPermissionsDiv" + sectionNum + "ForInstructor" + instrNum).hide();
	$("#toggleSessionLevelInSection" + sectionNum + "ForInstructor" + instrNum).html("Configure session-level privileges");
	$("#toggleSessionLevelInSection" + sectionNum + "ForInstructor" + instrNum).attr("onclick", "showTuneSessionnPermissionsDiv(" + instrNum + ", " + sectionNum + ")");
	$("#tuneSectionPermissionsDiv" + sectionNum + "ForInstructor" + instrNum + " input[name='issection" + sectionNum + "sessionsset']").attr("value", "false");
}

function checkTheRoleThatApplies(instrNum) {
	var instrRole = $("#accessControlInfoForInstr"+instrNum+" div div p").html();
	$("input[id='instructorroleforinstructor" + instrNum + "']").filter("[value='" + instrRole + "']").prop("checked", true);
}

function checkPrivilegesOfRoleForInstructor(instrNum, role) {
	if (role === "Co-owner") {
		checkPrivilegesOfCoownerForInstructor(instrNum);
	} else if (role === "Manager") {
		checkPrivilegesOfManagerForInstructor(instrNum);
	} else if (role === "Observer") {
		checkPrivilegesOfObserverForInstructor(instrNum);
	} else if (role === "Tutor") {
		checkPrivilegesOfTutorForInstructor(instrNum);
	} else if (role === "Helper") {
		checkPrivilegesOfHelperForInstructor(instrNum);
	} else {
		console.log(Role + " is not properly defined");
	}
}

function checkPrivilegesOfCoownerForInstructor(instrNum) {
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canmodifycourse']").prop("checked", true);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canmodifyinstructor']").prop("checked", true);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canmodifysession']").prop("checked", true);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canmodifystudent']").prop("checked", true);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canviewstudentinsection']").prop("checked", true);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='cangivecommentinsection']").prop("checked", true);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canviewcommentinsection']").prop("checked", true);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canmodifycommentinsection']").prop("checked", true);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='cansubmitsessioninsection']").prop("checked", true);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canviewsessioninsection']").prop("checked", true);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canmodifysessioncommentinsection']").prop("checked", true);
}

function checkPrivilegesOfManagerForInstructor(instrNum) {
	checkPrivilegesOfCoownerForInstructor(instrNum);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canmodifycourse']").prop("checked", false);
}

function checkPrivilegesOfObserverForInstructor(instrNum) {
	checkPrivilegesOfHelperForInstructor(instrNum);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canviewstudentinsection']").prop("checked", true);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canviewcommentinsection']").prop("checked", true);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canviewsessioninsection']").prop("checked", true);
}

function checkPrivilegesOfTutorForInstructor(instrNum) {
	checkPrivilegesOfHelperForInstructor(instrNum);
}

function checkPrivilegesOfHelperForInstructor(instrNum) {
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canmodifycourse']").prop("checked", false);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canmodifyinstructor']").prop("checked", false);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canmodifysession']").prop("checked", false);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canmodifystudent']").prop("checked", false);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canviewstudentinsection']").prop("checked", false);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='cangivecommentinsection']").prop("checked", false);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canviewcommentinsection']").prop("checked", false);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canmodifycommentinsection']").prop("checked", false);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='cansubmitsessioninsection']").prop("checked", false);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canviewsessioninsection']").prop("checked", false);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canmodifysessioncommentinsection']").prop("checked", false);
}


/**
 * Function that shows confirmation dialog for deleting a instructor
 * @param courseID
 * @param instructorName
 * @param isDeleteOwnself
 * @returns
 */
function toggleDeleteInstructorConfirmation(courseID, instructorName, isDeleteOwnself) {
    if (isDeleteOwnself) {
        return confirm("Are you sure you want to delete your instructor role from the course " + courseID + "? " +
        "You will not be able to access the course anymore.");
    } else {
        return confirm("Are you sure you want to delete the instructor " + instructorName + " from " + courseID + "? " +
            "He/she will not be able to access the course anymore.");
    }
}

$(function(){
	var numOfInstr = $("form[id^='formEditInstructor']").length;
	for (var i=0; i<numOfInstr;i++) {
		checkTheRoleThatApplies(i+1);
	}
	$("input[id^='instructorroleforinstructor']").change(function(){
		var idAttr = $(this).attr('id');
		var instrNum = parseInt(idAttr.substring(27));
		var role = $(this).attr("value");
		checkPrivilegesOfRoleForInstructor(instrNum, role);
	});
});
