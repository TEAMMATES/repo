'use strict';

var _const = require('../common/const');

var _instructor = require('../common/instructor');

var _sortBy = require('../common/sortBy');

var _statusMessage = require('../common/statusMessage');

var _bootboxWrapper = require('../common/bootboxWrapper');

// Trigger ajax request for a course through clicking the heading
function triggerAjax(e) {
    $(e).trigger('click');
}

/**
 * Check if all available sections are selected
 */
function checkAllSectionsSelected() {
    if ($('input[id^="section_check"]:visible:checked').length === $('input[id^="section_check"]:visible').length) {
        $('#section_all').prop('checked', true);
    } else {
        $('#section_all').prop('checked', false);
    }
}

/**
 * Check if all available teams are selected
 */
function checkAllTeamsSelected() {
    if ($('input[id^="team_check"]:visible:checked').length === $('input[id^="team_check"]:visible').length) {
        $('#team_all').prop('checked', true);
    } else {
        $('#team_all').prop('checked', false);
    }
}

/**
 * Remove param and its value pair in the given url
 * Return the url withour param and value pair
 */
function removeParamInUrl(url, param) {
    var indexOfParam = url.indexOf('?' + param);
    indexOfParam = indexOfParam === -1 ? url.indexOf('&' + param) : indexOfParam;
    var indexOfAndSign = url.indexOf('&', indexOfParam + 1);
    var urlBeforeParam = url.substr(0, indexOfParam);
    var urlAfterParamValue = indexOfAndSign === -1 ? '' : url.substr(indexOfAndSign);
    return urlBeforeParam + urlAfterParamValue;
}

/**
 * Go to the url with appended param and value pair
 */
function gotoUrlWithParam(url, param, value) {
    var paramValuePair = param + '=' + value;
    if (!url.includes('?')) {
        window.location.href = url + '?' + paramValuePair;
    } else if (!url.includes(param)) {
        window.location.href = url + '&' + paramValuePair;
    } else if (url.includes(paramValuePair)) {
        window.location.href = url;
    } else {
        var urlWithoutParam = removeParamInUrl(url, param);
        gotoUrlWithParam(urlWithoutParam, param, value);
    }
}

/**
 * Hide sections that are not selected
 */
function filterSection() {
    $('input[id^="section_check"]').each(function () {
        var courseIdx = $(this).attr('id').split('-')[1];
        var sectionIdx = $(this).attr('id').split('-')[2];
        if (this.checked) {
            $('#studentsection-c' + courseIdx + '\\.' + sectionIdx).show();
        } else {
            $('#studentsection-c' + courseIdx + '\\.' + sectionIdx).hide();
        }
    });
}

/**
 * Hide teams that are not selected
 */
function filterTeam() {
    $('input[id^="team_check"]').each(function () {
        var idContents = $(this).attr('id').split('-');
        var courseIdx = idContents[1];
        var sectionIdx = idContents[2];
        var teamIdx = idContents[3];
        if (this.checked) {
            $('#studentteam-c' + courseIdx + '\\.' + sectionIdx + '\\.' + teamIdx).parent().show();
        } else {
            $('#studentteam-c' + courseIdx + '\\.' + sectionIdx + '\\.' + teamIdx).parent().hide();
        }
    });
}

/**
 * Hide student email view based on search key
 * Uses the hidden attributes of the student_row inside dataTable
 */
function filterEmails() {
    var uniqueEmails = {};
    $('tr[id^="student-c"]').each(function () {
        var elementId = $(this).attr('id');
        var studentId = elementId.split('-')[1];
        var emailElement = $('#student_email-' + studentId.replace('.', '\\.'));
        var emailText = emailElement.text();
        if ($(this).is(':hidden') || uniqueEmails[emailText]) {
            emailElement.hide();
        } else {
            uniqueEmails[emailText] = true;
            emailElement.show();
        }
    });
}

/**
 * Apply search filters for course followed by sections, then by teams, lastly by name
 * Apply display filter for email
 */
function applyFilters() {
    $('tr[id^="student-c"]').show();
    filterSection();
    filterTeam();
    filterEmails();
}

// Binding check for course selection
function checkCourseBinding(e) {
    var courseIdx = $(e).attr('id').split('-')[1];

    // Check/hide all section that is in this course
    if ($(e).prop('checked')) {
        $('input[id^="section_check-' + courseIdx + '-"]').prop('checked', true);
        $('input[id^="section_check-' + courseIdx + '-"]').parent().show();
        $('input[id^="team_check-' + courseIdx + '-"]').prop('checked', true);
        $('input[id^="team_check-' + courseIdx + '-"]').parent().show();
    } else {
        $('input[id^="section_check-' + courseIdx + '-"]').prop('checked', false);
        $('input[id^="section_check-' + courseIdx + '-"]').parent().remove();
        $('input[id^="team_check-' + courseIdx + '-"]').prop('checked', false);
        $('input[id^="team_check-' + courseIdx + '-"]').parent().remove();
        $('div[id^="student_email-c' + courseIdx + '"]').remove();
    }

    // If all the courses are selected, check the 'Select All' option
    if ($('input[id^="course_check"]:checked').length === $('input[id^="course_check"]').length) {
        $('#course_all').prop('checked', true);
    } else {
        $('#course_all').prop('checked', false);
    }

    // If none of of the courses are selected, hide the section"s 'Select All' option
    if ($('input[id^="course_check"]:checked').length === 0) {
        $('#section_all').parent().hide();
        $('#team_all').parent().hide();
        $('#show_email').parent().hide();
    } else {
        $('#section_all').parent().show();
        $('#team_all').parent().show();
        $('#show_email').parent().show();
    }

    // If all the currently visible sections are selected, check the 'Select All' option
    // This is necessary here because we show/hide the section's 'Select All' previously
    checkAllSectionsSelected();
    checkAllTeamsSelected();

    applyFilters();
}

function bindCollapseEvents(panels) {
    var numPanels = -1;
    for (var i = 0; i < panels.length; i += 1) {
        var heading = $(panels[i]).children('.panel-heading');
        var bodyCollapse = $(panels[i]).children('.panel-collapse');
        if (heading.length !== 0 && bodyCollapse.length !== 0) {
            numPanels += 1;
            $(heading[0]).attr('data-target', '#panelBodyCollapse-' + numPanels);
            $(heading[0]).attr('id', 'panelHeading-' + numPanels);
            $(heading[0]).css('cursor', 'pointer');
            $(bodyCollapse[0]).attr('id', 'panelBodyCollapse-' + numPanels);
        }
    }
}

var STUDENT_LIMIT = 3000;
var PERFORMANCE_ISSUE_MESSAGE = 'Due to performance issue, it is not allowed to show more than ' + STUDENT_LIMIT + ' students. Please deselect some courses to view student list of other courses.';
var numStudents = 0;

function transportSectionChoices() {
    var sectionChoices = $('.section-to-be-transported');
    sectionChoices.remove();
    $('#sectionChoices').append(sectionChoices);
    sectionChoices.removeClass('section-to-be-transported');
}

function transportTeamChoices() {
    var teamChoices = $('.team-to-be-transported');
    teamChoices.remove();
    $('#teamChoices').append(teamChoices);
    teamChoices.removeClass('team-to-be-transported');
}

function transportEmailChoices() {
    var emailChoices = $('.email-to-be-transported');
    emailChoices.remove();
    $('#emails').append(emailChoices);
    emailChoices.removeAttr('class'); // the email divs have no other class
}

function bindPhotos(courseIdx) {
    $('td[id^="studentphoto-c' + courseIdx + '"]').each(function () {
        (0, _instructor.bindStudentPhotoLink)($(this).children('.profile-pic-icon-click').children('.student-profile-pic-view-link'));
    });
}

function numStudentsRetrieved() {
    var emailChoices = $('.email-to-be-transported');
    return emailChoices.length;
}

function showStudentLimitError(courseCheck, displayIcon) {
    courseCheck.prop('checked', false);
    (0, _statusMessage.setStatusMessage)(PERFORMANCE_ISSUE_MESSAGE, _const.BootstrapContextualColors.DANGER);
    displayIcon.html('');
}

function removeDataToBeTransported() {
    var sectionChoices = $('.section-to-be-transported');
    sectionChoices.remove();
    var teamChoices = $('.team-to-be-transported');
    teamChoices.remove();
    var emailChoices = $('.email-to-be-transported');
    emailChoices.remove();
}

var seeMoreRequest = function seeMoreRequest(e) {
    var $panelHeading = $(this);
    var panelCollapse = $panelHeading.parent().children('.panel-collapse');
    var toggleChevron = $panelHeading.parent().find('.glyphicon-chevron-down, .glyphicon-chevron-up');
    var panelBody = $(panelCollapse[0]).children('.panel-body');
    var displayIcon = $panelHeading.children('.display-icon');
    var courseIndex = $(panelCollapse[0]).attr('id').split('-')[1];
    var courseCheck = $('#course_check-' + courseIndex);
    var courseNumStudents = parseInt($('#numStudents-' + courseIndex).val(), 10);

    if ($panelHeading.attr('class').indexOf('ajax_submit') === -1) {
        (0, _statusMessage.clearStatusMessages)();
        if ($(panelCollapse[0]).attr('class').indexOf('checked') === -1) {
            $(panelCollapse).collapse('show');
            $(panelCollapse[0]).addClass('checked');
            $(courseCheck).prop('checked', true);
        } else {
            $(panelCollapse[0]).collapse('hide');
            $panelHeading.addClass('ajax_submit');
            $(panelBody[0]).html('');
            $(panelCollapse[0]).removeClass('checked');
            $(courseCheck).prop('checked', false);
            numStudents -= courseNumStudents;
        }
        checkCourseBinding(courseCheck);
    } else if (numStudents < STUDENT_LIMIT) {
        (0, _statusMessage.clearStatusMessages)();
        var formObject = $panelHeading.children('form');
        var courseIdx = $(formObject[0]).attr('class').split('-')[1];
        var formData = formObject.serialize();
        e.preventDefault();
        if (displayIcon.html().indexOf('img') === -1) {
            $.ajax({
                type: 'POST',
                url: $(formObject[0]).attr('action') + '?' + formData + '&courseidx=' + courseIdx,
                beforeSend: function beforeSend() {
                    displayIcon.html('<img height="25" width="25" src="/images/ajax-preload.gif">');
                },
                error: function error() {
                    var warningSign = '<span class="glyphicon glyphicon-warning-sign"></span>';
                    var errorMsg = '[ Failed to load. Click here to retry. ]';
                    errorMsg = '<strong style="margin-left: 1em; margin-right: 1em;">' + errorMsg + '</strong>';
                    displayIcon.html(warningSign + errorMsg);
                },
                success: function success(data) {
                    $(panelBody[0]).html(data);

                    // Count number of students retrieved
                    courseNumStudents = numStudentsRetrieved();
                    $('#numStudents-' + courseIdx).val(courseNumStudents);

                    // If number of students shown is already more than the limit
                    // Do not show more, even if we can retrieve it, as browser will lag.
                    if (numStudents >= STUDENT_LIMIT) {
                        showStudentLimitError(courseCheck, displayIcon);
                        removeDataToBeTransported();
                        return;
                    }

                    // Show newly retrieved students
                    numStudents += courseNumStudents;
                    transportSectionChoices();
                    transportTeamChoices();
                    transportEmailChoices();
                    bindPhotos(courseIdx);

                    $panelHeading.removeClass('ajax_submit');
                    displayIcon.html('');
                    if ($(panelCollapse[0]).attr('class').indexOf('in') === -1) {
                        $panelHeading.trigger('click');
                    }
                }
            });
        }
    } else {
        // Do not make ajax call if students shown already above limit
        showStudentLimitError(courseCheck, displayIcon);
    }

    if ($(panelCollapse).attr('class').indexOf('checked') === -1) {
        $(toggleChevron).addClass('glyphicon-chevron-down').removeClass('glyphicon-chevron-up');
    } else {
        $(toggleChevron).addClass('glyphicon-chevron-up').removeClass('glyphicon-chevron-down');
    }
};

function attachEventToSendInviteLink() {
    $('.course-student-remind-link').on('click', function (event) {
        event.preventDefault();

        var $clickedLink = $(event.currentTarget);
        var messageText = 'Usually, there is no need to use this feature because TEAMMATES sends an automatic ' + 'invite to students at the opening time of each session. Send a join request anyway?';
        var okCallback = function okCallback() {
            $.get($clickedLink.attr('href'), function () {
                var studentEmail = $clickedLink.parent().siblings("td[id|='studentemail']").html().trim();
                var message = 'An email has been sent to ' + studentEmail;
                (0, _statusMessage.setStatusMessage)(message, 'success');
            });
        };

        (0, _bootboxWrapper.showModalConfirmation)('Confirm sending join request', messageText, okCallback, null, null, null, _const.BootstrapContextualColors.INFO);
    });
}

$(document).ready(function () {
    (0, _instructor.prepareInstructorPages)();
    (0, _instructor.attachEventToDeleteStudentLink)();
    attachEventToSendInviteLink();

    $('a[id^="enroll-"]').on('click', function (e) {
        e.stopImmediatePropagation(); // not executing click event for parent elements
        window.location = $(this).attr('href');
        return false;
    });

    var panels = $('div.panel');

    bindCollapseEvents(panels);

    // Binding for "Display Archived Courses" check box.
    $('#displayArchivedCourses_check').on('change', function () {
        var urlToGo = $(window.location).attr('href');
        if (this.checked) {
            gotoUrlWithParam(urlToGo, 'displayarchive', 'true');
        } else {
            gotoUrlWithParam(urlToGo, 'displayarchive', 'false');
        }
    });

    // Binding for 'Show Emails' check box.
    $('#show_email').on('change', function () {
        var $copyEmailButton = $('#copy-email-button');
        var $emails = $('#emails');

        if (this.checked) {
            $emails.show();
            $copyEmailButton.show();
        } else {
            $emails.hide();
            $copyEmailButton.hide();
        }

        filterEmails();
    });

    // Binding for copy email button
    var copyEmailPopoverTimeout = void 0;
    $('#copy-email-button').click(function (e) {
        e.preventDefault();
        clearTimeout(copyEmailPopoverTimeout);
        var $copyEmailButton = $(this);
        var tips = 'Emails now are copied. If it doesn\'t work, you can also use <kbd>Ctrl + C</kbd> to COPY.<br>' + 'You may use <kbd>Ctrl + V</kbd> to PASTE to your email client. <br>' + '<small class="text-muted">This message will disappear in 10 seconds</small>';

        $copyEmailButton.popover('destroy').popover({
            html: true,
            trigger: 'manual',
            placement: 'top',
            content: function content() {
                return tips;
            }
        }).popover('show');

        (0, _instructor.selectElementContents)($('#emails').get(0));
        (0, _instructor.executeCopyCommand)();

        copyEmailPopoverTimeout = setTimeout(function () {
            $copyEmailButton.popover('destroy');
        }, 10000); // popover will disappear in 10 seconds
    });

    // Binding for changes in the Courses checkboxes.
    $('input[id^="course_check"]').on('change', function () {
        var courseIdx = $(this).attr('id').split('-')[1];
        var heading = $('#panelHeading-' + courseIdx);
        // Check/hide all section that is in this course
        heading.trigger('click');
    });

    // Binding for Sections checkboxes
    $(document).on('change', '.section_check', function () {
        var courseIdx = $(this).attr('id').split('-')[1];
        var sectionIdx = $(this).attr('id').split('-')[2];

        // Check/hide all teams that is in this section
        if (this.checked) {
            $('input[id^="team_check-' + courseIdx + '-' + sectionIdx + '-"]').prop('checked', true);
            $('input[id^="team_check-' + courseIdx + '-' + sectionIdx + '-"]').parent().show();
        } else {
            $('input[id^="team_check-' + courseIdx + '-' + sectionIdx + '-"]').prop('checked', false);
            $('input[id^="team_check-' + courseIdx + '-' + sectionIdx + '-"]').parent().hide();
        }

        // If none of of the sections are selected, hide the team's 'Select All' option
        if ($('input[id^="section_check"]:checked').length === 0) {
            $('#team_all').parent().hide();
            $('#show_email').parent().hide();
        } else {
            $('#team_all').parent().show();
            $('#show_email').parent().show();
        }

        // If all the currently visible sections are selected, check the "Select All" option
        checkAllSectionsSelected();

        // If all the currently visible teams are selected, check the "Select All" option
        // This is necessary here because we show/hide the teams's "Select All" previously
        checkAllTeamsSelected();

        applyFilters();
    });

    // Binding for Teams checkboxes.
    $(document).on('change', '.team_check', function () {
        if ($('input[id^="team_check"]:checked').length === 0) {
            $('#show_email').parent().hide();
        } else {
            $('#show_email').parent().show();
        }

        // If all the currently visible teams are selected, check the "Select All" option
        checkAllTeamsSelected();
        applyFilters();
    });

    // Binding for 'Select All' course option
    $('#course_all').on('change', function () {
        if (this.checked) {
            $('#section_all').prop('checked', true);
            $('#section_all').parent().show();
            $('#team_all').prop('checked', true);
            $('#team_all').parent().show();
            $('#show_email').parent().show();
            $('input[id^="course_check"]').prop('checked', true);
            $('input[id^="section_check-"]').prop('checked', true);
            $('input[id^="section_check-"]').parent().show();
            $('input[id^="team_check-"]').prop('checked', true);
            $('input[id^="team_check-"]').parent().show();
            var headings = $('.ajax_submit');
            for (var idx = 0; idx < headings.length; idx += 1) {
                setTimeout(triggerAjax, 400 * idx, headings[idx]);
            }
        } else {
            $('#section_all').prop('checked', false);
            $('#section_all').parent().hide();
            $('#team_all').prop('checked', false);
            $('#team_all').parent().hide();
            $('#show_email').parent().hide();
            $('input[id^="section_check-"]').prop('checked', false);
            $('input[id^="section_check-"]').parent().remove();
            $('input[id^="course_check"]').prop('checked', false);
            $('input[id^="team_check-"]').prop('checked', false);
            $('input[id^="team_check-"]').parent().remove();
            var heads = $('.panel-heading');
            for (var i = 0; i < heads.length; i += 1) {
                var className = $(heads[i]).attr('class');
                if (className.indexOf('ajax_submit') === -1) {
                    $(heads[i]).trigger('click');
                }
            }
        }
        applyFilters();
    });

    // Binding for "Select All" section option
    $('#section_all').on('change', function () {
        if (this.checked) {
            $('#team_all').prop('checked', true);
            $('#team_all').parent().show();
            $('#show_email').parent().show();
            $('input[id^="section_check-"]').prop('checked', true);
            $('input[id^="team_check-"]').prop('checked', true);
            $('input[id^="team_check-"]').parent().show();
        } else {
            $('#team_all').prop('checked', false);
            $('#team_all').parent().hide();
            $('#show_email').parent().hide();
            $('input[id^="section_check-"]').prop('checked', false);
            $('input[id^="team_check-"]').prop('checked', false);
            $('input[id^="team_check-"]').parent().hide();
        }
        applyFilters();
    });

    // Binding for 'Select All' team option
    $('#team_all').on('change', function () {
        $('input[id^="team_check"]:visible').prop('checked', this.checked);
        applyFilters();
    });

    // Pre-sort each table
    $('th[id^="button_sortsection-"]').each(function () {
        (0, _sortBy.toggleSort)($(this));
    });

    $('th[id^="button_sortteam-"]').each(function () {
        var col = $(this).parent().children().index($(this));
        if (col === 0) {
            (0, _sortBy.toggleSort)($(this));
        }
    });

    $('.ajax_submit').click(seeMoreRequest);
});
//# sourceMappingURL=instructorStudentList.js.map