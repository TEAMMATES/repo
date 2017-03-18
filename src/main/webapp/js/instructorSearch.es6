/* global highlightSearchResult:false */

'use strict';

$(document).ready(() => {
    $('.comments > .list-group-item').hover(function () {
        $("a[type='button']", this).show();
    }, function () {
        $("a[type='button']", this).hide();
    });

    // highlight search string
    highlightSearchResult('#searchBox', '.panel-body');
});
