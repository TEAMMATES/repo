$(document).ready(() => {
    $('#ajaxForCourses').trigger('submit');
    if (typeof moment !== 'undefined') {
        const $selectElement = $(`#${COURSE_TIME_ZONE}`);
        TimeZone.prepareTimeZoneInput($selectElement);
        TimeZone.autoDetectAndUpdateTimeZone($selectElement);
    }
});
