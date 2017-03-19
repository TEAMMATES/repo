/* global attachEventToDeleteStudentLink:false selectElementContents:false executeCopyCommand:false */
/* global toggleSort:false match:false */

$(document).ready(() => {
    attachEventToDeleteStudentLink();

    $('a[id^="enroll-"]').on('click', function (e) {
        e.stopImmediatePropagation(); // not executing click event for parent elements
        window.location = $(this).attr('href');
        return false;
    });

    const panels = $('div.panel');

    bindCollapseEvents(panels);

    // Binding for "Display Archived Courses" check box.
    $('#displayArchivedCourses_check').on('change', function () {
        const urlToGo = $(window.location).attr('href');
        if (this.checked) {
            gotoUrlWithParam(urlToGo, 'displayarchive', 'true');
        } else {
            gotoUrlWithParam(urlToGo, 'displayarchive', 'false');
        }
    });

    // Binding for 'Show Emails' check box.
    $('#show_email').on('change', function () {
        const $copyEmailButton = $('#copy-email-button');
        const $emails = $('#emails');

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
    let copyEmailPopoverTimeout;
    $('#copy-email-button').click(function (e) {
        e.preventDefault();
        clearTimeout(copyEmailPopoverTimeout);
        const $copyEmailButton = $(this);
        const tips = 'Emails now are copied. If it doesn\'t work, you can also use <kbd>Ctrl + C</kbd> to COPY.<br>'
                   + 'You may use <kbd>Ctrl + V</kbd> to PASTE to your email client. <br>'
                   + '<small class="text-muted">This message will disappear in 10 seconds</small>';

        $copyEmailButton
            .popover('destroy')
            .popover({
                html: true,
                trigger: 'manual',
                placement: 'top',
                content() {
                    return tips;
                },
            })
            .popover('show');

        selectElementContents($('#emails').get(0));
        executeCopyCommand();

        copyEmailPopoverTimeout = setTimeout(() => {
            $copyEmailButton.popover('destroy');
        }, 10000); // popover will disappear in 10 seconds
    });

    // Binding for changes in the Courses checkboxes.
    $('input[id^="course_check"]').on('change', function () {
        const courseIdx = $(this).attr('id').split('-')[1];
        const heading = $(`#panelHeading-${courseIdx}`);
        // Check/hide all section that is in this course
        heading.trigger('click');
    });

    // Binding for Sections checkboxes
    $(document).on('change', '.section_check', function () {
        const courseIdx = $(this).attr('id').split('-')[1];
        const sectionIdx = $(this).attr('id').split('-')[2];

        // Check/hide all teams that is in this section
        if (this.checked) {
            $(`input[id^="team_check-${courseIdx}-${sectionIdx}-"]`).prop('checked', true);
            $(`input[id^="team_check-${courseIdx}-${sectionIdx}-"]`).parent().show();
        } else {
            $(`input[id^="team_check-${courseIdx}-${sectionIdx}-"]`).prop('checked', false);
            $(`input[id^="team_check-${courseIdx}-${sectionIdx}-"]`).parent().hide();
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
    $(document).on('change', '.team_check', () => {
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
            const headings = $('.ajax_submit');
            for (let idx = 0; idx < headings.length; idx += 1) {
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
            const heads = $('.panel-heading');
            for (let i = 0; i < heads.length; i += 1) {
                const className = $(heads[i]).attr('class');
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
        toggleSort($(this));
    });

    $('th[id^="button_sortteam-"]').each(function () {
        const col = $(this).parent().children().index($(this));
        if (col === 0) {
            toggleSort($(this));
        }
    });
});

// Trigger ajax request for a course through clicking the heading
function triggerAjax(e) {
    $(e).trigger('click');
}

// Binding check for course selection
function checkCourseBinding(e) {
    const courseIdx = $(e).attr('id').split('-')[1];

    // Check/hide all section that is in this course
    if ($(e).prop('checked')) {
        $(`input[id^="section_check-${courseIdx}-"]`).prop('checked', true);
        $(`input[id^="section_check-${courseIdx}-"]`).parent().show();
        $(`input[id^="team_check-${courseIdx}-"]`).prop('checked', true);
        $(`input[id^="team_check-${courseIdx}-"]`).parent().show();
    } else {
        $(`input[id^="section_check-${courseIdx}-"]`).prop('checked', false);
        $(`input[id^="section_check-${courseIdx}-"]`).parent().remove();
        $(`input[id^="team_check-${courseIdx}-"]`).prop('checked', false);
        $(`input[id^="team_check-${courseIdx}-"]`).parent().remove();
        $(`div[id^="student_email-c${courseIdx}"]`).remove();
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
 * Go to the url with appended param and value pair
 */
function gotoUrlWithParam(url, param, value) {
    const paramValuePair = `${param}=${value}`;
    if (!url.includes('?')) {
        window.location.href = `${url}?${paramValuePair}`;
    } else if (!url.includes(param)) {
        window.location.href = `${url}&${paramValuePair}`;
    } else if (url.includes(paramValuePair)) {
        window.location.href = url;
    } else {
        const urlWithoutParam = removeParamInUrl(url, param);
        gotoUrlWithParam(urlWithoutParam, param, value);
    }
}

/**
 * Remove param and its value pair in the given url
 * Return the url withour param and value pair
 */
function removeParamInUrl(url, param) {
    let indexOfParam = url.indexOf(`?${param}`);
    indexOfParam = indexOfParam === -1 ? url.indexOf(`&${param}`) : indexOfParam;
    const indexOfAndSign = url.indexOf('&', indexOfParam + 1);
    const urlBeforeParam = url.substr(0, indexOfParam);
    const urlAfterParamValue = indexOfAndSign === -1 ? '' : url.substr(indexOfAndSign);
    return urlBeforeParam + urlAfterParamValue;
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

/**
 * Hide sections that are not selected
 */
function filterSection() {
    $('input[id^="section_check"]').each(function () {
        const courseIdx = $(this).attr('id').split('-')[1];
        const sectionIdx = $(this).attr('id').split('-')[2];
        if (this.checked) {
            $(`#studentsection-c${courseIdx}\\.${sectionIdx}`).show();
        } else {
            $(`#studentsection-c${courseIdx}\\.${sectionIdx}`).hide();
        }
    });
}

/**
 * Hide teams that are not selected
 */
function filterTeam() {
    $('input[id^="team_check"]').each(function () {
        const courseIdx = $(this).attr('id').split('-')[1];
        const sectionIdx = $(this).attr('id').split('-')[2];
        const teamIdx = $(this).attr('id').split('-')[3];
        if (this.checked) {
            $(`#studentteam-c${courseIdx}\\.${sectionIdx}\\.${teamIdx}`).parent().show();
        } else {
            $(`#studentteam-c${courseIdx}\\.${sectionIdx}\\.${teamIdx}`).parent().hide();
        }
    });
}

/**
 * Hide student email view based on search key
 * Uses the hidden attributes of the student_row inside dataTable
 */
function filterEmails() {
    const uniqueEmails = {};
    $('tr[id^="student-c"]').each(function () {
        const elementId = $(this).attr('id');
        const studentId = elementId.split('-')[1];
        const emailElement = $(`#student_email-${studentId.replace('.', '\\.')}`);
        const emailText = emailElement.text();
        if ($(this).is(':hidden') || uniqueEmails[emailText]) {
            emailElement.hide();
        } else {
            uniqueEmails[emailText] = true;
            emailElement.show();
        }
    });
}

/**
 * Custom function containsIN, for case insensitive matching
 * TODO: expand to fuzzy search
 */
$.extend($.expr[':'], {
    containsIN(elem) {
        return (elem.textContent || elem.innerText || '').toLowerCase().indexOf((match[3] || '').toLowerCase()) >= 0;
    },
});

function bindCollapseEvents(panels) {
    let numPanels = -1;
    for (let i = 0; i < panels.length; i += 1) {
        const heading = $(panels[i]).children('.panel-heading');
        const bodyCollapse = $(panels[i]).children('.panel-collapse');
        if (heading.length !== 0 && bodyCollapse.length !== 0) {
            numPanels += 1;
            $(heading[0]).attr('data-target', `#panelBodyCollapse-${numPanels}`);
            $(heading[0]).attr('id', `panelHeading-${numPanels}`);
            $(heading[0]).css('cursor', 'pointer');
            $(bodyCollapse[0]).attr('id', `panelBodyCollapse-${numPanels}`);
        }
    }
}
