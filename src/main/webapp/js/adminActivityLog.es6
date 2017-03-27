/* global
setStatusMessage:false StatusType:false bindBackToTopButtons:false addLoadingIndicator:false removeLoadingIndicator:false
*/

$(document).ready(() => {
    $('#filterReference').toggle();
    bindBackToTopButtons('.back-to-top-left, .back-to-top-right');
    highlightKeywordsInLogMessages();

    $('#toggleReferenceLink').on('click', (e) => {
        e.preventDefault();
        toggleReference();
    });

    $('#button_older').on('click', (e) => {
        e.preventDefault();
        const nextEndTime = $(e.target).data('nextEndTime');
        getOlderLogEntriesByAjax(nextEndTime);
    });

    $('#activity-logs-table .logEntryTimestamp').on('click', function (e) {
        e.preventDefault();
        const data = $(e.target).data();
        convertLogTimestampToAdminTimezone(data.time, data.googleId, data.role, this);
    });
});

function toggleReference() {
    $('#filterReference').toggle('slow');

    const button = $('#detailButton').attr('class');

    if (button === 'glyphicon glyphicon-chevron-down') {
        $('#detailButton').attr('class', 'glyphicon glyphicon-chevron-up');
        $('#referenceText').text('Hide Reference');
    } else {
        $('#detailButton').attr('class', 'glyphicon glyphicon-chevron-down');
        $('#referenceText').text('Show Reference');
    }
}

/**
 * Display a log entry's timestamp in admin's timezone, with timezone conversion via ajax.
 *
 * @param {int} time of the log entry as seconds since epoch
 * @param {String} googleId of the logged in user
 * @param {String} role of the logged in user
 * @param {JQuery} entry link the user clicks to perform the timezone conversion
 */
function convertLogTimestampToAdminTimezone(time, googleId, role, entry) {
    const params = `logTimeInAdminTimeZone=${time
                  }&logRole=${role
                  }&logGoogleId=${googleId}`;

    const link = $(entry);
    const localTimeDisplay = $(entry).parent().children()[1];

    const originalTime = $(link).html();

    $.ajax({
        type: 'POST',
        url: `/admin/adminActivityLogPage?${params}`,
        beforeSend() {
            $(localTimeDisplay).html("<img src='/images/ajax-loader.gif'/>");
        },
        error() {
            $(localTimeDisplay).html('Loading error, please retry');
        },
        success(data) {
            if (data.isError) {
                $(localTimeDisplay).html('Loading error, please retry');
            } else {
                $(link).parent().html(`${originalTime}<mark><br>${data.logLocalTime}</mark>`);
            }

            setStatusMessage(data.statusForAjax, StatusType.INFO);
        },
    });
}

/**
 * Updates the page with older log entries matching the query via ajax.
 *
 * @param {int} searchTimeOffset
 * @param {bool} shouldNewEntriesHaveKeywordsHighlighted whether older log entries should have keywords highlighted
 */
function getOlderLogEntriesByAjax(searchTimeOffset) {
    $('input[name=searchTimeOffset]').val(searchTimeOffset);

    const formObject = $('#ajaxLoaderDataForm');
    const formData = formObject.serialize();
    const $button = $('#button_older');
    const $logsTable = $('#activity-logs-table > tbody');

    $.ajax({
        type: 'POST',
        url: `/admin/adminActivityLogPage?${formData}`,
        beforeSend() {
            addLoadingIndicator($button, '');
        },
        error() {
            setFormErrorMessage($button, 'Failed to load older logs. Please try again.');
            removeLoadingIndicator($button, 'Retry');
        },
        success(data) {
            const $data = $(data);
            $logsTable.append($data.find('#activity-logs-table > tbody').html());
            updateInfoForRecentActionButton();
            highlightKeywordsInLogMessages();

            setStatusMessage($data.find('#status-message').html(), StatusType.INFO);
        },
    });
}

function setFormErrorMessage(button, msg) {
    button.after(`&nbsp;&nbsp;&nbsp;${msg}`);
}

function updateInfoForRecentActionButton() {
    const isShowAll = $('#ifShowAll').val();
    $('.ifShowAll_for_person').val(isShowAll);

    const isShowTestData = $('#ifShowTestData').val();
    $('.ifShowTestData_for_person').val(isShowTestData);
}

/**
 * Highlights default/search keywords in log messages.
 */
function highlightKeywordsInLogMessages() {
    const allLogMessages = $('.log-message');
    // highlight search keywords
    const searchKeywords = $('#query-keywords-for-info').val();
    const searchKeywordsList = searchKeywords.split(',');
    allLogMessages.highlight(searchKeywordsList);

    // highlight default keywords
    const defaultKeywords = $('#query-keywords-default-for-info').val();
    const defaultKeywordsList = defaultKeywords.split(',');
    allLogMessages.highlight(defaultKeywordsList, {
        element: 'b',
        className: ' ',
    });
}

/*
export default {
    toggleReference,
    submitLocalTimeAjaxRequest,
    submitFormAjax,
};
*/
