$(document).ready(function() {
    
    $('a[id^="enroll-"]').on('click', function(e) {
        e.stopImmediatePropagation(); // not executing click event for parent elements
        window.location = $(this).attr('href');
        return false;
    });

    var panels = $('div.panel');

    bindCollapseEvents(panels);

    // Binding for "Display Archived Courses" check box.
    $('#displayArchivedCourses-check').on('change', function() {
        var urlToGo = $(location).attr('href');
        if (this.checked) {
            gotoUrlWithParam(urlToGo, 'displayarchive', 'true');
        } else {
            gotoUrlWithParam(urlToGo, 'displayarchive', 'false');
        }
    });

    // Binding for 'Show Emails' check box.
    $('#show-email').on('change', function() {
        if (this.checked) {
            $('#emails').show();
        } else {
            $('#emails').hide();
        }
        filterEmails();
    });

    // Binding for changes in the Courses checkboxes.
    $('input[id^="course-check"]').on('change', function() {
        var courseIdx = $(this).attr('id').split('-')[2];
        var heading = $('#panelHeading-' + courseIdx);
        // Check/hide all section that is in this course
        heading.trigger('click');
    });

    // Binding for Sections checkboxes
    $(document).on('change', '.section-check', function() {
        var courseIdx = $(this).attr('id').split('-')[2];
        var sectionIdx = $(this).attr('id').split('-')[3];

        // Check/hide all teams that is in this section
        if (this.checked) {
            $('input[id^="team-check-' + courseIdx + '-' + sectionIdx + '-"]').prop('checked', true);
            $('input[id^="team-check-' + courseIdx + '-' + sectionIdx + '-"]').parent().show();
        } else {
            $('input[id^="team-check-' + courseIdx + '-' + sectionIdx + '-"]').prop('checked', false);
            $('input[id^="team-check-' + courseIdx + '-' + sectionIdx + '-"]').parent().hide();
        }

        // If none of of the sections are selected, hide the team's 'Select All' option
        if ($('input[id^="section-check"]:checked').length === 0) {
            $('#team-all').parent().hide();
            $('#show-email').parent().hide();
        } else {
            $('#team-all').parent().show();
            $('#show-email').parent().show();
        }

        // If all the currently visible sections are selected, check the "Select All" option
        checkAllSectionsSelected();

        // If all the currently visible teams are selected, check the "Select All" option
        // This is necessary here because we show/hide the teams's "Select All" previously
        checkAllTeamsSelected();

        applyFilters();
    });

    // Binding for Teams checkboxes.
    $(document).on('change', '.team-check', function() {
        if ($('input[id^="team-check"]:checked').length === 0) {
            $('#show-email').parent().hide();
        } else {
            $('#show-email').parent().show();
        }

        // If all the currently visible teams are selected, check the "Select All" option
        checkAllTeamsSelected();
        applyFilters();
    });

    // Binding for 'Select All' course option
    $('#course-all').on('change', function() {
        if (this.checked) {
            $('#section-all').prop('checked', true);
            $('#section-all').parent().show();
            $('#team-all').prop('checked', true);
            $('#team-all').parent().show();
            $('#show-email').parent().show();
            $('input[id^="course-check"]').prop('checked', true);
            $('input[id^="section-check-"]').prop('checked', true);
            $('input[id^="section-check-"]').parent().show();
            $('input[id^="team-check-"]').prop('checked', true);
            $('input[id^="team-check-"]').parent().show();
            var headings = $('.ajax-submit');
            for (var idx = 0; idx < headings.length; idx++) {
                setTimeout(triggerAjax, 400 * idx, headings[idx]);
            }
        } else {
            $('#section-all').prop('checked', false);
            $('#section-all').parent().hide();
            $('#team-all').prop('checked', false);
            $('#team-all').parent().hide();
            $('#show-email').parent().hide();
            $('input[id^="section-check-"]').prop('checked', false);
            $('input[id^="section-check-"]').parent().remove();
            $('input[id^="course-check"]').prop('checked', false);
            $('input[id^="team-check-"]').prop('checked', false);
            $('input[id^="team-check-"]').parent().remove();
            var heads = $('.panel-heading');
            for (var i = 0; i < heads.length; i++) {
                var className = $(heads[i]).attr('class');
                if (className.indexOf('ajax-submit') === -1) {
                    $(heads[i]).trigger('click');
                }
            }
        }
        applyFilters();
    });

    // Binding for "Select All" section option
    $('#section-all').on('change', function() {
        if (this.checked) {
            $('#team-all').prop('checked', true);
            $('#team-all').parent().show();
            $('#show-email').parent().show();
            $('input[id^="section-check-"]').prop('checked', true);
            $('input[id^="team-check-"]').prop('checked', true);
            $('input[id^="team-check-"]').parent().show();
        } else {
            $('#team-all').prop('checked', false);
            $('#team-all').parent().hide();
            $('#show-email').parent().hide();
            $('input[id^="section-check-"]').prop('checked', false);
            $('input[id^="team-check-"]').prop('checked', false);
            $('input[id^="team-check-"]').parent().hide();
        }
        applyFilters();
    });

    // Binding for 'Select All' team option
    $('#team-all').on('change', function() {
        $('input[id^="team-check"]:visible').prop('checked', this.checked);
        applyFilters();
    });

    // Pre-sort each table
    $('th[id^="button-sortsection-"]').each(function() {
        toggleSort($(this));
    });

    $('th[id^="button-sortteam-"]').each(function() {
        var col = $(this).parent().children().index($(this));
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
    var courseIdx = $(e).attr('id').split('-')[2];

    // Check/hide all section that is in this course
    if ($(e).prop('checked')) {
        $('input[id^="section-check-' + courseIdx + '-"]').prop('checked', true);
        $('input[id^="section-check-' + courseIdx + '-"]').parent().show();
        $('input[id^="team-check-' + courseIdx + '-"]').prop('checked', true);
        $('input[id^="team-check-' + courseIdx + '-"]').parent().show();
    } else {
        $('input[id^="section-check-' + courseIdx + '-"]').prop('checked', false);
        $('input[id^="section-check-' + courseIdx + '-"]').parent().remove();
        $('input[id^="team-check-' + courseIdx + '-"]').prop('checked', false);
        $('input[id^="team-check-' + courseIdx + '-"]').parent().remove();
        $('div[id^="student-email-c' + courseIdx + '"]').remove();
    }
    
    // If all the courses are selected, check the 'Select All' option
    if ($('input[id^="course-check"]:checked').length === $('input[id^="course-check"]').length) {
        $('#course-all').prop('checked', true);
    } else {
        $('#course-all').prop('checked', false);
    }
    
    // If none of of the courses are selected, hide the section"s 'Select All' option
    if ($('input[id^="course-check"]:checked').length === 0) {
        $('#section-all').parent().hide();
        $('#team-all').parent().hide();
        $('#show-email').parent().hide();
    } else {
        $('#section-all').parent().show();
        $('#team-all').parent().show();
        $('#show-email').parent().show();
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
    if ($('input[id^="section-check"]:visible:checked').length === $('input[id^="section-check"]:visible').length) {
        $('#section-all').prop('checked', true);
    } else {
        $('#section-all').prop('checked', false);
    }
}

/**
 * Check if all available teams are selected
 */
function checkAllTeamsSelected() {
    if ($('input[id^="team-check"]:visible:checked').length === $('input[id^="team-check"]:visible').length) {
        $('#team-all').prop('checked', true);
    } else {
        $('#team-all').prop('checked', false);
    }
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
    $('input[id^="section-check"]').each(function() {
        var courseIdx = $(this).attr('id').split('-')[2];
        var sectionIdx = $(this).attr('id').split('-')[3];
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
    $('input[id^="team-check"]').each(function() {
        var courseIdx = $(this).attr('id').split('-')[2];
        var sectionIdx = $(this).attr('id').split('-')[3];
        var teamIdx = $(this).attr('id').split('-')[4];
        if (this.checked) {
            $('#studentteam-c' + courseIdx + '\\.' + sectionIdx + '\\.' + teamIdx).parent().show();
        } else {
            $('#studentteam-c' + courseIdx + '\\.' + sectionIdx + '\\.' + teamIdx).parent().hide();
        }
    });
}

/**
 * Hide student email view based on search key
 * Uses the hidden attributes of the student-row inside dataTable
 */
function filterEmails() {
    var uniqueEmails = {};
    $('tr[id^="student-c"]').each(function() {
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
 * Custom function containsIN, for case insensitive matching
 * TODO: expand to fuzzy search
 */
$.extend($.expr[':'], {
    containsIN: function(elem) {
        return (elem.textContent || elem.innerText || '').toLowerCase().indexOf((match[3] || '').toLowerCase()) >= 0;
    }
});

function bindCollapseEvents(panels) {
    var numPanels = -1;
    for (var i = 0; i < panels.length; i++) {
        var heading = $(panels[i]).children('.panel-heading');
        var bodyCollapse = $(panels[i]).children('.panel-collapse');
        if (heading.length !== 0 && bodyCollapse.length !== 0) {
            numPanels++;
            $(heading[0]).attr('data-target', '#panelBodyCollapse-' + numPanels);
            $(heading[0]).attr('id', 'panelHeading-' + numPanels);
            $(heading[0]).css('cursor', 'pointer');
            $(bodyCollapse[0]).attr('id', 'panelBodyCollapse-' + numPanels);
        }
    }
}
