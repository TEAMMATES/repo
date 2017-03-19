'use strict';

QUnit.module('datepicker.js');

QUnit.test('triggerDatepickerOnClick(datepickerDivs)', function(assert) {
    assert.expect(1);

    $('#date-picker-div').datepicker();
    triggerDatepickerOnClick([$('#date-picker-div')]);
    $('#date-picker-div').click();

    assert.equal($('.ui-datepicker-calendar:visible').length, 1, 'Displays datepickers after trigger');
});

QUnit.test('getMaxDateForVisibleDate(startDate, publishDate)', function(assert) {
    assert.expect(5);

    var startDate = new Date(2017, 3, 19, 2, 31, 0, 0);
    var publishDate = new Date(2017, 3, 19, 2, 30, 0, 0);

    assert.equal(getMaxDateForVisibleDate(startDate, null), startDate,
            'Returns startDate when publishDate is null');
    assert.equal(getMaxDateForVisibleDate(startDate, undefined), startDate,
            'Returns startDate when publishDate is undefined');
    assert.equal(getMaxDateForVisibleDate(startDate, publishDate), publishDate,
            'Returns publishDate when startDate > publishDate');
    assert.equal(getMaxDateForVisibleDate(startDate, startDate), startDate,
            'Returns startDate when startDate = publishDate');
    assert.equal(getMaxDateForVisibleDate(publishDate, startDate), publishDate,
            'Returns startDate when startDate < publishDate');
});

QUnit.test('getMinDateForPublishDate(visibleDate)', function(assert) {
    assert.expect(1);

    var visibleDate = new Date(2017, 3, 19, 2, 31, 0, 0);

    assert.equal(getMinDateForPublishDate(visibleDate), visibleDate, 'Returns visibleDate');
});
